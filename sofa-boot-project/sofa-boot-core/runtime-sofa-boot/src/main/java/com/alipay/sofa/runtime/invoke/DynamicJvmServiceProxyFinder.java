/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.runtime.invoke;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alipay.sofa.ark.spi.replay.ReplayContext;
import com.alipay.sofa.common.utils.StringUtil;
import com.alipay.sofa.runtime.SofaRuntimeProperties;
import org.aopalliance.intercept.MethodInvocation;

import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.model.BizState;
import com.alipay.sofa.ark.spi.service.ArkInject;
import com.alipay.sofa.ark.spi.service.biz.BizManagerService;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.log.SofaLogger;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.service.ServiceProxy;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class DynamicJvmServiceProxyFinder {

    private static DynamicJvmServiceProxyFinder         dynamicJvmServiceProxyFinder = new DynamicJvmServiceProxyFinder();

    private static Map<String, JvmServiceTargetHabitat> jvmServiceTargetHabitats     = new ConcurrentHashMap<>();

    private DynamicJvmServiceProxyFinder() {
    }

    @ArkInject
    private BizManagerService bizManagerService;

    private boolean           hasFinishStartup = false;

    public static DynamicJvmServiceProxyFinder getDynamicJvmServiceProxyFinder() {
        return dynamicJvmServiceProxyFinder;
    }

    public ServiceComponent findServiceComponent(ClassLoader clientClassloader, Contract contract) {
        ServiceComponent serviceComponent = null;
        if (hasFinishStartup && SofaRuntimeProperties.isDynamicJvmServiceCacheEnable()) {
            serviceComponent = cacheSearching(contract);
            if (serviceComponent != null) {
                return serviceComponent;
            }
        }

        String interfaceType = contract.getInterfaceType().getCanonicalName();
        String uniqueId = contract.getUniqueId();
        for (SofaRuntimeManager sofaRuntimeManager : SofaFramework.getRuntimeSet()) {
            if (sofaRuntimeManager.getAppClassLoader().equals(clientClassloader)) {
                continue;
            }

            String version = ReplayContext.get();

            if (ReplayContext.PLACEHOLDER.equals(version)) {
                version = null;
            }

            Biz biz = getBiz(sofaRuntimeManager);
            // if null , check next
            if (biz == null) {
                continue;
            }

            // do not match state, check next
            // https://github.com/sofastack/sofa-boot/issues/532
            if (hasFinishStartup && biz.getBizState() != BizState.DEACTIVATED
                && biz.getBizState() != BizState.ACTIVATED) {
                continue;
            }

            // if specified version , but version do not match ,check next
            if (version != null && !version.equals(biz.getBizVersion())) {
                continue;
            }

            // if not specified version, but state do not match, check next
            // https://github.com/sofastack/sofa-boot/issues/532
            if (hasFinishStartup && version == null && biz.getBizState() != BizState.ACTIVATED) {
                continue;
            }

            // match biz
            serviceComponent = findServiceComponent(uniqueId, interfaceType,
                sofaRuntimeManager.getComponentManager());
            if (serviceComponent != null) {
                return serviceComponent;
            }
        }
        return null;
    }

    public void afterBizStartup(Biz biz) {
        if (!SofaRuntimeProperties.isDynamicJvmServiceCacheEnable()) {
            return;
        }

        // Currently, there is no way to get SOFA Runtime Manager from biz
        // The overhead is acceptable as this only happens after biz's successful installation
        for (SofaRuntimeManager runtimeManager: SofaFramework.getRuntimeSet()) {
            if (runtimeManager.getAppClassLoader().equals(biz.getBizClassLoader())) {
                for (ComponentInfo componentInfo: runtimeManager.getComponentManager().getComponents()) {
                    if (componentInfo instanceof ServiceComponent) {
                        ServiceComponent serviceComponent = (ServiceComponent) componentInfo;
                        String uniqueName = getUniqueName(serviceComponent.getService());
                        jvmServiceTargetHabitats.computeIfAbsent(uniqueName, e -> new JvmServiceTargetHabitat(biz.getBizName()));
                        JvmServiceTargetHabitat jvmServiceTargetHabitat = jvmServiceTargetHabitats.get(uniqueName);
                        jvmServiceTargetHabitat.addServiceComponent(biz.getBizVersion(), serviceComponent);
                    }
                }
            }
        }
    }

    public void afterBizUninstall(Biz biz) {
        if (!SofaRuntimeProperties.isDynamicJvmServiceCacheEnable()) {
            return;
        }

        for (SofaRuntimeManager runtimeManager : SofaFramework.getRuntimeSet()) {
            if (runtimeManager.getAppClassLoader().equals(biz.getBizClassLoader())) {
                for (ComponentInfo componentInfo : runtimeManager.getComponentManager()
                    .getComponents()) {
                    if (componentInfo instanceof ServiceComponent) {
                        ServiceComponent serviceComponent = (ServiceComponent) componentInfo;
                        String uniqueName = getUniqueName(serviceComponent.getService());
                        JvmServiceTargetHabitat jvmServiceTargetHabitat = jvmServiceTargetHabitats
                            .get(uniqueName);
                        if (jvmServiceTargetHabitat != null) {
                            jvmServiceTargetHabitat.removeServiceComponent(biz.getBizVersion());
                        }
                    }
                }
            }
        }
    }

    private ServiceComponent cacheSearching(Contract contract) {
        // Master Biz is in starting phase, cache isn't ready
        if (!hasFinishStartup) {
            return null;
        }

        String uniqueName = getUniqueName(contract);
        JvmServiceTargetHabitat jvmServiceTargetHabitat = jvmServiceTargetHabitats.get(uniqueName);
        if (jvmServiceTargetHabitat == null) {
            return null;
        }

        String version = ReplayContext.get();
        version = ReplayContext.PLACEHOLDER.equals(version) ? null : version;
        if (StringUtil.isNotBlank(version)) {
            return jvmServiceTargetHabitat.getServiceComponent(version);
        }
        return jvmServiceTargetHabitat.getDefaultServiceComponent();
    }

    private String getUniqueName(Contract contract) {
        String uniqueName = contract.getInterfaceType().getName();
        if (StringUtil.isNotBlank(contract.getUniqueId())) {
            uniqueName += ":" + contract.getUniqueId();
        }
        return uniqueName;
    }

    public ServiceProxy findServiceProxy(ClassLoader clientClassloader, Contract contract) {
        ServiceComponent serviceComponent = findServiceComponent(clientClassloader, contract);
        if (serviceComponent == null) {
            return null;
        }

        SofaRuntimeManager sofaRuntimeManager = serviceComponent.getContext()
            .getSofaRuntimeManager();
        Biz biz = getBiz(sofaRuntimeManager);

        if (biz == null) {
            return null;
        }

        JvmBinding referenceJvmBinding = (JvmBinding) contract
            .getBinding(JvmBinding.JVM_BINDING_TYPE);
        JvmBinding serviceJvmBinding = (JvmBinding) serviceComponent.getService().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        boolean serialize;
        if (serviceJvmBinding != null) {
            serialize = referenceJvmBinding.getJvmBindingParam().isSerialize()
                        || serviceJvmBinding.getJvmBindingParam().isSerialize();
        } else {
            // Service provider don't intend to publish JVM service, serialize is considered to be true in this case
            serialize = true;
        }
        return new DynamicJvmServiceInvoker(clientClassloader,
            sofaRuntimeManager.getAppClassLoader(), serviceComponent.getService().getTarget(),
            contract, biz.getIdentity(), serialize);
    }

    /**
     * Find corresponding {@link ServiceComponent} in specified {@link ComponentManager}
     *
     * @param uniqueId
     * @param interfaceType
     * @param componentManager
     * @return
     */
    private ServiceComponent findServiceComponent(String uniqueId, String interfaceType,
                                                  ComponentManager componentManager) {
        Collection<ComponentInfo> components = componentManager
            .getComponentInfosByType(ServiceComponent.SERVICE_COMPONENT_TYPE);
        for (ComponentInfo c : components) {
            ServiceComponent component = (ServiceComponent) c;
            Contract serviceContract = component.getService();
            if (serviceContract.getInterfaceType().getCanonicalName().equals(interfaceType)
                && uniqueId.equals(serviceContract.getUniqueId())) {
                return component;
            }
        }
        return null;
    }

    /**
     * Get Biz {@link Biz} according to SofaRuntimeManager {@link SofaRuntimeManager}
     *
     * @param sofaRuntimeManager
     * @return
     */
    public static Biz getBiz(SofaRuntimeManager sofaRuntimeManager) {
        if (getDynamicJvmServiceProxyFinder().bizManagerService == null) {
            return null;
        }

        for (Biz biz : getDynamicJvmServiceProxyFinder().bizManagerService.getBizInOrder()) {
            if (sofaRuntimeManager.getAppClassLoader().equals(biz.getBizClassLoader())) {
                return biz;
            }
        }
        return null;
    }

    public void setHasFinishStartup(boolean hasFinishStartup) {
        this.hasFinishStartup = hasFinishStartup;
    }

    static class DynamicJvmServiceInvoker extends ServiceProxy {

        private Contract                 contract;
        private Object                   targetService;
        private String                   bizIdentity;
        private ThreadLocal<ClassLoader> clientClassloader = new ThreadLocal<>();
        private boolean                  serialize;

        static protected final String    TOSTRING_METHOD   = "toString";
        static protected final String    EQUALS_METHOD     = "equals";
        static protected final String    HASHCODE_METHOD   = "hashCode";

        public DynamicJvmServiceInvoker(ClassLoader clientClassloader,
                                        ClassLoader serviceClassLoader, Object targetService,
                                        Contract contract, String bizIdentity, boolean serialize) {
            super(serviceClassLoader);
            this.clientClassloader.set(clientClassloader);
            this.targetService = targetService;
            this.contract = contract;
            this.bizIdentity = bizIdentity;
            this.serialize = serialize;
        }

        @Override
        protected Object doInvoke(MethodInvocation invocation) throws Throwable {
            try {

                if (SofaLogger.isDebugEnabled()) {
                    SofaLogger
                        .debug(">> Start in Cross App JVM service invoke, the service interface is  - "
                               + getInterfaceType());
                }

                if (getDynamicJvmServiceProxyFinder().bizManagerService != null) {
                    ReplayContext.setPlaceHolder();
                }

                Method targetMethod = invocation.getMethod();
                Object[] targetArguments = invocation.getArguments();

                if (TOSTRING_METHOD.equalsIgnoreCase(targetMethod.getName())
                    && targetMethod.getParameterTypes().length == 0) {
                    return targetService.toString();
                } else if (EQUALS_METHOD.equalsIgnoreCase(targetMethod.getName())
                           && targetMethod.getParameterTypes().length == 1) {
                    return targetService.equals(targetArguments[0]);
                } else if (HASHCODE_METHOD.equalsIgnoreCase(targetMethod.getName())
                           && targetMethod.getParameterTypes().length == 0) {
                    return targetService.hashCode();
                }

                Class[] oldArgumentTypes = targetMethod.getParameterTypes();
                Method transformMethod;
                // check whether skip serialize or not
                if (!serialize || SofaRuntimeProperties.isSkipJvmSerialize(clientClassloader.get())) {
                    ClassLoader tcl = Thread.currentThread().getContextClassLoader();
                    try {
                        pushThreadContextClassLoader(getServiceClassLoader());
                        transformMethod = getTargetMethod(targetMethod, oldArgumentTypes);
                        return transformMethod.invoke(targetService, targetArguments);
                    } finally {
                        pushThreadContextClassLoader(tcl);
                    }
                } else {
                    Object[] arguments = (Object[]) hessianTransport(targetArguments, null);
                    Class[] argumentTypes = (Class[]) hessianTransport(oldArgumentTypes, null);
                    transformMethod = getTargetMethod(targetMethod, argumentTypes);
                    Object retVal = transformMethod.invoke(targetService, arguments);
                    return hessianTransport(retVal, getClientClassloader());
                }
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            } finally {
                if (getDynamicJvmServiceProxyFinder().bizManagerService != null) {
                    ReplayContext.clearPlaceHolder();
                }
                setClientClassloader(null);
            }
        }

        @Override
        protected void doCatch(MethodInvocation invocation, Throwable e, long startTime) {
            if (SofaLogger.isDebugEnabled()) {
                SofaLogger.debug(getCommonInvocationLog("Exception", invocation, startTime));
            }
        }

        @Override
        protected void doFinally(MethodInvocation invocation, long startTime) {
            if (SofaLogger.isDebugEnabled()) {
                SofaLogger.debug(getCommonInvocationLog("Finally", invocation, startTime));
            }
        }

        private Class getInterfaceType() {
            return contract.getInterfaceType();
        }

        public ClassLoader getClientClassloader() {
            return clientClassloader.get();
        }

        public void setClientClassloader(ClassLoader clientClassloader) {
            this.clientClassloader.set(clientClassloader);
        }

        private Method getTargetMethod(Method method, Class[] argumentTypes) {
            try {
                return targetService.getClass().getMethod(method.getName(), argumentTypes);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException(targetService + " in " + bizIdentity
                                                + " don't have the method " + method);
            }
        }

        private static Object hessianTransport(Object source, ClassLoader contextClassLoader) {
            Object target;
            ClassLoader currentContextClassloader = Thread.currentThread().getContextClassLoader();
            try {
                if (contextClassLoader != null) {
                    Thread.currentThread().setContextClassLoader(contextClassLoader);
                }
                SerializerFactory serializerFactory = new SerializerFactory();
                serializerFactory.setAllowNonSerializable(true);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Hessian2Output h2o = new Hessian2Output(bos);
                h2o.setSerializerFactory(serializerFactory);
                h2o.writeObject(source);
                h2o.flush();
                byte[] content = bos.toByteArray();

                Hessian2Input h2i = new Hessian2Input(new ByteArrayInputStream(content));
                h2i.setSerializerFactory(serializerFactory);
                target = h2i.readObject();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } finally {
                Thread.currentThread().setContextClassLoader(currentContextClassloader);
            }
            return target;
        }
    }
}

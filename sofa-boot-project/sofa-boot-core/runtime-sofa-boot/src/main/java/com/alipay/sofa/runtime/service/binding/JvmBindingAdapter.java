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
package com.alipay.sofa.runtime.service.binding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

import com.alipay.sofa.boot.error.ErrorCode;
import com.alipay.sofa.runtime.SofaRuntimeProperties;
import com.alipay.sofa.runtime.filter.JvmFilterContext;
import com.alipay.sofa.runtime.filter.JvmFilterHolder;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;

import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.invoke.DynamicJvmServiceProxyFinder;
import com.alipay.sofa.runtime.log.SofaLogger;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.ServiceProxy;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;

/**
 * JVM Binding Adapter, used to handle JvmBinding
 *
 * @author xi.hux@alipay.com
 * @version $Id: DefaultBindingAdapter.java,v 0.1 2009-10-12 17:14:41 xi.hux Exp $
 */
public class JvmBindingAdapter implements BindingAdapter<JvmBinding> {
    public JvmBindingAdapter() {
    }

    @Override
    public void preOutBinding(Object contract, JvmBinding binding, Object target,
                              SofaRuntimeContext sofaRuntimeContext) {
    }

    @Override
    public Object outBinding(Object contract, JvmBinding binding, Object target,
                             SofaRuntimeContext sofaRuntimeContext) {
        return null;
    }

    @Override
    public void preUnoutBinding(Object contract, JvmBinding binding, Object target,
                                SofaRuntimeContext sofaRuntimeContext) {

    }

    @Override
    public void postUnoutBinding(Object contract, JvmBinding binding, Object target,
                                 SofaRuntimeContext sofaRuntimeContext) {

    }

    public BindingType getBindingType() {
        return JvmBinding.JVM_BINDING_TYPE;
    }

    public Class<JvmBinding> getBindingClass() {
        return JvmBinding.class;
    }

    public Object inBinding(Object contract, JvmBinding binding,
                            SofaRuntimeContext sofaRuntimeContext) {
        return createServiceProxy((Contract) contract, binding, sofaRuntimeContext);
    }

    @Override
    public void unInBinding(Object contract, JvmBinding binding,
                            SofaRuntimeContext sofaRuntimeContext) {
        binding.setDestroyed(true);
        if (binding.hasBackupProxy()) {
            binding.setBackupProxy(null);
        }
    }

    /**
     * create service proxy object
     *
     * @return proxy object
     */
    private Object createServiceProxy(Contract contract, JvmBinding binding,
                                      SofaRuntimeContext sofaRuntimeContext) {
        ClassLoader newClassLoader;
        ClassLoader appClassLoader = sofaRuntimeContext.getAppClassLoader();
        Class<?> javaClass = contract.getInterfaceType();

        try {
            Class appLoadedClass = appClassLoader.loadClass(javaClass.getName());

            if (appLoadedClass == javaClass) {
                newClassLoader = appClassLoader;
            } else {
                newClassLoader = javaClass.getClassLoader();
            }
        } catch (ClassNotFoundException e) {
            newClassLoader = javaClass.getClassLoader();
        }

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

        try {
            Thread.currentThread().setContextClassLoader(newClassLoader);
            ServiceProxy handler = new JvmServiceInvoker(contract, binding, sofaRuntimeContext);
            ProxyFactory factory = new ProxyFactory();
            if (javaClass.isInterface()) {
                factory.addInterface(javaClass);
            } else {
                factory.setTargetClass(javaClass);
                factory.setProxyTargetClass(true);
            }
            factory.addAdvice(handler);
            return factory.getProxy(newClassLoader);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    /**
     * JVM Service Invoker
     */
    static class JvmServiceInvoker extends ServiceProxy {
        private Contract           contract;
        private JvmBinding         binding;
        private Object             target;
        private SofaRuntimeContext sofaRuntimeContext;

        public JvmServiceInvoker(Contract contract, JvmBinding binding,
                                 SofaRuntimeContext sofaRuntimeContext) {
            super(sofaRuntimeContext.getAppClassLoader());
            this.binding = binding;
            this.sofaRuntimeContext = sofaRuntimeContext;
            this.contract = contract;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            if (!SofaRuntimeProperties.isJvmFilterEnable()) {
                // Jvm filtering is not enabled
                return super.invoke(invocation);
            }

            ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
            JvmFilterContext context = new JvmFilterContext(invocation);
            Object rtn;

            if (getTarget() == null) {
                ServiceComponent serviceComponent = DynamicJvmServiceProxyFinder
                    .getDynamicJvmServiceProxyFinder().findServiceComponent(
                        sofaRuntimeContext.getAppClassLoader(), contract);
                if (serviceComponent == null) {
                    // Jvm service is not found in normal or Ark environment
                    // We're actually invoking an RPC service, skip Jvm filtering
                    return super.invoke(invocation);
                }
                context.setSofaRuntimeContext(serviceComponent.getContext());
            } else {
                context.setSofaRuntimeContext(sofaRuntimeContext);
            }

            long startTime = System.currentTimeMillis();
            try {
                Thread.currentThread().setContextClassLoader(serviceClassLoader);
                // Do Jvm filter <code>before</code> invoking
                // if some filter returns false, skip remaining filters and actual Jvm invoking
                if (JvmFilterHolder.beforeInvoking(context)) {
                    rtn = doInvoke(invocation);
                    context.setInvokeResult(rtn);
                }
            } catch (Throwable e) {
                // Exception occurs, set <code>e</code> in Jvm context
                context.setException(e);
                doCatch(invocation, e, startTime);
                throw e;
            } finally {
                // Do Jvm Filter <code>after</code> invoking regardless of the fact whether exception happens or not
                JvmFilterHolder.afterInvoking(context);
                rtn = context.getInvokeResult();
                doFinally(invocation, startTime);
                Thread.currentThread().setContextClassLoader(oldClassLoader);
            }
            return rtn;
        }

        @Override
        public Object doInvoke(MethodInvocation invocation) throws Throwable {
            if (binding.isDestroyed()) {
                throw new IllegalStateException("Can not call destroyed reference! JVM Reference["
                                                + getInterfaceName() + "#" + getUniqueId()
                                                + "] has already been destroyed.");
            }

            SofaLogger.debug(">> Start in JVM service invoke, the service interface is  - {}",
                getInterfaceName());

            Object retVal;
            Object targetObj = this.getTarget();

            // invoke internal dynamic-biz jvm service
            if (targetObj == null) {
                ServiceProxy serviceProxy = DynamicJvmServiceProxyFinder
                    .getDynamicJvmServiceProxyFinder().findServiceProxy(
                        sofaRuntimeContext.getAppClassLoader(), contract);
                if (serviceProxy != null) {
                    try {
                        return serviceProxy.invoke(invocation);
                    } finally {
                        SofaLogger.debug(
                            "<< Finish Cross App JVM service invoke, the service is  - {}]",
                            (getInterfaceName() + "#" + getUniqueId()));
                    }
                }
            }

            if (targetObj == null || ((targetObj instanceof Proxy) && binding.hasBackupProxy())) {
                targetObj = binding.getBackupProxy();
                SofaLogger.debug("<<{}.{} backup proxy invoke.", getInterfaceName().getName(),
                    invocation.getMethod().getName());
            }

            if (targetObj == null) {
                throw new IllegalStateException(ErrorCode.convert("01-00400", getInterfaceName(),
                    getUniqueId()));
            }

            ClassLoader tcl = Thread.currentThread().getContextClassLoader();
            try {
                pushThreadContextClassLoader(sofaRuntimeContext.getAppClassLoader());
                retVal = invocation.getMethod().invoke(targetObj, invocation.getArguments());
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            } finally {
                SofaLogger.debug(
                    "<< Finish JVM service invoke, the service implementation is  - {}]",
                    (this.target == null ? "null" : this.target.getClass().getName()));

                popThreadContextClassLoader(tcl);
            }

            return retVal;
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

        protected Object getTarget() {
            if (this.target == null) {
                ComponentName componentName = ComponentNameFactory.createComponentName(
                    ServiceComponent.SERVICE_COMPONENT_TYPE, getInterfaceName(),
                    contract.getUniqueId());
                ComponentInfo componentInfo = sofaRuntimeContext.getComponentManager()
                    .getComponentInfo(componentName);

                if (componentInfo != null) {
                    this.target = componentInfo.getImplementation().getTarget();
                }
            }

            return target;
        }

        protected Class<?> getInterfaceName() {
            return contract.getInterfaceType();
        }

        protected String getUniqueId() {
            return contract.getUniqueId();
        }
    }
}

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

import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.integration.invoke.DynamicJvmServiceProxyFinder;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.log.SofaLogger;
import com.alipay.sofa.runtime.spi.service.ServiceProxy;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

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
        public Object doInvoke(MethodInvocation invocation) throws Throwable {
            if (binding.isDestroyed()) {
                throw new IllegalStateException("Can not call destroyed reference! JVM Reference["
                                                + getInterfaceName() + "#" + getUniqueId()
                                                + "] has already been destroyed.");
            }

            SofaLogger.debug(">> Start in JVM service invoke, the service interface is  - {0}",
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
                            "<< Finish Cross App JVM service invoke, the service is  - {0}]",
                            (getInterfaceName() + "#" + getUniqueId()));
                    }
                }
            }

            if ((targetObj == null || ((targetObj instanceof Proxy) && binding.hasBackupProxy()))) {
                targetObj = binding.getBackupProxy();
                SofaLogger.debug("<<{0}.{1} backup proxy invoke.", getInterfaceName().getName(),
                    invocation.getMethod().getName());
            }

            if (targetObj == null) {
                throw new IllegalStateException(
                    "JVM Reference["
                            + getInterfaceName()
                            + "#"
                            + getUniqueId()
                            + "] cant not find the corresponding JVM service. "
                            + "Please check if there is a SOFA deployment publish the corresponding JVM service. "
                            + "If this exception occurred when the application starts up, please add Require-Module to SOFA deployment's MANIFEST.MF to indicate the startup dependency of SOFA modules.");
            }

            ClassLoader tcl = Thread.currentThread().getContextClassLoader();
            try {
                pushThreadContextClassLoader(sofaRuntimeContext.getAppClassLoader());
                retVal = invocation.getMethod().invoke(targetObj, invocation.getArguments());
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            } finally {
                SofaLogger.debug(
                    "<< Finish JVM service invoke, the service implementation is  - {0}]",
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
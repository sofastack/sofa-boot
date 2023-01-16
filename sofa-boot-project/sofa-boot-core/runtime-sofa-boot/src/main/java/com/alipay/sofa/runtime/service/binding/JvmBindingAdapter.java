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

import com.alipay.sofa.boot.util.ClassLoaderContextUtils;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.ServiceProxy;
import org.springframework.aop.framework.ProxyFactory;

/**
 * JVM Binding Adapter, used to handle JvmBinding.
 *
 * @author xi.hux@alipay.com
 * @version $Id: DefaultBindingAdapter.java,v 0.1 2009-10-12 17:14:41 xi.hux Exp $
 */
public class JvmBindingAdapter implements BindingAdapter<JvmBinding> {

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

    @Override
    public BindingType getBindingType() {
        return JvmBinding.JVM_BINDING_TYPE;
    }

    @Override
    public Class<JvmBinding> getBindingClass() {
        return JvmBinding.class;
    }

    @Override
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
            Class<?> appLoadedClass = appClassLoader.loadClass(javaClass.getName());

            if (appLoadedClass == javaClass) {
                newClassLoader = appClassLoader;
            } else {
                newClassLoader = javaClass.getClassLoader();
            }
        } catch (ClassNotFoundException e) {
            newClassLoader = javaClass.getClassLoader();
        }

        ClassLoader finalClassLoader = newClassLoader;

        return ClassLoaderContextUtils.callWithTemporaryContextClassloader(() -> {
            ServiceProxy handler = new JvmServiceInvoker(contract, binding, sofaRuntimeContext);
            ProxyFactory factory = new ProxyFactory();
            if (javaClass.isInterface()) {
                factory.addInterface(javaClass);
                factory.addInterface(JvmBindingInterface.class);
            } else {
                factory.setTargetClass(javaClass);
                factory.setProxyTargetClass(true);
            }
            factory.addAdvice(handler);
            return factory.getProxy(finalClassLoader);
        }, finalClassLoader);

    }
}

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
package com.alipay.sofa.runtime.proxy;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.BeanFactory;

/**
 * Extension of {@link ProxyFactoryBean} to avoid early init.
 *
 * @author ruoshan
 * @since 3.12.0
 */
public class SofaProxyFactoryBean extends ProxyFactoryBean {

    public SofaProxyFactoryBean(Class<?>[] proxyInterfaces, String targetName,
                                Class<?> targetClass, BeanFactory beanFactory) {
        if (proxyInterfaces != null) {
            setInterfaces(proxyInterfaces);
        }
        setTargetName(targetName);
        setTargetClass(targetClass);
        setBeanFactory(beanFactory);
    }
}

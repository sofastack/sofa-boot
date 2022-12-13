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
package com.alipay.sofa.runtime.test.proxy;

import com.alipay.sofa.runtime.proxy.ProxyBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.test.proxy.bean.ProxyTestBeanFacade;
import com.alipay.sofa.runtime.test.proxy.bean.ProxyTestBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.test.proxy.bean.ProxyTestBeanImpl;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author ruoshan
 * @since 2.6.1
 */
@Configuration
public class ProxyConfiguration {

    @Bean
    public static ProxyTestBeanFactoryPostProcessor proxyTestBeanFactoryPostProcessor() {
        return new ProxyTestBeanFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnProperty(prefix = "com.alipay.sofa.proxy.bean", name = "enabled", havingValue = "true")
    public static ProxyBeanFactoryPostProcessor proxyBeanFactoryPostProcessor() {
        return new ProxyBeanFactoryPostProcessor();
    }

    @Bean
    public ProxyFactoryBean proxyFactoryBean5(ProxyTestBeanFacade proxyTestBean) {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setInterfaces(ProxyTestBeanFacade.class);
        proxyFactoryBean.setTarget(proxyTestBean);
        return proxyFactoryBean;
    }

    @Bean
    public static ProxyFactoryBean proxyFactoryBean6() {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setInterfaces(ProxyTestBeanFacade.class);
        proxyFactoryBean.setTarget(new ProxyTestBeanImpl());
        return proxyFactoryBean;
    }
}

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
package com.alipay.sofa.smoke.tests.runtime.proxy;

import com.alipay.sofa.runtime.proxy.ProxyBeanFactoryPostProcessor;
import com.alipay.sofa.runtime.proxy.SofaProxyFactoryBean;
import com.alipay.sofa.smoke.tests.runtime.RuntimeSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ProxyBeanFactoryPostProcessor}.
 * 
 * @author ruoshan
 * @author huzijie
 * @since 2.6.1
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class)
@Import(TestProxyConfiguration.class)
public class ProxyBeanFactoryPostProcessorTests {

    @Autowired
    private ProxyTestBeanFacade proxyTestBean;

    @Autowired
    @Qualifier("&proxyFactoryBean1")
    private ProxyFactoryBean    proxyFactoryBean1;

    @Autowired
    @Qualifier("&proxyFactoryBean2")
    private ProxyFactoryBean    proxyFactoryBean2;

    @Autowired
    @Qualifier("&proxyFactoryBean3")
    private ProxyFactoryBean    proxyFactoryBean3;

    @Autowired
    @Qualifier("&proxyFactoryBean4")
    private ProxyFactoryBean    proxyFactoryBean4;

    @Autowired
    @Qualifier("&proxyFactoryBean5")
    private ProxyFactoryBean    proxyFactoryBean5;

    @Autowired
    @Qualifier("&proxyFactoryBean6")
    private ProxyFactoryBean    proxyFactoryBean6;

    @Test
    public void verifyClassType() {
        assertThat(proxyTestBean.isPostProcessed()).isTrue();
        assertThat(proxyFactoryBean1 instanceof SofaProxyFactoryBean).isTrue();
        assertThat(proxyFactoryBean2 instanceof SofaProxyFactoryBean).isTrue();
        assertThat(proxyFactoryBean3 instanceof SofaProxyFactoryBean).isTrue();
        assertThat(proxyFactoryBean4 instanceof SofaProxyFactoryBean).isTrue();
        // only proxy xml bean
        assertThat(proxyFactoryBean5 instanceof SofaProxyFactoryBean).isFalse();
        assertThat(proxyFactoryBean6 instanceof SofaProxyFactoryBean).isFalse();
    }

}

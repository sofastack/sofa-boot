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
package com.alipay.sofa.infra.proxy;

import com.alipay.sofa.infra.base.SofaBootWebSpringBootApplication;
import com.alipay.sofa.infra.proxy.bean.ProxyTestBeanFacade;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author ruoshan
 * @since 2.6.1
 */
@SpringBootTest(classes = SofaBootWebSpringBootApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("/config/application-sofa-proxy.properties")
public class SofaProxyFactoryBeanTest {

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

    @Test
    public void test() {
        Assert.assertTrue(proxyTestBean.isPostProcessed());
        Assert.assertTrue(proxyFactoryBean1 instanceof SofaProxyFactoryBean);
        Assert.assertTrue(proxyFactoryBean2 instanceof SofaProxyFactoryBean);
        Assert.assertTrue(proxyFactoryBean3 instanceof SofaProxyFactoryBean);
        Assert.assertTrue(proxyFactoryBean4 instanceof SofaProxyFactoryBean);
    }

}
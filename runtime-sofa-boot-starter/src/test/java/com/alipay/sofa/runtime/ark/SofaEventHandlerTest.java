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
package com.alipay.sofa.runtime.ark;

import com.alipay.sofa.ark.spi.constant.Constants;
import com.alipay.sofa.ark.spi.event.BizEvent;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.model.BizState;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.SofaRuntimeProperties;
import com.alipay.sofa.runtime.beans.service.SampleService;
import com.alipay.sofa.runtime.integration.invoke.DynamicJvmServiceProxyFinder;
import com.alipay.sofa.runtime.integration.service.SofaEventHandler;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.service.ServiceProxy;
import com.alipay.sofa.runtime.spring.initializer.SofaRuntimeSpringContextInitializer;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class SofaEventHandlerTest {

    private ConfigurableApplicationContext applicationContext;

    @Mocked
    private Biz                            biz;
    @Mocked
    private SofaRuntimeManager             sofaRuntimeManager;
    @Mocked
    private Contract                       contract;
    @Mocked
    private MethodInvocation               invocation;

    @Before
    public void before() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("com.alipay.sofa.boot.disableJvmFirst", "true");
        properties.put("com.alipay.sofa.boot.skipJvmReferenceHealthCheck", "true");
        SpringApplication springApplication = new SpringApplication(XmlConfiguration.class);
        springApplication.setDefaultProperties(properties);
        this.applicationContext = springApplication.run(new String[] {});
    }

    @Test
    public void testUninstallEvent() {
        new Expectations() {
            {
                biz.getBizClassLoader();
                result = this.getClass().getClassLoader();
            }
        };
        Assert.assertTrue(SofaRuntimeProperties.isDisableJvmFirst(applicationContext
            .getClassLoader()));
        Assert.assertTrue(SofaRuntimeProperties.isSkipJvmReferenceHealthCheck(applicationContext
            .getClassLoader()));
        Assert.assertFalse(SofaFramework.getRuntimeSet().isEmpty());
        Assert.assertTrue(applicationContext.isActive());

        SofaEventHandler sofaEventHandler = new SofaEventHandler();
        sofaEventHandler.handleEvent(new BizEvent(biz, Constants.BIZ_EVENT_TOPIC_UNINSTALL));

        Assert.assertFalse(SofaRuntimeProperties.isDisableJvmFirst(applicationContext
            .getClassLoader()));
        Assert.assertFalse(SofaRuntimeProperties.isSkipJvmReferenceHealthCheck(applicationContext
            .getClassLoader()));
        Assert.assertTrue(SofaFramework.getRuntimeSet().isEmpty());

        Assert.assertFalse(applicationContext.isActive());
    }

    @Test
    public void testHealthCheck() {
        new Expectations() {
            {
                biz.getBizClassLoader();
                result = this.getClass().getClassLoader();
            }
        };
        SofaEventHandler sofaEventHandler = new SofaEventHandler();
        sofaEventHandler.handleEvent(new BizEvent(biz, Constants.BIZ_EVENT_TOPIC_HEALTH_CHECK));
    }

    @Test
    public void testDynamicProxyFinder() throws Exception {
        SofaFramework.registerSofaRuntimeManager(sofaRuntimeManager);
        new MockUp<DynamicJvmServiceProxyFinder>() {
            @Mock
            public Biz getBiz(SofaRuntimeManager sofaRuntimeManager) {
                return biz;
            }
        };

        new Expectations() {
            {
                biz.getIdentity();
                result = "MockName:MockVersion";

                biz.getBizState();
                result = BizState.ACTIVATED;

                sofaRuntimeManager.getAppClassLoader();
                result = applicationContext.getClassLoader().getParent();
            }
        };

        new Expectations() {
            {
                sofaRuntimeManager.getComponentManager();
                result = ((SofaRuntimeContext) applicationContext.getBean("sofaRuntimeContext"))
                    .getComponentManager();
                contract.getInterfaceType();
                result = SampleService.class;
                contract.getUniqueId();
                result = "";

                invocation.getArguments();
                result = new Object[] {};
                invocation.getMethod();
                result = SampleService.class.getMethod("service");
            }
        };
        ServiceProxy serviceProxy = DynamicJvmServiceProxyFinder.getDynamicJvmServiceProxyFinder()
            .findServiceProxy(applicationContext.getClassLoader(), contract);
        try {
            Assert.assertTrue("AnnotationSampleService".equals(serviceProxy.invoke(invocation)));
        } catch (Throwable throwable) {
            throw new RuntimeException("testDynamicProxyFinder case failed.", throwable);
        }
    }

    @After
    public void closeContext() {
        this.applicationContext.close();
        Set<SofaRuntimeManager> runtimeManagers = SofaFramework.getRuntimeSet();
        for (SofaRuntimeManager runtimeManager : runtimeManagers) {
            SofaFramework.unRegisterSofaRuntimeManager(runtimeManager);
        }
        SofaRuntimeSpringContextInitializer.setIsInitiated(false);
    }

    @Configuration
    @EnableAutoConfiguration
    @ComponentScan("com.alipay.sofa.runtime.beans.impl")
    public static class XmlConfiguration {
    }
}
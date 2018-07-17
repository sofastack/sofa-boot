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
import com.alipay.sofa.healthcheck.initializer.HealthCheckInitializer;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.SofaRuntimeProperties;
import com.alipay.sofa.runtime.beans.service.SampleService;
import com.alipay.sofa.runtime.integration.invoke.DynamicJvmServiceProxyFinder;
import com.alipay.sofa.runtime.integration.service.SofaEventHandler;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.service.ServiceProxy;
import com.alipay.sofa.runtime.spring.configuration.SofaRuntimeAutoConfiguration;
import mockit.*;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.*;

import java.util.Collections;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class SofaEventHandlerTest {

    private final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

    @Mocked
    private Biz                                      biz;

    @Before
    public void before() {
        new NonStrictExpectations() {
            {
                biz.getBizClassLoader();
                result = this.getClass().getClassLoader();

                biz.getIdentity();
                result = "MockName:MockVersion";

                biz.getBizState();
                result = BizState.ACTIVATED;
            }
        };

        new MockUp<DynamicJvmServiceProxyFinder>() {
            @Mock
            public Biz getBiz(SofaRuntimeManager sofaRuntimeManager) {
                return biz;
            }
        };

        EnvironmentTestUtils.addEnvironment(this.applicationContext,
            "com.alipay.sofa.boot.disableJvmFirst=true");
        EnvironmentTestUtils.addEnvironment(this.applicationContext,
            "com.alipay.sofa.boot.skipJvmReferenceHealthCheck=true");
        this.applicationContext.register(SofaRuntimeAutoConfiguration.class,
            HealthCheckInitializer.class, XmlConfiguration.class);
        this.applicationContext.refresh();
    }

    @Test
    public void testUninstallEvent() {
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
        SofaEventHandler sofaEventHandler = new SofaEventHandler();
        sofaEventHandler.handleEvent(new BizEvent(biz, Constants.BIZ_EVENT_TOPIC_HEALTH_CHECK));
    }

    @Test
    public void testDynamicProxyFinder(@Mocked final SofaFramework sofaFramework,
                                       @Mocked final SofaRuntimeManager sofaRuntimeManager,
                                       @Mocked final Contract contract,
                                       @Mocked final MethodInvocation invocation) throws Exception {
        new Expectations() {
            {
                sofaFramework.getRuntimeSet();
                result = Collections.singleton(sofaRuntimeManager);
                sofaRuntimeManager.getAppClassLoader();
                result = applicationContext.getClassLoader().getParent();
                sofaRuntimeManager.getAppClassLoader();
                result = applicationContext.getClassLoader();
            }
        };

        new NonStrictExpectations() {
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
            };
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
    }

    @Configuration
    @ComponentScan("com.alipay.sofa.runtime.beans.impl")
    public static class XmlConfiguration {
    }
}
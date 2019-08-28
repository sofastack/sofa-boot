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
package com.alipay.sofa.runtime.test;

import java.util.Properties;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.alipay.sofa.ark.spi.constant.Constants;
import com.alipay.sofa.ark.spi.event.BizEvent;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.model.BizState;
import com.alipay.sofa.runtime.SofaEventHandler;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.SofaRuntimeProperties;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.invoke.DynamicJvmServiceProxyFinder;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.service.ServiceProxy;
import com.alipay.sofa.runtime.spring.aware.DefaultRuntimeShutdownAware;
import com.alipay.sofa.runtime.test.beans.facade.SampleService;
import com.alipay.sofa.runtime.test.beans.service.DefaultSampleService;
import com.alipay.sofa.runtime.test.configuration.RuntimeConfiguration;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class SofaEventHandlerTest {
    @Mocked
    private Biz                            biz;
    @Mocked
    private SofaRuntimeManager             sofaRuntimeManager;
    @Mocked
    private Contract                       contract;
    @Mocked
    private MethodInvocation               invocation;

    private ConfigurableApplicationContext ctx;

    @Before
    public void before() {
        Properties properties = new Properties();
        properties.setProperty("com.alipay.sofa.boot.disableJvmFirst", "true");
        properties.setProperty("com.alipay.sofa.boot.skipJvmReferenceHealthCheck", "true");
        properties.setProperty("com.alipay.sofa.boot.skipJvmSerialize", "true");
        properties.setProperty("spring.application.name", "tSofaEventHandlerTest");
        SofaFramework.getRuntimeSet().forEach(value -> SofaFramework.unRegisterSofaRuntimeManager(value));
        SpringApplication springApplication = new SpringApplication(
            SofaEventHandlerTestConfiguration.class);
        springApplication.setDefaultProperties(properties);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        ctx = springApplication.run();
    }

    @Test
    public void testUninstallEvent() {
        new Expectations() {
            {
                biz.getBizClassLoader();
                result = this.getClass().getClassLoader();
            }
        };
        Assert.assertFalse(SofaFramework.getRuntimeSet().isEmpty());
        Assert.assertTrue(ctx.isActive());

        SofaEventHandler sofaEventHandler = new SofaEventHandler();
        sofaEventHandler.handleEvent(new BizEvent(biz,
            Constants.BIZ_EVENT_TOPIC_AFTER_INVOKE_BIZ_STOP));

        Assert.assertFalse(SofaRuntimeProperties.isDisableJvmFirst(ctx.getClassLoader()));
        Assert
            .assertFalse(SofaRuntimeProperties.isSkipJvmReferenceHealthCheck(ctx.getClassLoader()));
        Assert.assertFalse(SofaRuntimeProperties.isSkipJvmSerialize(ctx.getClassLoader()));
        Assert.assertTrue(SofaFramework.getRuntimeSet().isEmpty());
        Assert.assertFalse(ctx.isActive());
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
        sofaEventHandler.handleEvent(new BizEvent(biz,
            Constants.BIZ_EVENT_TOPIC_AFTER_INVOKE_BIZ_START));
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
                result = ctx.getClassLoader().getParent();
            }
        };

        new Expectations() {
            {
                sofaRuntimeManager.getComponentManager();
                result = ((SofaRuntimeContext) ctx.getBean("sofaRuntimeContext"))
                    .getComponentManager();
                contract.getInterfaceType();
                result = SampleService.class;
                contract.getUniqueId();
                result = "";
                contract.getBinding(JvmBinding.JVM_BINDING_TYPE);
                result = new JvmBinding();

                invocation.getArguments();
                result = new Object[] {};
                invocation.getMethod();
                result = SampleService.class.getMethod("service");
            }
        };
        ServiceProxy serviceProxy = DynamicJvmServiceProxyFinder.getDynamicJvmServiceProxyFinder()
            .findServiceProxy(ctx.getClassLoader(), contract);
        try {
            Assert.assertTrue(SofaEventHandlerTest.class.getName().equals(
                serviceProxy.invoke(invocation)));
        } catch (Throwable throwable) {
            throw new RuntimeException("testDynamicProxyFinder case failed.", throwable);
        }
    }

    @Configuration
    @Import(RuntimeConfiguration.class)
    static class SofaEventHandlerTestConfiguration {
        @Bean
        public DefaultRuntimeShutdownAware defaultRuntimeShutdownAware() {
            return new DefaultRuntimeShutdownAware();
        }

        @Bean
        @SofaService
        public DefaultSampleService defaultSampleService() {
            return new DefaultSampleService(SofaEventHandlerTest.class.getName());
        }
    }
}
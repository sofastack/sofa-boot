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

import com.alipay.sofa.ark.spi.event.biz.AfterBizStartupEvent;
import com.alipay.sofa.ark.spi.event.biz.BeforeBizStopEvent;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.model.BizState;
import com.alipay.sofa.runtime.SofaBizHealthCheckEventHandler;
import com.alipay.sofa.runtime.SofaBizUninstallEventHandler;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.SofaRuntimeProperties;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.invoke.DynamicJvmServiceProxyFinder;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.service.ServiceProxy;
import com.alipay.sofa.runtime.test.beans.facade.SampleService;
import com.alipay.sofa.runtime.test.beans.service.DefaultSampleService;
import com.alipay.sofa.runtime.test.configuration.RuntimeConfiguration;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class SofaEventHandlerTest {
    @Mock
    private Biz                biz;
    @Mock
    private SofaRuntimeManager sofaRuntimeManager;
    @Mock
    private Contract           contract;
    @Mock
    private MethodInvocation   invocation;

    private ConfigurableApplicationContext ctx;

    @Before
    public void before() {
        Properties properties = new Properties();
        properties.setProperty("com.alipay.sofa.boot.disableJvmFirst", "true");
        properties.setProperty("com.alipay.sofa.boot.skipJvmReferenceHealthCheck", "true");
        properties.setProperty("com.alipay.sofa.boot.skipExtensionHealthCheck", "true");
        properties.setProperty("com.alipay.sofa.boot.skipJvmSerialize", "true");
        properties.setProperty("com.alipay.sofa.boot.extensionFailureInsulating", "true");
        properties.setProperty("com.alipay.sofa.boot.serviceInterfaceTypeCheck", "true");
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
        Mockito.when(biz.getBizClassLoader()).thenReturn(this.getClass().getClassLoader());
        Assert.assertFalse(SofaFramework.getRuntimeSet().isEmpty());
        Assert.assertTrue(ctx.isActive());

        SofaBizUninstallEventHandler sofaEventHandler = new SofaBizUninstallEventHandler();
        sofaEventHandler.handleEvent(new BeforeBizStopEvent(biz));

        Assert.assertFalse(SofaRuntimeProperties.isDisableJvmFirst(ctx.getClassLoader()));
        Assert
                .assertFalse(SofaRuntimeProperties.isSkipJvmReferenceHealthCheck(ctx.getClassLoader()));
        Assert.assertFalse(SofaRuntimeProperties.isSkipExtensionHealthCheck(ctx.getClassLoader()));
        Assert
                .assertFalse(SofaRuntimeProperties.isExtensionFailureInsulating(ctx.getClassLoader()));
        Assert.assertFalse(SofaRuntimeProperties.isServiceInterfaceTypeCheck());
        Assert.assertFalse(SofaRuntimeProperties.isSkipJvmSerialize(ctx.getClassLoader()));
        Assert.assertTrue(SofaFramework.getRuntimeSet().isEmpty());
        Assert.assertFalse(ctx.isActive());
    }

    @Test
    public void testHealthCheck() {
        Mockito.when(biz.getBizClassLoader()).thenReturn(this.getClass().getClassLoader());
        SofaBizHealthCheckEventHandler sofaEventHandler = new SofaBizHealthCheckEventHandler();
        sofaEventHandler.handleEvent(new AfterBizStartupEvent(biz));
    }

    @Test
    public void testDynamicProxyFinder() throws Exception {
        SofaFramework.registerSofaRuntimeManager(sofaRuntimeManager);
        Mockito.when(biz.getIdentity()).thenReturn("MockName:MockVersion");
        Mockito.when(biz.getBizState()).thenReturn(BizState.ACTIVATED);
        Mockito.when(sofaRuntimeManager.getAppClassLoader()).thenReturn(ctx.getClassLoader().getParent());

        Mockito.when(sofaRuntimeManager.getComponentManager()).thenReturn(((SofaRuntimeContext) ctx.getBean("sofaRuntimeContext"))
                .getComponentManager());
        Mockito.when(contract.getInterfaceType()).thenAnswer((Answer<Class>) invocationOnMock -> SampleService.class);
        Mockito.when(contract.getUniqueId()).thenReturn("");
        Mockito.when(contract.getBinding(JvmBinding.JVM_BINDING_TYPE)).thenReturn(new JvmBinding());
        Mockito.when(invocation.getArguments()).thenReturn(new Object[] {});
        Mockito.when(invocation.getMethod()).thenReturn(SampleService.class.getMethod("service"));

        DynamicJvmServiceProxyFinder dynamicJvmServiceProxyFinder = DynamicJvmServiceProxyFinder.getDynamicJvmServiceProxyFinder();
        try (MockedStatic<DynamicJvmServiceProxyFinder> utilities = Mockito.mockStatic(DynamicJvmServiceProxyFinder.class)) {
            utilities.when(() -> DynamicJvmServiceProxyFinder.getBiz(any()))
                    .thenReturn(biz);
            utilities.when(DynamicJvmServiceProxyFinder::getDynamicJvmServiceProxyFinder).thenReturn(dynamicJvmServiceProxyFinder);
            dynamicJvmServiceProxyFinder.setHasFinishStartup(true);
            ServiceProxy serviceProxy = dynamicJvmServiceProxyFinder
                    .findServiceProxy(ctx.getClassLoader(), contract);
            try {
                Assert.assertTrue(SofaEventHandlerTest.class.getName().equals(
                        serviceProxy.invoke(invocation)));
            } catch (Throwable throwable) {
                throw new RuntimeException("testDynamicProxyFinder case failed.", throwable);
            }
        }

    }

    @Configuration(proxyBeanMethods = false)
    @Import(RuntimeConfiguration.class)
    static class SofaEventHandlerTestConfiguration {

        @Bean
        @SofaService
        public DefaultSampleService defaultSampleService() {
            return new DefaultSampleService(SofaEventHandlerTest.class.getName());
        }
    }
}

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

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;
import com.alipay.sofa.runtime.spring.SpringContextComponent;
import com.alipay.sofa.runtime.spring.SpringContextImplementation;
import com.alipay.sofa.runtime.test.beans.facade.SampleService;
import com.alipay.sofa.runtime.test.beans.service.DefaultSampleService;
import com.alipay.sofa.runtime.test.configuration.RuntimeConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;

import static com.alipay.sofa.runtime.service.component.ServiceComponent.SERVICE_COMPONENT_TYPE;
import static com.alipay.sofa.runtime.spring.SpringContextComponent.SPRING_COMPONENT_TYPE;

/**
 * @author huzijie
 * @version ComponentManagerShutdownTest.java, v 0.1 2022年04月29日 5:19 PM huzijie Exp $
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class ComponentManagerShutdownTest {

    @Test
    public void testNormalShutdown() {
        ComponentManager componentManager = initComponentManager();
        ComponentInfo serviceComponentInfo = componentManager
            .getComponentInfosByType(SERVICE_COMPONENT_TYPE).stream().findFirst().get();
        ComponentInfo springComponentInfo = componentManager
            .getComponentInfosByType(SPRING_COMPONENT_TYPE).stream().findFirst().get();
        GenericApplicationContext applicationContext = (GenericApplicationContext) springComponentInfo
            .getImplementation().getTarget();

        Assert.assertEquals(2, componentManager.size());
        Assert.assertTrue(serviceComponentInfo.isActivated());
        Assert.assertTrue(springComponentInfo.isActivated());
        Assert.assertTrue(applicationContext.isActive());
        componentManager.shutdown();
        Assert.assertEquals(0, componentManager.size());
        Assert.assertFalse(serviceComponentInfo.isActivated());
        Assert.assertFalse(springComponentInfo.isActivated());
        Assert.assertFalse(applicationContext.isActive());
    }

    @Test
    public void testSkipAllComponentShutdown() {
        SofaRuntimeProperties.setSkipAllComponentShutdown(Thread.currentThread()
            .getContextClassLoader(), true);
        ComponentManager componentManager = initComponentManager();
        ComponentInfo serviceComponentInfo = componentManager
            .getComponentInfosByType(SERVICE_COMPONENT_TYPE).stream().findFirst().get();
        ComponentInfo springComponentInfo = componentManager
            .getComponentInfosByType(SPRING_COMPONENT_TYPE).stream().findFirst().get();
        GenericApplicationContext applicationContext = (GenericApplicationContext) springComponentInfo
            .getImplementation().getTarget();

        Assert.assertEquals(2, componentManager.size());
        Assert.assertTrue(serviceComponentInfo.isActivated());
        Assert.assertTrue(springComponentInfo.isActivated());
        Assert.assertTrue(applicationContext.isActive());
        componentManager.shutdown();
        Assert.assertEquals(2, componentManager.size());
        Assert.assertTrue(serviceComponentInfo.isActivated());
        Assert.assertTrue(springComponentInfo.isActivated());
        Assert.assertTrue(applicationContext.isActive());
        SofaRuntimeProperties.setSkipAllComponentShutdown(Thread.currentThread()
            .getContextClassLoader(), false);
    }

    @Test
    public void testSkipCommonComponentShutdown() {
        SofaRuntimeProperties.setSkipCommonComponentShutdown(Thread.currentThread()
            .getContextClassLoader(), true);
        ComponentManager componentManager = initComponentManager();
        ComponentInfo serviceComponentInfo = componentManager
            .getComponentInfosByType(SERVICE_COMPONENT_TYPE).stream().findFirst().get();
        ComponentInfo springComponentInfo = componentManager
            .getComponentInfosByType(SPRING_COMPONENT_TYPE).stream().findFirst().get();
        GenericApplicationContext applicationContext = (GenericApplicationContext) springComponentInfo
            .getImplementation().getTarget();

        Assert.assertEquals(2, componentManager.size());
        Assert.assertTrue(serviceComponentInfo.isActivated());
        Assert.assertTrue(springComponentInfo.isActivated());
        Assert.assertTrue(applicationContext.isActive());
        componentManager.shutdown();
        Assert.assertEquals(1, componentManager.size());
        Assert.assertTrue(serviceComponentInfo.isActivated());
        Assert.assertFalse(springComponentInfo.isActivated());
        Assert.assertFalse(applicationContext.isActive());
        SofaRuntimeProperties.setSkipCommonComponentShutdown(Thread.currentThread()
            .getContextClassLoader(), false);
    }

    private ComponentManager initComponentManager() {
        AnnotationConfigApplicationContext rootContext = new AnnotationConfigApplicationContext(
            ComponentManagerTestConfiguration.class);
        SofaRuntimeContext sofaRuntimeContext = rootContext.getBean(SofaRuntimeContext.class);
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();

        ComponentName serviceComponentName = ComponentNameFactory.createComponentName(
            SERVICE_COMPONENT_TYPE, SampleService.class, "");
        ComponentInfo serviceComponentInfo = componentManager
            .getComponentInfo(serviceComponentName);
        componentManager.register(serviceComponentInfo);

        GenericApplicationContext applicationContext = new GenericApplicationContext();
        ComponentName springComponentName = ComponentNameFactory.createComponentName(
            SPRING_COMPONENT_TYPE, "testModule");
        ComponentInfo springComponentInfo = new SpringContextComponent(springComponentName,
            new SpringContextImplementation(applicationContext), sofaRuntimeContext);
        applicationContext.refresh();
        componentManager.register(springComponentInfo);

        return componentManager;
    }

    @Configuration(proxyBeanMethods = false)
    @Import(RuntimeConfiguration.class)
    static class ComponentManagerTestConfiguration {
        @Bean
        @SofaService
        public SampleService sampleService() {
            return new DefaultSampleService();
        }
    }
}

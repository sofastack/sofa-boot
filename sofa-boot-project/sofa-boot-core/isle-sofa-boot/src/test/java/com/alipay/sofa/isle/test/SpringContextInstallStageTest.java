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
package com.alipay.sofa.isle.test;

import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.deployment.DeploymentException;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.stage.SpringContextInstallStage;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.client.impl.ClientFactoryImpl;
import com.alipay.sofa.runtime.component.impl.StandardSofaRuntimeManager;
import com.alipay.sofa.runtime.ext.component.ExtensionPointComponent;
import com.alipay.sofa.runtime.ext.component.ExtensionPointImpl;
import com.alipay.sofa.runtime.service.client.ReferenceClientImpl;
import com.alipay.sofa.runtime.service.client.ServiceClientImpl;
import com.alipay.sofa.runtime.service.impl.BindingAdapterFactoryImpl;
import com.alipay.sofa.runtime.service.impl.BindingConverterFactoryImpl;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.*;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @author huzijie
 * @version SpringContextInstallStageTest.java, v 0.1 2021年12月20日 3:23 下午 huzijie Exp $
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringContextInstallStageTest {
    @Autowired
    private SpringContextInstallStage springContextInstallStage;
    @Autowired
    private SofaModuleProperties      moduleProperties;

    @Test
    public void testQuickFailure() {
        try {
            springContextInstallStage.process();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof DeploymentException);
            Assert.assertTrue(e.getMessage().contains("01-11007"));
            Assert.assertTrue(e.getMessage().contains("isle-test"));
        }
        moduleProperties.setIgnoreModuleInstallFailure(true);
        try {
            springContextInstallStage.process();
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Configuration
    static class SpringContextInstallConfiguration {

        @Bean("SOFABOOT-APPLICATION")
        public ApplicationRuntimeModel applicationRuntimeModel(SofaRuntimeManager sofaRuntimeManager) {
            ApplicationRuntimeModel model = new ApplicationRuntimeModel();
            model.setSofaRuntimeContext(sofaRuntimeManager.getSofaRuntimeContext());
            model.addFailed(new DeploymentDescriptor() {
                @Override
                public String getModuleName() {
                    return "isle-test";
                }

                @Override
                public String getName() {
                    return null;
                }

                @Override
                public List<String> getRequiredModules() {
                    return null;
                }

                @Override
                public String getProperty(String key) {
                    return null;
                }

                @Override
                public String getSpringParent() {
                    return null;
                }

                @Override
                public ClassLoader getClassLoader() {
                    return null;
                }

                @Override
                public void setApplicationContext(ApplicationContext context) {

                }

                @Override
                public ApplicationContext getApplicationContext() {
                    return null;
                }

                @Override
                public void addInstalledSpringXml(String fileName) {

                }

                @Override
                public List<String> getInstalledSpringXml() {
                    return null;
                }

                @Override
                public boolean isSpringPowered() {
                    return false;
                }

                @Override
                public void startDeploy() {

                }

                @Override
                public void deployFinish() {

                }

                @Override
                public Map<String, Resource> getSpringResources() {
                    return null;
                }

                @Override
                public long getElapsedTime() {
                    return 0;
                }

                @Override
                public long getStartTime() {
                    return 0;
                }

                @Override
                public int compareTo(DeploymentDescriptor o) {
                    return 0;
                }
            });

            Implementation implementation = new DefaultImplementation("test");
            ComponentInfo component = new ExtensionPointComponent(new ExtensionPointImpl(
                "testComponent", null), model.getSofaRuntimeContext(), implementation);
            ComponentInfo componentWithContext = new ExtensionPointComponent(
                new ExtensionPointImpl("testComponentWithContext", null),
                model.getSofaRuntimeContext(), implementation);
            componentWithContext.setApplicationContext(sofaRuntimeManager
                .getRootApplicationContext());
            model.getSofaRuntimeContext().getComponentManager().register(component);
            model.getSofaRuntimeContext().getComponentManager().register(componentWithContext);
            return model;
        }

        @Bean
        @ConditionalOnMissingBean
        public SpringContextInstallStage springContextInstallStage(ApplicationContext applicationContext,
                                                                   SofaModuleProperties sofaModuleProperties) {
            return new SpringContextInstallStage((AbstractApplicationContext) applicationContext,
                sofaModuleProperties);
        }

        @Bean
        public SofaModuleProperties sofaModuleProperties() {
            return new SofaModuleProperties();
        }

        @Bean
        public AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor() {
            return new AutowiredAnnotationBeanPostProcessor();
        }

        @Bean
        @ConditionalOnMissingBean
        public static BindingConverterFactory bindingConverterFactory() {
            BindingConverterFactory bindingConverterFactory = new BindingConverterFactoryImpl();
            bindingConverterFactory
                .addBindingConverters(getClassesByServiceLoader(BindingConverter.class));
            return bindingConverterFactory;
        }

        @Bean
        @ConditionalOnMissingBean
        public static BindingAdapterFactory bindingAdapterFactory() {
            BindingAdapterFactory bindingAdapterFactory = new BindingAdapterFactoryImpl();
            bindingAdapterFactory
                .addBindingAdapters(getClassesByServiceLoader(BindingAdapter.class));
            return bindingAdapterFactory;
        }

        @Bean
        @ConditionalOnMissingBean
        public static SofaRuntimeManager sofaRuntimeManager(Environment environment,
                                                            BindingConverterFactory bindingConverterFactory,
                                                            BindingAdapterFactory bindingAdapterFactory) {
            ClientFactoryInternal clientFactoryInternal = new ClientFactoryImpl();
            SofaRuntimeManager sofaRuntimeManager = new StandardSofaRuntimeManager(
                environment.getProperty("spring.application.name"), Thread.currentThread()
                    .getContextClassLoader(), clientFactoryInternal);
            sofaRuntimeManager.getComponentManager().registerComponentClient(
                ReferenceClient.class,
                new ReferenceClientImpl(sofaRuntimeManager.getSofaRuntimeContext(),
                    bindingConverterFactory, bindingAdapterFactory));
            sofaRuntimeManager.getComponentManager().registerComponentClient(
                ServiceClient.class,
                new ServiceClientImpl(sofaRuntimeManager.getSofaRuntimeContext(),
                    bindingConverterFactory, bindingAdapterFactory));
            SofaFramework.registerSofaRuntimeManager(sofaRuntimeManager);
            return sofaRuntimeManager;
        }

        public static <T> Set<T> getClassesByServiceLoader(Class<T> clazz) {
            ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);

            Set<T> result = new HashSet<>();
            for (T t : serviceLoader) {
                result.add(t);
            }
            return result;
        }
    }
}
///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.alipay.sofa.boot.isle.test;
//
//import com.alipay.sofa.boot.isle.spring.SofaModuleContextLifecycle;
//import com.alipay.sofa.boot.isle.stage.DefaultPipelineContext;
//import com.alipay.sofa.boot.isle.stage.ModelCreatingStage;
//import com.alipay.sofa.boot.isle.stage.PipelineContext;
//import com.alipay.sofa.boot.isle.stage.PipelineStage;
//import com.alipay.sofa.boot.isle.test.util.AddCustomJar;
//import com.alipay.sofa.boot.isle.test.util.SeparateClassLoaderTestRunner;
//import com.alipay.sofa.boot.isle.deployment.DeploymentException;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.support.DefaultListableBeanFactory;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.TestPropertySource;
//
//import java.lang.reflect.Field;
//import java.util.List;
//
///**
// * @author huzijie
// * @version DuplicateModuleTest.java, v 0.1 2021年11月24日 5:18 下午 huzijie Exp $
// */
//@RunWith(SeparateClassLoaderTestRunner.class)
//@AddCustomJar({ "dev-module-0.1.0.jar", "dup-module-0.1.0.jar" })
//@TestPropertySource(locations = "/config/application.properties", properties = {
//                                                                                "com.alipay.sofa.boot.allowModuleOverriding=true",
//                                                                                "spring.main.allow-bean-definition-overriding=true" })
//@Import(DuplicateModuleTest.DuplicateModuleTestConfiguration.class)
//public class DuplicateModuleTest {
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    @Autowired
//    private ModelCreatingStage modelCreatingStage;
//
//    @Test
//    public void test() {
//        try {
//            ((DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory())
//                .setAllowBeanDefinitionOverriding(true);
//            Field field = ModelCreatingStage.class.getDeclaredField("allowModuleOverriding");
//            field.setAccessible(true);
//            field.set(modelCreatingStage, false);
//            modelCreatingStage.process();
//            Assert.fail();
//        } catch (Exception e) {
//            Assert.assertTrue(e instanceof DeploymentException);
//            Assert
//                .assertTrue(e
//                    .getMessage()
//                    .contains(
//                        "SOFA-BOOT-01-11006: Cannot register module deployment for module name '[com.alipay.sofa.dev]': replacing '[dev-module-0.1.0.jar]' with '[dup-module-0.1.0.jar]'"));
//        }
//    }
//
//    @Import(ModelCreatingStageTest.ModelCreatingStageTestConfiguration.class)
//    @Configuration(proxyBeanMethods = false)
//    static class DuplicateModuleTestConfiguration {
//        // ignore
//
//        @Bean
//        @ConditionalOnMissingBean
//        public PipelineContext pipelineContext(List<PipelineStage> stageList) {
//            return new DefaultPipelineContext(stageList);
//        }
//
//        @Bean
//        @ConditionalOnMissingBean
//        public SofaModuleContextLifecycle sofaModuleContextLifecycle(PipelineContext pipelineContext) {
//            return new SofaModuleContextLifecycle(pipelineContext);
//        }
//    }
//
//}

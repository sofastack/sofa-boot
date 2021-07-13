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

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.util.BeanDefinitionUtil;
import com.alipay.sofa.isle.spring.SofaModuleBeanFactoryPostProcessor;
import com.alipay.sofa.isle.spring.share.SofaModulePostProcessorShareFilter;
import com.alipay.sofa.isle.spring.share.SofaModulePostProcessorShareManager;
import com.alipay.sofa.isle.spring.share.UnshareSofaModulePostProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by TomorJM on 2019-10-09.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = PostProcessorShareTest.ProcessorConfig.class)
public class PostProcessorShareTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void test() {
        Map<String, BeanDefinition> processors = (Map<String, BeanDefinition>) context.getBean(SofaBootConstants.PROCESSORS_OF_ROOT_APPLICATION_CONTEXT);
        processors.forEach((k, v) -> {
            Class cls = BeanDefinitionUtil.resolveBeanClassType(v);
            Assert.assertTrue(!ProcessorConfig.TestA.class.equals(cls));
            Assert.assertTrue(!ProcessorConfig.TestB.class.equals(cls));
            Assert.assertTrue(!ProcessorConfig.TestC.class.equals(cls));
            Assert.assertTrue(!ProcessorConfig.TestD.class.equals(cls));
        });
        Assert.assertTrue(processors.keySet().contains("testE"));
    }

    @Configuration(proxyBeanMethods = false)
    static class ProcessorConfig {

        @Bean
        @ConditionalOnMissingBean
        public SofaModulePostProcessorShareManager sofaModulePostProcessorShareManager(ApplicationContext applicationContext) {
            return new SofaModulePostProcessorShareManager(
                (AbstractApplicationContext) applicationContext);
        }

        @Bean
        @ConditionalOnMissingBean
        public static SofaModuleBeanFactoryPostProcessor sofaModuleBeanFactoryPostProcessor(SofaModulePostProcessorShareManager shareManager) {
            return new SofaModuleBeanFactoryPostProcessor(shareManager);
        }

        @Bean
        @ConditionalOnMissingBean
        public TestFilter testFilter() {
            return new TestFilter();
        }

        @Bean
        @ConditionalOnMissingBean
        public TestA testA() {
            return new TestA();
        }

        @Bean
        @ConditionalOnMissingBean
        public TestB testB() {
            return new TestB();
        }

        @Bean
        @ConditionalOnMissingBean
        public TestC testC() {
            return new TestC();
        }

        @Bean(value = "testD")
        @ConditionalOnMissingBean
        public TestD testD() {
            return new TestD();
        }

        @Bean
        @ConditionalOnMissingBean
        public TestE testE() {
            return new TestE();
        }

        public class TestFilter implements SofaModulePostProcessorShareFilter {

            @Override
            public List<Class<? extends BeanPostProcessor>> filterBeanPostProcessorClass() {
                return Arrays.asList(TestA.class);
            }

            @Override
            public List<Class<? extends BeanFactoryPostProcessor>> filterBeanFactoryPostProcessorClass() {
                return Arrays.asList(TestB.class);
            }

            @Override
            public List<String> filterBeanName() {
                return Arrays.asList("testD");
            }
        }

        public class TestA implements BeanPostProcessor {
        }

        public class TestB implements BeanFactoryPostProcessor {
            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                                                                                           throws BeansException {
            }
        }

        @UnshareSofaModulePostProcessor
        public class TestC {

        }

        static class TestD implements BeanPostProcessor {

        }

        public class TestE implements BeanPostProcessor {

        }
    }

}

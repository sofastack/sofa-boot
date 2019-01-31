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
package com.alipay.sofa.isle.integration;

import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.bean.TestBean;
import com.alipay.sofa.isle.constants.SofaModuleFrameworkConstants;
import com.alipay.sofa.isle.processor.FactoryBeanPostProcessor;
import com.alipay.sofa.isle.processor.SampleBeanPostProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author ruoshan
 * @since 2.6.1
 */
@SpringBootTest(classes = SofaBootTestApplication.class)
@RunWith(SpringRunner.class)
public class PostProcessorTest implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private TestBean           testBean;

    @Test
    public void testBeanIsPostProcessed() {
        Assert.assertTrue(testBean.isPostProcessed());
    }

    @Test
    public void testFactoryBeanPostProcessor() {
        ApplicationRuntimeModel applicationRuntimeModel = (ApplicationRuntimeModel) applicationContext
            .getBean(SofaModuleFrameworkConstants.APPLICATION);
        GenericApplicationContext subApplicationContext = (GenericApplicationContext) applicationRuntimeModel
            .getInstalled().get(0).getApplicationContext();
        SampleBeanPostProcessor sampleBeanPostProcessor = null;
        FactoryBeanPostProcessor factoryBeanPostProcessor = null;
        for (BeanPostProcessor beanPostProcessor : ((DefaultListableBeanFactory) subApplicationContext
            .getBeanFactory()).getBeanPostProcessors()) {
            if (SampleBeanPostProcessor.class.getName().equals(
                beanPostProcessor.getClass().getName())) {
                sampleBeanPostProcessor = (SampleBeanPostProcessor) beanPostProcessor;
            } else if (beanPostProcessor instanceof FactoryBeanPostProcessor) {
                factoryBeanPostProcessor = (FactoryBeanPostProcessor) beanPostProcessor;
            }
        }
        Assert.assertNotNull(sampleBeanPostProcessor);
        Assert.assertNotNull(factoryBeanPostProcessor);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
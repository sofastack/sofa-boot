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
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.profile.DefaultSofaModuleProfileChecker;
import com.alipay.sofa.isle.profile.SofaModuleProfileChecker;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.stage.ModelCreatingStage;
import com.alipay.sofa.runtime.component.impl.StandardSofaRuntimeManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/10/26
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ModelCreatingStageTest implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Test
    public void test() throws Exception {
        applicationContext.getBean("modelCreatingStage", ModelCreatingStage.class).process();
        ApplicationRuntimeModel application = applicationContext.getBean(
            SofaBootConstants.APPLICATION, ApplicationRuntimeModel.class);
        Assert.assertNotNull(application.getSofaRuntimeContext());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(SofaModuleProperties.class)
    static class ModelCreatingStageTestConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ModelCreatingStage modelCreatingStage(ApplicationContext applicationContext) {
            return new ModelCreatingStage((AbstractApplicationContext) applicationContext);
        }

        @Bean
        @ConditionalOnMissingBean
        public SofaModuleProfileChecker sofaModuleProfileChecker() {
            return new DefaultSofaModuleProfileChecker();
        }

        @Bean
        @ConditionalOnMissingBean
        public SofaRuntimeManager sofaRuntimeManager() {
            SofaRuntimeManager sofaRuntimeManager = new StandardSofaRuntimeManager("test",
                ModelCreatingStageTest.class.getClassLoader(), null);
            return sofaRuntimeManager;
        }
    }
}

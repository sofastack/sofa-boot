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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.isle.profile.DefaultSofaModuleProfileChecker;
import com.alipay.sofa.isle.profile.SofaModuleProfileChecker;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.stage.DefaultPipelineContext;
import com.alipay.sofa.isle.stage.ModelCreatingStage;
import com.alipay.sofa.isle.stage.ModuleLogOutputStage;
import com.alipay.sofa.isle.stage.PipelineContext;
import com.alipay.sofa.isle.stage.PipelineStage;
import com.alipay.sofa.isle.stage.SpringContextInstallStage;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = "spring.application.name=PipelineStageTest")
public class PipelineStageTest {

    @Autowired
    private PipelineContext pipelineContext;

    @Test
    public void testStage() {
        List<PipelineStage> pipelineStages = ((DefaultPipelineContext) pipelineContext)
            .getStageList();
        Assert.assertEquals(4, pipelineStages.size());
        Assert.assertEquals(10000, pipelineStages.get(0).getOrder());
        Assert.assertEquals(20000, pipelineStages.get(1).getOrder());
        Assert.assertEquals(25000, pipelineStages.get(2).getOrder());
        Assert.assertEquals(30000, pipelineStages.get(3).getOrder());
    }

    @Configuration
    @EnableConfigurationProperties(SofaModuleProperties.class)
    static class StageTestConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ModelCreatingStage modelCreatingStage(ApplicationContext applicationContext) {
            return new ModelCreatingStage((AbstractApplicationContext) applicationContext);
        }

        @Bean
        @ConditionalOnMissingBean
        public SpringContextInstallStage springContextInstallStage(ApplicationContext applicationContext) {
            return new SpringContextInstallStage((AbstractApplicationContext) applicationContext);
        }

        @Bean
        @ConditionalOnMissingBean
        public ModuleLogOutputStage moduleLogOutputStage(ApplicationContext applicationContext) {
            return new ModuleLogOutputStage((AbstractApplicationContext) applicationContext);
        }

        @Bean
        @ConditionalOnMissingBean
        public PipelineContext pipelineContext() {
            return new DefaultPipelineContext();
        }

        @Bean
        @ConditionalOnMissingBean
        public SofaModuleProfileChecker sofaModuleProfileChecker() {
            return new DefaultSofaModuleProfileChecker();
        }

        @Bean
        public PipelineStage mockPipelineStage() {
            return new TestPipelineStage();
        }
    }

    static class TestPipelineStage implements PipelineStage {
        @Override
        public void process() throws Exception {

        }

        @Override
        public String getName() {
            return TestPipelineStage.class.getName();
        }

        /**
         * {@link ModelCreatingStage} < {@link SpringContextInstallStage}
         * < {@link TestPipelineStage} < {@link ModuleLogOutputStage}
         * @return
         */
        @Override
        public int getOrder() {
            return 25000;
        }
    }
}
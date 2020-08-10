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
package com.alipay.sofa.startup.test;

import com.alipay.sofa.startup.SofaStartupContext;
import com.alipay.sofa.startup.test.beans.InitCostBean;
import com.alipay.sofa.startup.test.spring.SofaStartupAutoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: Zhijie
 * @since: 2020/7/13
 */
public class BeanInitCostTest {

    @Test
    public void testBeanCost() {
        ApplicationContext ctx = initApplicationContext(0);
        SofaStartupContext sofaStartupContext = ctx.getBean(SofaStartupContext.class);
        Assert.assertTrue(sofaStartupContext.getBeanInitCost() >= InitCostBean.INIT_COST_TIME);
        final Long initCostTime = sofaStartupContext.getBeanInitDetail().get(
            sofaStartupContext.getModuleName() + "_initCostBean");
        Assert.assertTrue(initCostTime >= InitCostBean.INIT_COST_TIME);
    }

    @Test
    public void testBeanCostUnderThreshold() {
        ApplicationContext ctx = initApplicationContext(InitCostBean.INIT_COST_TIME + 1000);
        SofaStartupContext sofaStartupContext = ctx.getBean(SofaStartupContext.class);
        Assert.assertTrue(sofaStartupContext.getBeanInitCost() >= InitCostBean.INIT_COST_TIME);
        final Long initCostTime = sofaStartupContext.getBeanInitDetail().get(
            sofaStartupContext.getModuleName() + "_initCostBean");
        Assert.assertNull(initCostTime);
    }

    private ApplicationContext initApplicationContext(long cost) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("spring.application.name", "SofaStartupContextBeanCostTest");
        properties.put("com.alipay.sofa.boot.startup.bean-init-cost-threshold", cost);
        SpringApplication springApplication = new SpringApplication(
            SofaStartupContextBeanCostTestConfiguration.class);
        springApplication.setDefaultProperties(properties);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        return springApplication.run();
    }

    @Configuration
    @Import(SofaStartupAutoConfiguration.class)
    static class SofaStartupContextBeanCostTestConfiguration {

        @Bean
        public InitCostBean initCostBean() {
            return new InitCostBean();
        }
    }

}

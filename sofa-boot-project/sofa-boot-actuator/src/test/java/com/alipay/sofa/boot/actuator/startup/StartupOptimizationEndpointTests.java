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
package com.alipay.sofa.boot.actuator.startup;

import com.alipay.sofa.boot.startup.BeanInitInfo;
import com.alipay.sofa.boot.startup.BeanStat;
import com.alipay.sofa.boot.startup.StartupOptimizer;
import com.alipay.sofa.boot.startup.StartupReporter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests for {@link StartupOptimizationEndpoint}.
 *
 * @author OpenAI
 */
public class StartupOptimizationEndpointTests {

    @Test
    void analyzeAndSlowBeans() {
        StartupReporter startupReporter = new StartupReporter();
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBeanDefinition("slow", new RootBeanDefinition(Object.class));
        startupReporter.addCommonStartupStat(beanStat("slow", 600));
        StartupOptimizationEndpoint endpoint = new StartupOptimizationEndpoint(
            new StartupOptimizer(startupReporter), context);

        assertThat(endpoint.analyze().getRecommendations()).hasSize(1);
        List<BeanInitInfo> slowBeans = endpoint.slowBeans("slow-beans", 1);

        assertThat(slowBeans).extracting(BeanInitInfo::getBeanName).containsExactly("slow");
    }

    @Test
    void rejectsUnsupportedSelector() {
        StartupReporter startupReporter = new StartupReporter();
        StartupOptimizationEndpoint endpoint = new StartupOptimizationEndpoint(
            new StartupOptimizer(startupReporter), new GenericApplicationContext());

        assertThatIllegalArgumentException().isThrownBy(() -> endpoint.slowBeans("unknown", 1));
    }

    private BeanStat beanStat(String beanName, long cost) {
        BeanStat beanStat = new BeanStat();
        beanStat.setType(StartupReporter.SPRING_BEANS_INSTANTIATE);
        beanStat.setName(beanName);
        beanStat.setCost(cost);
        beanStat.setBeanClassName(Object.class.getName());
        return beanStat;
    }
}

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
package com.alipay.sofa.boot.startup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StartupOptimizer}.
 *
 * @author OpenAI
 */
public class StartupOptimizerTests {

    @Test
    void analyzeStartupBottlenecksGeneratesSlowBeanRecommendation() {
        StartupReporter startupReporter = new StartupReporter();
        StartupOptimizer startupOptimizer = new StartupOptimizer(startupReporter);
        GenericApplicationContext context = new GenericApplicationContext();
        registerBeanDefinition(context, "slowBean", false);
        registerBeanDefinition(context, "asyncBean", true);
        startupReporter.addCommonStartupStat(beanStat("slowBean", 600));
        startupReporter.addCommonStartupStat(beanStat("asyncBean", 700));

        StartupReport report = startupOptimizer.analyzeStartupBottlenecks(context);

        assertThat(report.getSlowestBeans()).extracting(BeanInitInfo::getBeanName)
            .containsExactly("asyncBean", "slowBean");
        assertThat(report.getSequentialBeans()).extracting(BeanInitInfo::getBeanName)
            .containsExactly("slowBean");
        assertThat(report.getRecommendations()).hasSize(1);
        assertThat(report.getRecommendations().get(0).getBeanName()).isEqualTo("slowBean");
    }

    @Test
    void findSlowBeansRespectsTopLimitAndEmptyTop() {
        StartupReporter startupReporter = new StartupReporter();
        StartupOptimizer startupOptimizer = new StartupOptimizer(startupReporter);
        GenericApplicationContext context = new GenericApplicationContext();
        registerBeanDefinition(context, "one", false);
        registerBeanDefinition(context, "two", false);
        startupReporter.addCommonStartupStat(beanStat("one", 100));
        startupReporter.addCommonStartupStat(beanStat("two", 200));

        assertThat(startupOptimizer.findSlowBeans(context, 1)).extracting(BeanInitInfo::getBeanName)
            .containsExactly("two");
        assertThat(startupOptimizer.findSlowBeans(context, 0)).isEmpty();
    }

    @Test
    void collectsNestedBeanStats() {
        StartupReporter startupReporter = new StartupReporter();
        StartupOptimizer startupOptimizer = new StartupOptimizer(startupReporter);
        GenericApplicationContext context = new GenericApplicationContext();
        registerBeanDefinition(context, "nested", false);
        ChildrenStat<BeanStat> root = new ChildrenStat<>();
        root.addChild(beanStat("nested", 300));
        startupReporter.addCommonStartupStat(root);

        assertThat(startupOptimizer.findSequentialBeans(context)).extracting(BeanInitInfo::getBeanName)
            .containsExactly("nested");
    }

    private void registerBeanDefinition(GenericApplicationContext context, String beanName,
                                        boolean async) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition(Object.class);
        if (async) {
            beanDefinition.setAttribute(StartupOptimizer.ASYNC_INIT_METHOD_NAME, "init");
        }
        context.registerBeanDefinition(beanName, beanDefinition);
    }

    private BeanStat beanStat(String beanName, long cost) {
        BeanStat beanStat = new BeanStat();
        beanStat.setType(StartupReporter.SPRING_BEANS_INSTANTIATE);
        beanStat.setName(beanName);
        beanStat.setCost(cost);
        beanStat.setBeanClassName(Object.class.getName());
        beanStat.putAttribute("classType", Object.class.getName());
        return beanStat;
    }
}

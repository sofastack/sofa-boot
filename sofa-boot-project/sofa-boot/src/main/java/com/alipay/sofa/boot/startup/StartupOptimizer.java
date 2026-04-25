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

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Analyzer that creates startup bottleneck reports and async init recommendations.
 *
 * @author OpenAI
 */
public class StartupOptimizer {

    static final String           ASYNC_INIT_METHOD_NAME         = "async-init-method-name";

    private static final long     ASYNC_RECOMMENDATION_THRESHOLD = 500;

    private final StartupReporter startupReporter;

    public StartupOptimizer(StartupReporter startupReporter) {
        this.startupReporter = startupReporter;
    }

    public StartupReport analyzeStartupBottlenecks(ApplicationContext context) {
        StartupReport report = new StartupReport();
        report.setSequentialBeans(findSequentialBeans(context));
        report.setSlowestBeans(findSlowBeans(context, 10));
        report.setRecommendations(generateRecommendations(report));
        return report;
    }

    public List<BeanInitInfo> findSequentialBeans(ApplicationContext context) {
        return findBeanInitInfos(context).stream().filter(beanInitInfo -> !beanInitInfo.isAsync())
            .collect(Collectors.toList());
    }

    public List<BeanInitInfo> findSlowBeans(ApplicationContext context, int top) {
        if (top <= 0) {
            return List.of();
        }
        return findBeanInitInfos(context).stream()
            .sorted(Comparator.comparingLong(BeanInitInfo::getInitTime).reversed()).limit(top)
            .collect(Collectors.toList());
    }

    public List<StartupRecommendation> generateRecommendations(StartupReport report) {
        List<StartupRecommendation> recommendations = new ArrayList<>();
        for (BeanInitInfo slowBean : report.getSlowestBeans()) {
            if (slowBean.getInitTime() > ASYNC_RECOMMENDATION_THRESHOLD && !slowBean.isAsync()) {
                recommendations.add(new StartupRecommendation("ASYNC_CANDIDATE", slowBean
                    .getBeanName(), String.format(
                    "Bean '%s' init cost %dms, consider adding @SofaAsyncInit.",
                    slowBean.getBeanName(), slowBean.getInitTime())));
            }
        }
        return recommendations;
    }

    private List<BeanInitInfo> findBeanInitInfos(ApplicationContext context) {
        List<BeanInitInfo> beanInitInfos = new ArrayList<>();
        for (BaseStat stageStat : startupReporter.getStartupStaticsModel().getStageStats()) {
            collectBeanInitInfos(stageStat, context, beanInitInfos);
        }
        return beanInitInfos;
    }

    private void collectBeanInitInfos(BaseStat stat, ApplicationContext context,
                                      List<BeanInitInfo> beanInitInfos) {
        if (stat instanceof BeanStat beanStat
            && StartupReporter.SPRING_BEAN_INSTANTIATE_TYPES.contains(beanStat.getType())) {
            beanInitInfos.add(toBeanInitInfo(beanStat, context));
        }
        if (stat instanceof ChildrenStat<?> childrenStat) {
            childrenStat.getChildren()
                .forEach(child -> collectBeanInitInfos(child, context, beanInitInfos));
        }
    }

    private BeanInitInfo toBeanInitInfo(BeanStat beanStat, ApplicationContext context) {
        BeanInitInfo beanInitInfo = new BeanInitInfo();
        beanInitInfo.setBeanName(beanStat.getName());
        beanInitInfo.setBeanClassName(getBeanClassName(beanStat));
        beanInitInfo.setInitTime(beanStat.getCost());
        beanInitInfo.setAttributes(beanStat.getAttributes());
        beanInitInfo.setAsync(isAsyncBean(context, beanStat.getName()));
        return beanInitInfo;
    }

    private String getBeanClassName(BeanStat beanStat) {
        String classType = beanStat.getAttribute("classType");
        if (StringUtils.hasText(classType)) {
            return classType;
        }
        return beanStat.getBeanClassName();
    }

    private boolean isAsyncBean(ApplicationContext context, String beanName) {
        if (!(context instanceof ConfigurableApplicationContext configurableApplicationContext)
            || !StringUtils.hasText(beanName)) {
            return false;
        }
        ConfigurableListableBeanFactory beanFactory = configurableApplicationContext
            .getBeanFactory();
        if (!beanFactory.containsBeanDefinition(beanName)) {
            return false;
        }
        Object asyncInitMethodName = beanFactory.getBeanDefinition(beanName)
            .getAttribute(ASYNC_INIT_METHOD_NAME);
        return asyncInitMethodName instanceof String methodName && StringUtils.hasText(methodName);
    }
}

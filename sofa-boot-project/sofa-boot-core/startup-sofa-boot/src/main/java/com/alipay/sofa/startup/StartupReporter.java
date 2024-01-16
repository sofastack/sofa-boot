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
package com.alipay.sofa.startup;

import com.alipay.sofa.boot.startup.BaseStat;
import com.alipay.sofa.boot.startup.BeanStat;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Collect and report the costs
 *
 * @author Zhijie
 * @since 2020/7/8
 */
public class StartupReporter {

    public static final String             SPRING_BEANS_INSTANTIATE                       = "spring.beans.instantiate";

    public static final String             SPRING_BEANS_SMART_INSTANTIATE                 = "spring.beans.smart-initialize";

    public static final String             SPRING_CONTEXT_BEANDEF_REGISTRY_POST_PROCESSOR = "spring.context.beandef-registry.post-process";

    public static final String             SPRING_CONTEXT_BEAN_FACTORY_POST_PROCESSOR     = "spring.context.bean-factory.post-process";

    public static final String             SPRING_BEAN_POST_PROCESSOR                     = "spring.context.beans.post-process";

    public static final String             SPRING_CONFIG_CLASSES_ENHANCE                  = "spring.context.config-classes.enhance";

    public static final Collection<String> SPRING_BEAN_INSTANTIATE_TYPES                  = new HashSet<>();

    public static final Collection<String> SPRING_CONTEXT_POST_PROCESSOR_TYPES            = new HashSet<>();

    public static final Collection<String> SPRING_CONFIG_CLASSES_ENHANCE_TYPES            = new HashSet<>();

    static {
        SPRING_BEAN_INSTANTIATE_TYPES.add(SPRING_BEANS_INSTANTIATE);
        SPRING_BEAN_INSTANTIATE_TYPES.add(SPRING_BEANS_SMART_INSTANTIATE);
        SPRING_CONTEXT_POST_PROCESSOR_TYPES.add(SPRING_CONTEXT_BEANDEF_REGISTRY_POST_PROCESSOR);
        SPRING_CONTEXT_POST_PROCESSOR_TYPES.add(SPRING_CONTEXT_BEAN_FACTORY_POST_PROCESSOR);
        SPRING_CONFIG_CLASSES_ENHANCE_TYPES.add(SPRING_CONFIG_CLASSES_ENHANCE);
        SPRING_CONFIG_CLASSES_ENHANCE_TYPES.add(SPRING_BEAN_POST_PROCESSOR);
    }

    private final StartupStaticsModel      startupStaticsModel                            = new StartupStaticsModel();

    private int                            bufferSize                                     = 4096;

    private int                            beanInitCostThreshold                          = 100;

    public StartupReporter() {
        startupStaticsModel.setApplicationBootTime(ManagementFactory.getRuntimeMXBean()
            .getStartTime());
    }

    /**
     * Bind the environment to the {@link StartupReporter}.
     * @param environment the environment to bind
     */
    public void bindToStartupReporter(ConfigurableEnvironment environment) {
        try {
            Binder.get(environment).bind("com.alipay.sofa.boot.startup", Bindable.ofInstance(this));
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot bind to StartupReporter", ex);
        }
    }

    public void setAppName(String appName) {
        this.startupStaticsModel.setAppName(appName);
    }

    /**
     * End the application boot
     */
    public void applicationBootFinish() {
        startupStaticsModel.setApplicationBootElapsedTime(ManagementFactory.getRuntimeMXBean()
            .getUptime());
        startupStaticsModel.getStageStats().sort((o1, o2) -> {
            if (o1.getStartTime() == o2.getStartTime()) {
                return 0;
            }
            return o1.getStartTime() > o2.getStartTime() ? 1 : -1;
        });
    }

    /**
     * Add common startup stat to report
     * @param stat the added CommonStartupStat
     */
    public void addCommonStartupStat(BaseStat stat) {
        startupStaticsModel.getStageStats().add(stat);
    }

    /**
     * Find the stage reported in sofaStartupStaticsModel by Name
     * @param stageName stageName
     * @return the reported stage, return null if can't find the stage
     */
    public BaseStat getStageNyName(String stageName) {
        return startupStaticsModel.getStageStats().stream().filter(commonStartupStat -> commonStartupStat.getName().equals(stageName))
                .findFirst().orElse(null);
    }

    /**
     * Convert {@link BufferingApplicationStartup} to {@link BeanStat} list.
     * @param context the {@link ConfigurableApplicationContext}.
     * @return list of bean stats.
     */
    public List<BeanStat> generateBeanStats(ConfigurableApplicationContext context) {

        List<BeanStat> rootBeanStatList = new ArrayList<>();
        ApplicationStartup applicationStartup = context.getApplicationStartup();
        if (applicationStartup instanceof BufferingApplicationStartup) {
            BufferingApplicationStartup bufferingApplicationStartup = (BufferingApplicationStartup) applicationStartup;
            Map<Long, BeanStat> beanStatIdMap = new HashMap<>();

            StartupTimeline startupTimeline = bufferingApplicationStartup.drainBufferedTimeline();

            // filter bean initializers by cost
            List<StartupTimeline.TimelineEvent> timelineEvents = startupTimeline.getEvents();

            // convert startup to bean stats
            timelineEvents.forEach(timelineEvent -> {
                BeanStat beanStat = eventToBeanStat(timelineEvent);
                rootBeanStatList.add(beanStat);
                beanStatIdMap.put(timelineEvent.getStartupStep().getId(), beanStat);
            });

            // build stat tree
            timelineEvents.forEach(timelineEvent -> {
                BeanStat parentBeanStat = beanStatIdMap.get(timelineEvent.getStartupStep().getParentId());
                BeanStat beanStat = beanStatIdMap.get(timelineEvent.getStartupStep().getId());

                if (parentBeanStat != null) {
                    // parent node real cost subtract child node
                    parentBeanStat.setRealRefreshElapsedTime(parentBeanStat.getRealRefreshElapsedTime()
                            - beanStat.getCost());
                    // remove child node in root list
                    rootBeanStatList.remove(beanStat);
                    // if child list cost is larger than threshold, put it to parent children.
                    if (filterBeanInitializeByCost(beanStat)) {
                        parentBeanStat.addChild(beanStat);
                    }
                } else {
                    // if root node is less than threshold, remove it.
                    if (!filterBeanInitializeByCost(beanStat)) {
                        rootBeanStatList.remove(beanStat);
                    }
                }
            });
        }
        return rootBeanStatList;
    }

    private boolean filterBeanInitializeByCost(BeanStat beanStat) {
        String name = beanStat.getBeanType();
        if (SPRING_BEAN_INSTANTIATE_TYPES.contains(name)
            || SPRING_CONTEXT_POST_PROCESSOR_TYPES.contains(name)
            || SPRING_CONFIG_CLASSES_ENHANCE_TYPES.contains(name)) {
            return beanStat.getCost() >= beanInitCostThreshold;
        } else {
            return true;
        }
    }

    private BeanStat eventToBeanStat(StartupTimeline.TimelineEvent timelineEvent) {
        BeanStat beanStat = new BeanStat();
        beanStat.setStartTime(timelineEvent.getStartTime().toEpochMilli());
        beanStat.setEndTime(timelineEvent.getEndTime().toEpochMilli());
        beanStat.setCost(timelineEvent.getDuration().toMillis());
        beanStat.setRealRefreshElapsedTime(beanStat.getCost());

        // for compatibility
        beanStat.setBeanRefreshStartTime(beanStat.getStartTime());
        beanStat.setBeanRefreshEndTime(beanStat.getEndTime());
        beanStat.setRefreshElapsedTime(beanStat.getCost());

        String name = timelineEvent.getStartupStep().getName();
        beanStat.setBeanType(name);
        if (SPRING_BEAN_INSTANTIATE_TYPES.contains(name)) {
            StartupStep.Tags tags = timelineEvent.getStartupStep().getTags();
            String beanName = getValueFromTags(tags, "beanName");
            beanStat.setName(beanName);
        } else if (SPRING_CONTEXT_POST_PROCESSOR_TYPES.contains(name)) {
            StartupStep.Tags tags = timelineEvent.getStartupStep().getTags();
            String beanName = getValueFromTags(tags, "postProcessor");
            beanStat.setName(beanName);
        } else {
            beanStat.setName(name);
        }
        timelineEvent.getStartupStep().getTags().forEach(tag -> beanStat.putAttribute(tag.getKey(), tag.getValue()));

        // for compatibility
        beanStat.setBeanClassName(beanStat.getName());

        return beanStat;
    }

    private String getValueFromTags(StartupStep.Tags tags, String key) {
        for (StartupStep.Tag tag : tags) {
            if (Objects.equals(key, tag.getKey())) {
                return tag.getValue();
            }
        }
        return null;
    }

    /**
     * Build the com.alipay.sofa.startup.SofaStartupReporter.SofaStartupCostModel
     * @return the time cost model
     */
    public StartupStaticsModel report() {
        return startupStaticsModel;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getBeanInitCostThreshold() {
        return beanInitCostThreshold;
    }

    public void setBeanInitCostThreshold(int beanInitCostThreshold) {
        this.beanInitCostThreshold = beanInitCostThreshold;
    }

    public static class StartupStaticsModel {
        private String         appName;
        private long           applicationBootElapsedTime = 0;
        private long           applicationBootTime;
        private List<BaseStat> stageStats                 = new ArrayList<>();

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public long getApplicationBootElapsedTime() {
            return applicationBootElapsedTime;
        }

        public void setApplicationBootElapsedTime(long applicationBootElapsedTime) {
            this.applicationBootElapsedTime = applicationBootElapsedTime;
        }

        public long getApplicationBootTime() {
            return applicationBootTime;
        }

        public void setApplicationBootTime(long applicationBootTime) {
            this.applicationBootTime = applicationBootTime;
        }

        public List<BaseStat> getStageStats() {
            return stageStats;
        }

        public void setStageStats(List<BaseStat> stageStats) {
            this.stageStats = stageStats;
        }
    }
}

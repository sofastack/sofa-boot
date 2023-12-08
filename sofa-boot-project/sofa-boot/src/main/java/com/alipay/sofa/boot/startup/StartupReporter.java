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

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.context.metrics.buffering.StartupTimeline;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The base component to collect and report the startup costs.
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

    public static final Collection<String> SPRING_BEAN_INSTANTIATE_TYPES                  = Set
                                                                                              .of(SPRING_BEANS_INSTANTIATE,
                                                                                                  SPRING_BEANS_SMART_INSTANTIATE);

    public static final Collection<String> SPRING_CONTEXT_POST_PROCESSOR_TYPES            = Set
                                                                                              .of(SPRING_CONTEXT_BEANDEF_REGISTRY_POST_PROCESSOR,
                                                                                                  SPRING_CONTEXT_BEAN_FACTORY_POST_PROCESSOR);

    public static final Collection<String> SPRING_CONFIG_CLASSES_ENHANCE_TYPES            = Set
                                                                                              .of(SPRING_CONFIG_CLASSES_ENHANCE,
                                                                                                  SPRING_BEAN_POST_PROCESSOR);

    private final StartupStaticsModel      startupStaticsModel;

    private final List<BeanStatCustomizer> beanStatCustomizers;

    private int                            bufferSize                                     = 4096;

    private int                            costThreshold                                  = 50;

    public StartupReporter() {
        this.startupStaticsModel = new StartupStaticsModel();
        this.startupStaticsModel.setApplicationBootTime(ManagementFactory.getRuntimeMXBean()
            .getStartTime());
        this.beanStatCustomizers = SpringFactoriesLoader.forDefaultResourceLocation(
            StartupReporter.class.getClassLoader()).load(BeanStatCustomizer.class);
    }

    /**
     * Bind the environment to the {@link StartupReporter}.
     * @param environment the environment to bind
     */
    public void bindToStartupReporter(ConfigurableEnvironment environment) {
        try {
            Binder.get(environment).bind("sofa.boot.startup", Bindable.ofInstance(this));
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
     * @return the reported stage, return null when can't find the stage
     */
    public BaseStat getStageNyName(String stageName) {
        return startupStaticsModel.getStageStats().stream().filter(commonStartupStat -> commonStartupStat.getName().equals(stageName))
                .findFirst().orElse(null);
    }

    /**
     * Return the {@link StartupStaticsModel startupStaticsModel} as a snapshot of currently buffered
     * steps.
     * <p>
     * This will not remove details from the model, see {@link #getStartupStaticsModel()}
     * for its counterpart.
     * @return a snapshot of currently buffered stages.
     */
    public StartupStaticsModel getStartupStaticsModel() {
        return startupStaticsModel;
    }

    /**
     * Return the {@link StartupTimeline timeline} by pulling stages from the model.
     * <p>
     * This removes steps from the buffer, see {@link #getStartupStaticsModel()} for its
     * read-only counterpart.
     * @return buffered stages drained from the buffer.
     */
    public StartupStaticsModel drainStartupStaticsModel() {
        StartupStaticsModel startupStaticsModel = new StartupStaticsModel();
        startupStaticsModel.setAppName(this.startupStaticsModel.getAppName());
        startupStaticsModel.setApplicationBootElapsedTime(this.startupStaticsModel
            .getApplicationBootElapsedTime());
        startupStaticsModel.setApplicationBootTime(this.startupStaticsModel
            .getApplicationBootTime());
        List<BaseStat> stats = new ArrayList<>();
        Iterator<BaseStat> iterator = this.startupStaticsModel.getStageStats().iterator();
        while (iterator.hasNext()) {
            stats.add(iterator.next());
            iterator.remove();
        }
        startupStaticsModel.setStageStats(stats);
        return startupStaticsModel;
    }

    /**
     * Convert {@link BufferingApplicationStartup} to {@link BeanStat} list.
     * @param context the {@link ConfigurableApplicationContext}.
     * @return list of bean stats.
     */
    public List<BeanStat> generateBeanStats(ConfigurableApplicationContext context) {

        List<BeanStat> rootBeanStatList = new ArrayList<>();
        ApplicationStartup applicationStartup = context.getApplicationStartup();
        if (applicationStartup instanceof BufferingApplicationStartup bufferingApplicationStartup) {
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
                        customBeanStat(context, beanStat);
                    }
                } else {
                    // if root node is less than threshold, remove it.
                    if (!filterBeanInitializeByCost(beanStat)) {
                        rootBeanStatList.remove(beanStat);
                    } else {
                        customBeanStat(context, beanStat);
                    }
                }
            });
        }
        return rootBeanStatList;
    }

    private boolean filterBeanInitializeByCost(BeanStat beanStat) {
        String name = beanStat.getType();
        if (SPRING_BEAN_INSTANTIATE_TYPES.contains(name)
            || SPRING_CONTEXT_POST_PROCESSOR_TYPES.contains(name)
            || SPRING_CONFIG_CLASSES_ENHANCE_TYPES.contains(name)) {
            return beanStat.getCost() >= costThreshold;
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
        beanStat.setType(name);
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

    private BeanStat customBeanStat(ConfigurableApplicationContext context, BeanStat beanStat) {
        if (!context.isActive()) {
            return beanStat;
        }
        String type = beanStat.getType();
        if (SPRING_BEAN_INSTANTIATE_TYPES.contains(type)) {
            String beanName = beanStat.getName();
            Object bean = context.getBean(beanName);
            beanStat.putAttribute("classType", AopProxyUtils.ultimateTargetClass(bean).getName());

            BeanStat result = beanStat;
            for (BeanStatCustomizer customizer : beanStatCustomizers) {
                BeanStat current = customizer.customize(beanName, bean, result);
                if (current == null) {
                    return result;
                }
                result = current;
            }
            return result;
        } else {
            return beanStat;
        }
    }

    public int getCostThreshold() {
        return costThreshold;
    }

    public void setCostThreshold(int costThreshold) {
        this.costThreshold = costThreshold;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
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

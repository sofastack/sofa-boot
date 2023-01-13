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

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * The base component to collect and report the startup costs
 *
 * @author Zhijie
 * @since 2020/7/8
 */
public class StartupReporter {

    private final StartupStaticsModel startupStaticsModel;

    private boolean                   storeStatics  = false;

    private int                       costThreshold = 100;

    public StartupReporter() {
        this.startupStaticsModel = new StartupStaticsModel();
        this.startupStaticsModel.setApplicationBootTime(ManagementFactory.getRuntimeMXBean()
            .getStartTime());
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
     * @return the reported stage, return null if can't find the stage
     */
    public BaseStat getStageNyName(String stageName) {
        return startupStaticsModel.getStageStats().stream().filter(commonStartupStat -> commonStartupStat.getName().equals(stageName))
                .findFirst().orElse(null);
    }

    /**
     * Build the com.alipay.sofa.startup.SofaStartupReporter.SofaStartupCostModel
     * @return the time cost model
     */
    public StartupStaticsModel report() {
        return startupStaticsModel;
    }

    public void clear() {
        startupStaticsModel.clear();
    }

    public boolean isStoreStatics() {
        return storeStatics;
    }

    public void setStoreStatics(boolean storeStatics) {
        this.storeStatics = storeStatics;
    }

    public int getCostThreshold() {
        return costThreshold;
    }

    public void setCostThreshold(int costThreshold) {
        this.costThreshold = costThreshold;
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

        // todo clear all date
        public void clear() {

        }
    }
}

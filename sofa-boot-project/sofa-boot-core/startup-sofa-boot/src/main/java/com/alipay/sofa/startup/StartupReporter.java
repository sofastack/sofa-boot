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

import com.alipay.sofa.boot.startup.StageStat;
import org.springframework.core.env.Environment;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Collect and report the costs
 *
 * @author Zhijie
 * @since 2020/7/8
 */
public class StartupReporter {
    private final StartupStaticsModel startupStaticsModel = new StartupStaticsModel();

    public StartupReporter(Environment environment) {
        startupStaticsModel.setAppName(environment.getProperty("spring.application.name"));
        startupStaticsModel.setApplicationBootTime(ManagementFactory.getRuntimeMXBean()
            .getStartTime());
    }

    /**
     * End the application boot
     */
    public void applicationBootFinish() {
        startupStaticsModel.setApplicationBootElapsedTime(ManagementFactory.getRuntimeMXBean()
            .getUptime());
        startupStaticsModel.getStageStats().sort((o1, o2) -> o1.getStageStartTime() - o2.getStageStartTime() > 0 ? 1 : -1);
    }

    /**
     * Add common startup stat to report
     * @param stageStat the added CommonStartupStat
     */
    public void addCommonStartupStat(StageStat stageStat) {
        startupStaticsModel.getStageStats().add(stageStat);
    }

    /**
     * Find the stage reported in sofaStartupStaticsModel by Name
     * @param stageName stageName
     * @return the reported stage, return null if can't find the stage
     */
    public StageStat getStageNyName(String stageName) {
        return startupStaticsModel.getStageStats().stream().filter(commonStartupStat -> commonStartupStat.getStageName().equals(stageName))
                .findFirst().orElse(null);
    }

    /**
     * Build the com.alipay.sofa.startup.SofaStartupReporter.SofaStartupCostModel
     * @return the time cost model
     */
    public StartupStaticsModel report() {
        return startupStaticsModel;
    }

    public static class StartupStaticsModel {
        private String          appName;
        private long            applicationBootElapsedTime = 0;
        private long            applicationBootTime;
        private List<StageStat> stageStats                 = new ArrayList<StageStat>();

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

        public List<StageStat> getStageStats() {
            return stageStats;
        }

        public void setStageStats(List<StageStat> stageStats) {
            this.stageStats = stageStats;
        }
    }
}

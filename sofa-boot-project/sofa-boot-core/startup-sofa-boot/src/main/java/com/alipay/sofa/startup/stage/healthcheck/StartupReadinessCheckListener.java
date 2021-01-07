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
package com.alipay.sofa.startup.stage.healthcheck;

import com.alipay.sofa.boot.startup.StageStat;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;
import com.alipay.sofa.startup.StartupReporter;

import static com.alipay.sofa.boot.startup.BootStageConstants.HEALTH_CHECK_STAGE;

/**
 * @author huzijie
 * @version StartupReadinessCheckListener.java, v 0.1 2020年12月31日 4:39 下午 huzijie Exp $
 */
public class StartupReadinessCheckListener extends ReadinessCheckListener {
    private final StartupReporter startupReporter;

    public StartupReadinessCheckListener(StartupReporter startupReporter) {
        this.startupReporter = startupReporter;
    }

    @Override
    public void readinessHealthCheck() {
        StageStat stageStat = new StageStat();
        stageStat.setStageName(HEALTH_CHECK_STAGE);
        stageStat.setStageStartTime(System.currentTimeMillis());
        super.readinessHealthCheck();
        stageStat.setStageEndTime(System.currentTimeMillis());
        startupReporter.addCommonStartupStat(stageStat);
    }
}

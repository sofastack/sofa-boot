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
package com.alipay.sofa.boot.actuator.startup.isle;

import com.alipay.sofa.boot.actuator.startup.StartupReporter;
import com.alipay.sofa.boot.startup.BaseStat;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.stage.ModelCreatingStage;

import static com.alipay.sofa.boot.startup.BootStageConstants.ISLE_MODEL_CREATING_STAGE;

/**
 * Extend for {@link ModelCreatingStage} to compute sofa module create cost time
 *
 * @author huzijie
 * @version StartupModelCreatingStage.java, v 0.1 2020年12月31日 4:34 下午 huzijie Exp $
 */
public class StartupModelCreatingStage extends ModelCreatingStage {

    private StartupReporter startupReporter;

    @Override
    protected void doProcess() throws Exception {
        BaseStat stat = new BaseStat();
        stat.setName(ISLE_MODEL_CREATING_STAGE);
        stat.setStartTime(System.currentTimeMillis());
        try {
            super.doProcess();
        } finally {
            stat.setEndTime(System.currentTimeMillis());
            startupReporter.addCommonStartupStat(stat);
        }
    }

    public StartupReporter getStartupReporter() {
        return startupReporter;
    }

    public void setStartupReporter(StartupReporter startupReporter) {
        this.startupReporter = startupReporter;
    }
}

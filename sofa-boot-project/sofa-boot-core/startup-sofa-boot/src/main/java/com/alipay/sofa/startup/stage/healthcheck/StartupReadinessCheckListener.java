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

import com.alipay.sofa.boot.startup.BaseStat;
import com.alipay.sofa.healthcheck.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.healthcheck.HealthCheckProperties;
import com.alipay.sofa.healthcheck.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.HealthIndicatorProcessor;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;
import com.alipay.sofa.runtime.configure.SofaRuntimeConfigurationProperties;
import com.alipay.sofa.startup.StartupReporter;
import org.springframework.core.env.Environment;

import static com.alipay.sofa.boot.startup.BootStageConstants.HEALTH_CHECK_STAGE;

/**
 * @author huzijie
 * @version StartupReadinessCheckListener.java, v 0.1 2020年12月31日 4:39 下午 huzijie Exp $
 */
public class StartupReadinessCheckListener extends ReadinessCheckListener {
    private final StartupReporter startupReporter;

    public StartupReadinessCheckListener(Environment environment,
                                         HealthCheckerProcessor healthCheckerProcessor,
                                         HealthIndicatorProcessor healthIndicatorProcessor,
                                         AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor,
                                         SofaRuntimeConfigurationProperties sofaRuntimeConfigurationProperties,
                                         HealthCheckProperties healthCheckProperties,
                                         StartupReporter startupReporter) {
        super(environment, healthCheckerProcessor, healthIndicatorProcessor,
            afterReadinessCheckCallbackProcessor, sofaRuntimeConfigurationProperties,
            healthCheckProperties);
        this.startupReporter = startupReporter;
    }

    @Override
    public void readinessHealthCheck() {
        BaseStat stat = new BaseStat();
        stat.setName(HEALTH_CHECK_STAGE);
        stat.setStartTime(System.currentTimeMillis());
        super.readinessHealthCheck();
        stat.setEndTime(System.currentTimeMillis());
        startupReporter.addCommonStartupStat(stat);
    }
}

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
package com.alipay.sofa.healthcheck.readiness;

import com.alipay.sofa.healthcheck.base.BaseHealthCheckTest;
import com.alipay.sofa.healthcheck.configuration.HealthCheckConstants;
import com.alipay.sofa.healthcheck.configuration.SofaBootHealthCheckAutoConfiguration;
import com.alipay.sofa.healthcheck.core.AfterHealthCheckCallbackProcessor;
import com.alipay.sofa.healthcheck.core.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.core.HealthIndicatorProcessor;
import com.alipay.sofa.healthcheck.startup.ReadinessCheckListener;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.HashMap;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class HealthCheckConfigurationTest extends BaseHealthCheckTest {
    @Test
    public void testInject() {
        initApplicationContext(new HashMap<String, Object>(),
            SofaBootHealthCheckAutoConfiguration.class);
        Assert.notNull(applicationContext.getBean(ReadinessCheckListener.class));
        Assert.notNull(applicationContext.getBean(HealthCheckerProcessor.class));
        Assert.notNull(applicationContext.getBean(HealthIndicatorProcessor.class));
        Assert.notNull(applicationContext.getBean(AfterHealthCheckCallbackProcessor.class));

        ReadinessCheckListener readinessCheckListener = applicationContext
            .getBean(ReadinessCheckListener.class);
        Assert.isTrue(!readinessCheckListener.skipIndicator());
        Assert.isTrue(!readinessCheckListener.skipComponent());
        Assert.isTrue(!readinessCheckListener.skipAllCheck());
    }

    @Test
    public void testHealthCheckConfiguration() {
        HashMap<String, Object> healthCheckConfiguration = new HashMap<>();
        healthCheckConfiguration.put(HealthCheckConstants.SOFABOOT_SKIP_ALL_HEALTH_CHECK, true);
        healthCheckConfiguration.put(HealthCheckConstants.SOFABOOT_SKIP_HEALTH_INDICATOR_CHECK,
            true);
        healthCheckConfiguration.put(HealthCheckConstants.SOFABOOT_SKIP_COMPONENT_HEALTH_CHECK,
            true);
        initApplicationContext(healthCheckConfiguration, SofaBootHealthCheckAutoConfiguration.class);

        ReadinessCheckListener readinessCheckListener = applicationContext
            .getBean(ReadinessCheckListener.class);
        Assert.isTrue(readinessCheckListener.skipIndicator());
        Assert.isTrue(readinessCheckListener.skipComponent());
        Assert.isTrue(readinessCheckListener.skipAllCheck());
    }
}
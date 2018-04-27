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
package com.alipay.sofa.healthcheck.core;

import com.alipay.sofa.healthcheck.bean.HealthIndicatorB;
import com.alipay.sofa.healthcheck.util.BaseHealthCheckTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author liangen
 * @version $Id: HealthIndicatorCheckProcessorTest.java, v 0.1 2018年03月11日 下午1:39 liangen Exp $
 */
public class HealthIndicatorCheckProcessorTest extends BaseHealthCheckTest {

    private final HealthIndicatorCheckProcessor healthIndicatorCheckProcessor = new HealthIndicatorCheckProcessor();

    @Configuration
    static class HealthIndicatorConfiguration {

        @Bean
        public HealthIndicatorB healthIndicatorB(@Value("${health-indicator-b.health}") boolean health) {
            return new HealthIndicatorB(health);
        }
    }

    @Test
    public void testCheckIndicatorPassed() {
        initApplicationContext(true);
        boolean result = healthIndicatorCheckProcessor.checkIndicator();
        Assert.assertTrue(result);
    }

    @Test
    public void testCheckIndicatorFailed() {
        initApplicationContext(false);
        boolean result = healthIndicatorCheckProcessor.checkIndicator();
        Assert.assertFalse(result);
    }

    private void initApplicationContext(boolean health) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("health-indicator-b.health", health);
        initApplicationContext(properties, HealthIndicatorConfiguration.class);
    }

}
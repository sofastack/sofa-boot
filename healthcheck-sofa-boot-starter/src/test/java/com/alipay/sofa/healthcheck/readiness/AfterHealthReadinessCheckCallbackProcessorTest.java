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

import com.alipay.sofa.healthcheck.bean.MiddlewareHealthCheckCallback;
import com.alipay.sofa.healthcheck.bean.ApplicationHealthCheckCallback;
import com.alipay.sofa.healthcheck.base.BaseHealthCheckTest;
import com.alipay.sofa.healthcheck.configuration.SofaBootHealthCheckAutoConfiguration;
import com.alipay.sofa.healthcheck.core.AfterHealthCheckCallbackProcessor;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author liangen
 * @author qilong.zql
 * @version 2.3.0
 */
public class AfterHealthReadinessCheckCallbackProcessorTest extends BaseHealthCheckTest {

    @Configuration
    static class AfterReadinessCheckCallbackConfiguration {

        @Bean
        public MiddlewareHealthCheckCallback middlewareHealthCheckCallback(@Value("${after-readiness-check-callback-a.health:false}") boolean health) {
            return new MiddlewareHealthCheckCallback(health);
        }

        @Bean
        public ApplicationHealthCheckCallback applicationHealthCheckCallback(@Value("${after-readiness-check-callback-b.mark:false}") boolean mark) {
            return new ApplicationHealthCheckCallback(mark);
        }
    }

    @Configuration
    static class ReadinessCheckFailedTestConfiguration extends
                                                      AfterReadinessCheckCallbackConfiguration {
        @Bean
        public HealthChecker failedHealthCheck() {
            return new HealthChecker() {
                @Override
                public Health isHealthy() {
                    return Health.down().withDetail("server", "server is bad").build();
                }

                @Override
                public String getComponentName() {
                    return "failedHealthCheck";
                }

                @Override
                public int getRetryCount() {
                    return 0;
                }

                @Override
                public long getRetryTimeInterval() {
                    return 0;
                }

                @Override
                public boolean isStrictCheck() {
                    return true;
                }
            };
        }
    }

    @Configuration
    static class ReadinessCheckSuccessTestConfiguration extends
                                                       AfterReadinessCheckCallbackConfiguration {
        @Bean
        public HealthChecker successHealthCheck() {
            return new HealthChecker() {
                @Override
                public Health isHealthy() {
                    return Health.up().withDetail("server", "server is ok").build();
                }

                @Override
                public String getComponentName() {
                    return "successHealthCheck";
                }

                @Override
                public int getRetryCount() {
                    return 0;
                }

                @Override
                public long getRetryTimeInterval() {
                    return 0;
                }

                @Override
                public boolean isStrictCheck() {
                    return true;
                }
            };
        }
    }

    @Test
    public void testAfterHealthCheckCallbackMarked() {
        initApplicationContext(true, false, AfterReadinessCheckCallbackConfiguration.class);
        AfterHealthCheckCallbackProcessor afterHealthCheckCallbackProcessor = applicationContext
            .getBean(AfterHealthCheckCallbackProcessor.class);
        ApplicationHealthCheckCallback applicationHealthCheckCallback = applicationContext
            .getBean(ApplicationHealthCheckCallback.class);
        HashMap<String, Health> hashMap = new HashMap<>();
        boolean result = afterHealthCheckCallbackProcessor.afterReadinessCheckCallback(hashMap);
        Assert.assertTrue(result);
        Assert.assertTrue(applicationHealthCheckCallback.isMark());
        Assert.assertTrue(hashMap.size() == 2);
        Health middleHealth = hashMap.get("middlewareHealthCheckCallback");
        Health applicationHealth = hashMap.get("applicationHealthCheckCallback");
        Assert.assertNotNull(middleHealth);
        Assert.assertNotNull(applicationHealth);
        Assert.assertTrue(middleHealth.getStatus().equals(Status.UP));
        Assert.assertTrue(applicationHealth.getStatus().equals(Status.UP));
        Assert.assertTrue(middleHealth.getDetails().size() == 1);
        Assert.assertTrue(applicationHealth.getDetails().size() == 1);
        Assert.assertTrue("server is ok".equals(middleHealth.getDetails().get("server")));
        Assert.assertTrue("port is ok".equals(applicationHealth.getDetails().get("port")));
    }

    @Test
    public void testAfterHealthCheckCallbackUnMarked() {
        initApplicationContext(false, false, AfterReadinessCheckCallbackConfiguration.class);
        AfterHealthCheckCallbackProcessor afterHealthCheckCallbackProcessor = applicationContext
            .getBean(AfterHealthCheckCallbackProcessor.class);
        ApplicationHealthCheckCallback applicationHealthCheckCallback = applicationContext
            .getBean(ApplicationHealthCheckCallback.class);
        HashMap<String, Health> hashMap = new HashMap<>();
        boolean result = afterHealthCheckCallbackProcessor.afterReadinessCheckCallback(hashMap);
        Assert.assertFalse(result);
        Assert.assertTrue(applicationHealthCheckCallback.isMark());
        Assert.assertTrue(hashMap.size() == 2);
        Health middleHealth = hashMap.get("middlewareHealthCheckCallback");
        Health applicationHealth = hashMap.get("applicationHealthCheckCallback");
        Assert.assertNotNull(middleHealth);
        Assert.assertNotNull(applicationHealth);
        Assert.assertTrue(middleHealth.getStatus().equals(Status.DOWN));
        Assert.assertTrue(applicationHealth.getStatus().equals(Status.UP));
        Assert.assertTrue(middleHealth.getDetails().size() == 1);
        Assert.assertTrue("server is bad".equals(middleHealth.getDetails().get("server")));
        Assert.assertTrue("port is ok".equals(applicationHealth.getDetails().get("port")));

    }

    @Test
    public void testReadinessCheckFailedAndCallbackNotRun() {
        initApplicationContext(false, false, ReadinessCheckFailedTestConfiguration.class);
        ApplicationHealthCheckCallback applicationHealthCheckCallback = applicationContext
            .getBean(ApplicationHealthCheckCallback.class);
        Assert.assertFalse(applicationHealthCheckCallback.isMark());
    }

    @Test
    public void testReadinessCheckSuccessAndCallbackRun() {
        initApplicationContext(false, false, ReadinessCheckSuccessTestConfiguration.class);
        ApplicationHealthCheckCallback applicationHealthCheckCallback = applicationContext
            .getBean(ApplicationHealthCheckCallback.class);
        Assert.assertTrue(applicationHealthCheckCallback.isMark());
    }

    private void initApplicationContext(boolean health, boolean mark, Class clazz) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("after-readiness-check-callback-a.health", health);
        properties.put("after-readiness-check-callback-b.mark", mark);
        initApplicationContext(properties, clazz, SofaBootHealthCheckAutoConfiguration.class);
    }
}
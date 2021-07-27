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
package com.alipay.sofa.healthcheck.test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alipay.sofa.healthcheck.HealthCheckProperties;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.healthcheck.test.bean.ApplicationHealthCheckCallback;
import com.alipay.sofa.healthcheck.test.bean.FailedHealthCheck;
import com.alipay.sofa.healthcheck.test.bean.HighestOrderReadinessCheckCallback;
import com.alipay.sofa.healthcheck.test.bean.LowestOrderReadinessCheckCallback;
import com.alipay.sofa.healthcheck.test.bean.MiddlewareHealthCheckCallback;
import com.alipay.sofa.healthcheck.test.bean.SuccessHealthCheck;
import com.alipay.sofa.runtime.configure.SofaRuntimeConfigurationProperties;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alipay.sofa.healthcheck.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.healthcheck.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.HealthIndicatorProcessor;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;

/**
 * @author liangen
 * @author qilong.zql
 * @version 2.3.0
 */
public class AfterHealthReadinessCheckCallbackTest {

    private ApplicationContext ctx;

    @Test
    public void testAfterReadinessCheckCallbackMarked() {
        initApplicationContext(true, false,
            AfterHealthReadinessCheckCallbackTestConfiguration.class);
        AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor = ctx
            .getBean(AfterReadinessCheckCallbackProcessor.class);
        ApplicationHealthCheckCallback applicationHealthCheckCallback = ctx
            .getBean(ApplicationHealthCheckCallback.class);
        HashMap<String, Health> hashMap = new HashMap<>();
        boolean result = afterReadinessCheckCallbackProcessor.afterReadinessCheckCallback(hashMap);
        Assert.assertTrue(result);
        Assert.assertTrue(applicationHealthCheckCallback.isMark());
        Assert.assertEquals(2, hashMap.size());
        Health middleHealth = hashMap.get("middlewareHealthCheckCallback");
        Health applicationHealth = hashMap.get("applicationHealthCheckCallback");
        Assert.assertNotNull(middleHealth);
        Assert.assertNotNull(applicationHealth);
        Assert.assertEquals(middleHealth.getStatus(), Status.UP);
        Assert.assertEquals(applicationHealth.getStatus(), Status.UP);
        Assert.assertEquals(1, middleHealth.getDetails().size());
        Assert.assertEquals(1, applicationHealth.getDetails().size());
        Assert.assertEquals("server is ok", middleHealth.getDetails().get("server"));
        Assert.assertEquals("port is ok", applicationHealth.getDetails().get("port"));
    }

    @Test
    public void testAfterReadinessCheckCallbackUnMarked() {
        initApplicationContext(false, false,
            AfterHealthReadinessCheckCallbackTestConfiguration.class);
        AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor = ctx
            .getBean(AfterReadinessCheckCallbackProcessor.class);
        ApplicationHealthCheckCallback applicationHealthCheckCallback = ctx
            .getBean(ApplicationHealthCheckCallback.class);
        HashMap<String, Health> hashMap = new HashMap<>();
        boolean result = afterReadinessCheckCallbackProcessor.afterReadinessCheckCallback(hashMap);
        Assert.assertFalse(result);
        Assert.assertFalse(applicationHealthCheckCallback.isMark());
        Assert.assertEquals(2, hashMap.size());
        Health middleHealth = hashMap.get("middlewareHealthCheckCallback");
        Health applicationHealth = hashMap.get("applicationHealthCheckCallback");
        Assert.assertNotNull(middleHealth);
        Assert.assertNotNull(applicationHealth);
        Assert.assertEquals(middleHealth.getStatus(), Status.DOWN);
        Assert.assertEquals(applicationHealth.getStatus(), Status.DOWN);
        Assert.assertEquals(1, middleHealth.getDetails().size());
        Assert.assertEquals("server is bad", middleHealth.getDetails().get("server"));
        Assert.assertTrue(applicationHealth.getDetails().get("invoking").toString()
            .contains("skipped"));
    }

    @Test
    public void testReadinessCheckFailedAndCallbackNotRun() {
        initApplicationContext(false, false, ReadinessCheckFailedTestConfiguration.class);
        ApplicationHealthCheckCallback applicationHealthCheckCallback = ctx
            .getBean(ApplicationHealthCheckCallback.class);
        Assert.assertFalse(applicationHealthCheckCallback.isMark());
    }

    @Test
    public void testBreakingReadinessCheckCallback() {
        initApplicationContext(false, false, ReadinessCheckCallbackBreakTestConfiguration.class);
        LowestOrderReadinessCheckCallback callback = ctx
            .getBean(LowestOrderReadinessCheckCallback.class);
        Assert.assertFalse(callback.getMark());
    }

    private void initApplicationContext(boolean health, boolean mark, Class clazz) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("after-readiness-check-callback-a.health", health);
        properties.put("after-readiness-check-callback-b.mark", mark);
        properties.put("spring.application.name", "AfterHealthReadinessCheckCallbackTest");
        SpringApplication springApplication = new SpringApplication(clazz);
        springApplication.setDefaultProperties(properties);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        ctx = springApplication.run();
    }

    @Configuration(proxyBeanMethods = false)
    static class ReadinessCheckCallbackBreakTestConfiguration extends
                                                             AfterHealthReadinessCheckCallbackTestConfiguration {
        @Bean
        public HighestOrderReadinessCheckCallback highestOrderReadinessCheckCallback() {
            return new HighestOrderReadinessCheckCallback();
        }

        @Bean
        public LowestOrderReadinessCheckCallback lowestOrderReadinessCheckCallback() {
            return new LowestOrderReadinessCheckCallback();
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties({ HealthCheckProperties.class,
            SofaRuntimeConfigurationProperties.class })
    static class AfterHealthReadinessCheckCallbackTestConfiguration {
        @Bean
        public MiddlewareHealthCheckCallback middlewareHealthCheckCallback(@Value("${after-readiness-check-callback-a.health:false}") boolean health) {
            return new MiddlewareHealthCheckCallback(health);
        }

        @Bean
        public ApplicationHealthCheckCallback applicationHealthCheckCallback(@Value("${after-readiness-check-callback-b.mark:false}") boolean mark) {
            return new ApplicationHealthCheckCallback(mark);
        }

        @Bean
        public AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor() {
            return new AfterReadinessCheckCallbackProcessor();
        }

        @Bean
        public ReadinessCheckListener readinessCheckListener() {
            return new ReadinessCheckListener();
        }

        @Bean
        public HealthCheckerProcessor healthCheckerProcessor() {
            return new HealthCheckerProcessor();
        }

        @Bean
        public HealthIndicatorProcessor healthIndicatorProcessor() {
            return new HealthIndicatorProcessor();
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class ReadinessCheckFailedTestConfiguration extends
                                                      AfterHealthReadinessCheckCallbackTestConfiguration {
        @Bean
        public HealthChecker failedHealthCheck() {
            return new FailedHealthCheck();
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class ReadinessCheckSuccessTestConfiguration extends
                                                       AfterHealthReadinessCheckCallbackTestConfiguration {
        @Bean
        public HealthChecker successHealthCheck() {
            return new SuccessHealthCheck();
        }
    }
}
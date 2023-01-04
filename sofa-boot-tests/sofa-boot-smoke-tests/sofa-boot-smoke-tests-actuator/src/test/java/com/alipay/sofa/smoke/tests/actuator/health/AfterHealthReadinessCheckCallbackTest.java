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
package com.alipay.sofa.smoke.tests.actuator.health;

import com.alipay.sofa.boot.actuator.health.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.smoke.tests.actuator.health.beans.ApplicationHealthCheckCallback;
import com.alipay.sofa.smoke.tests.actuator.health.beans.MiddlewareHealthCheckCallback;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.env.MockEnvironment;

import java.util.HashMap;

/**
 * @author liangen
 * @author qilong.zql
 * @version 2.3.0
 */
//todo assert 换成 org.assertj.core.api.Assertions.assertThat
public class AfterHealthReadinessCheckCallbackTest {

    @Test
    public void testAfterReadinessCheckCallbackMarked() {
        ApplicationContext ctx = initApplicationContext(true, false);
        AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor = ctx
            .getBean(AfterReadinessCheckCallbackProcessor.class);
        afterReadinessCheckCallbackProcessor.init();
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
        ApplicationContext ctx = initApplicationContext(false, false);
        AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor = ctx
            .getBean(AfterReadinessCheckCallbackProcessor.class);
        afterReadinessCheckCallbackProcessor.init();
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

    private ApplicationContext initApplicationContext(boolean health, boolean mark) {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("health", Boolean.toString(health));
        environment.setProperty("mark", Boolean.toString(mark));
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.setEnvironment(environment);
        ctx.register(BaseConfiguration.class);
        ctx.refresh();
        return ctx;
    }

    @Configuration(proxyBeanMethods = false)
    static class BaseConfiguration {

        @Bean
        public AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor() {
            return new AfterReadinessCheckCallbackProcessor();
        }

        @Bean
        public MiddlewareHealthCheckCallback middlewareHealthCheckCallback(@Value("${health:false}") boolean health) {
            return new MiddlewareHealthCheckCallback(health);
        }

        @Bean
        public ApplicationHealthCheckCallback applicationHealthCheckCallback(@Value("${mark:false}") boolean mark) {
            return new ApplicationHealthCheckCallback(mark);
        }
    }
}
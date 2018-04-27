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

import com.alipay.sofa.healthcheck.bean.AfterReadinessCheckCallbackA;
import com.alipay.sofa.healthcheck.bean.AfterReadinessCheckCallbackB;
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
 * @version $Id: AfterHealthCheckCallbackProcessorTest.java, v 0.1 2018年03月11日 下午2:39 liangen Exp $
 */
public class AfterHealthCheckCallbackProcessorTest extends BaseHealthCheckTest {

    private final AfterHealthCheckCallbackProcessor afterHealthCheckCallbackProcessor = new AfterHealthCheckCallbackProcessor();

    @Configuration
    static class AfterReadinessCheckCallbackConfiguration {

        @Bean
        public AfterReadinessCheckCallbackA afterReadinessCheckCallbackA(@Value("${after-readiness-check-callback-a.health:false}") boolean health) {
            return new AfterReadinessCheckCallbackA(health);
        }

        @Bean
        public AfterReadinessCheckCallbackB afterReadinessCheckCallbackB(@Value("${after-readiness-check-callback-b.mark:false}") boolean mark) {
            return new AfterReadinessCheckCallbackB(mark);
        }
    }

    @Test
    public void testAfterHealthCheckCallbackMarked() {
        initApplicationContext(true, false);
        boolean result = afterHealthCheckCallbackProcessor.checkAfterHealthCheckCallback();
        Assert.assertTrue(result);
        Assert.assertTrue(applicationContext.getBean(AfterReadinessCheckCallbackB.class).isMark());
    }

    @Test
    public void testAfterHealthCheckCallbackUnMarked() {
        initApplicationContext(false, false);
        boolean result = afterHealthCheckCallbackProcessor.checkAfterHealthCheckCallback();
        Assert.assertFalse(result);
        Assert.assertFalse(applicationContext.getBean(AfterReadinessCheckCallbackB.class).isMark());
    }

    private void initApplicationContext(boolean health, boolean mark) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("after-readiness-check-callback-a.health", health);
        properties.put("after-readiness-check-callback-b.mark", mark);
        initApplicationContext(properties, AfterReadinessCheckCallbackConfiguration.class);
    }
}
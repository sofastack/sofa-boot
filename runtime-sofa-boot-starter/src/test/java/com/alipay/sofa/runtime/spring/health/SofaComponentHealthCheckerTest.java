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
package com.alipay.sofa.runtime.spring.health;

import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.alipay.sofa.healthcheck.configuration.HealthCheckConstants;
import com.alipay.sofa.runtime.spring.configuration.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.runtime.spring.listener.SofaRuntimeApplicationListener;

/**
 * @author abby.zh
 * @since 2.4.10
 */
public class SofaComponentHealthCheckerTest {

    private final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

    @After
    public void closeContext() {
        this.applicationContext.close();
    }

    @Before
    public void before() {
        ApplicationPreparedEvent applicationPreparedEvent = Mockito
            .mock(ApplicationPreparedEvent.class);
        when(applicationPreparedEvent.getApplicationContext()).thenReturn(applicationContext);
        new SofaRuntimeApplicationListener().onApplicationEvent(applicationPreparedEvent);
    }

    @Test
    public void testDefaultConfig() {
        this.applicationContext.register(SofaRuntimeAutoConfiguration.class);
        this.applicationContext.refresh();
        SofaComponentHealthChecker sofaComponentHealthChecker = applicationContext
            .getBean(SofaComponentHealthChecker.class);
        Assert.assertEquals(HealthCheckConstants.SOFABOOT_COMPONENT_CHECK_RETRY_DEFAULT_COUNT,
            sofaComponentHealthChecker.getRetryCount());
        Assert.assertEquals(HealthCheckConstants.SOFABOOT_COMPONENT_CHECK_RETRY_DEFAULT_INTERVAL,
            sofaComponentHealthChecker.getRetryTimeInterval());
    }

    @Test
    public void testCustomConfig() {
        int customRetryCount = 10;
        int customRetryInterval = 30;
        this.applicationContext.register(SofaRuntimeAutoConfiguration.class);
        TestPropertyValues
            .of(HealthCheckConstants.SOFABOOT_COMPONENT_CHECK_RETRY_COUNT + "=" + customRetryCount)
            .and(
                HealthCheckConstants.SOFABOOT_COMPONENT_CHECK_RETRY_INTERVAL + "="
                        + customRetryInterval).applyTo(applicationContext);
        this.applicationContext.register(SofaRuntimeAutoConfiguration.class);
        this.applicationContext.refresh();
        SofaComponentHealthChecker sofaComponentHealthChecker = applicationContext
            .getBean(SofaComponentHealthChecker.class);
        Assert.assertEquals(customRetryCount, sofaComponentHealthChecker.getRetryCount());
        Assert.assertEquals(customRetryInterval, sofaComponentHealthChecker.getRetryTimeInterval());
    }

}
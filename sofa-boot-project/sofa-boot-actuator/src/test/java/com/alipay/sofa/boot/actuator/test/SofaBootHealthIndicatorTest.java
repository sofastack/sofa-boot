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
package com.alipay.sofa.boot.actuator.test;

import com.alipay.sofa.boot.actuator.health.SofaBootHealthIndicator;
import com.alipay.sofa.healthcheck.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/9/8
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class SofaBootHealthIndicatorTest {

    @Test
    public void testUp() {
        HealthCheckerProcessor healthCheckerProcessor = Mockito.mock(HealthCheckerProcessor.class);
        ReadinessCheckListener readinessCheckListener = Mockito.mock(ReadinessCheckListener.class);
        SofaBootHealthIndicator sofaBootHealthIndicator = new SofaBootHealthIndicator(
            healthCheckerProcessor, readinessCheckListener);
        Mockito.doReturn(true).when(readinessCheckListener).isReadinessCheckFinish();
        Mockito.doReturn(true).when(healthCheckerProcessor).livenessHealthCheck(Mockito.anyMap());
        Health health = sofaBootHealthIndicator.health();
        Assert.assertEquals(Status.UP, health.getStatus());
    }

    @Test
    public void testDown() {
        HealthCheckerProcessor healthCheckerProcessor = Mockito.mock(HealthCheckerProcessor.class);
        ReadinessCheckListener readinessCheckListener = Mockito.mock(ReadinessCheckListener.class);
        SofaBootHealthIndicator sofaBootHealthIndicator = new SofaBootHealthIndicator(
            healthCheckerProcessor, readinessCheckListener);
        Mockito.doReturn(true).when(readinessCheckListener).isReadinessCheckFinish();
        Mockito.doReturn(false).when(healthCheckerProcessor).livenessHealthCheck(Mockito.anyMap());
        Health health = sofaBootHealthIndicator.health();
        Assert.assertEquals(Status.DOWN, health.getStatus());
    }
}

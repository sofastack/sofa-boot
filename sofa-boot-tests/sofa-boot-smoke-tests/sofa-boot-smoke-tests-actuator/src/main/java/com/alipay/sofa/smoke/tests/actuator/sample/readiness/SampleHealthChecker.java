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
package com.alipay.sofa.smoke.tests.actuator.sample.readiness;

import com.alipay.sofa.boot.actuator.health.HealthChecker;
import org.springframework.boot.actuate.health.Health;

/**
 * @author huzijie
 * @version SampleHealthChecker.java, v 0.1 2023年11月24日 4:08 PM huzijie Exp $
 */
public class SampleHealthChecker implements HealthChecker {

    private final long sleep;

    public SampleHealthChecker(long sleep) {
        this.sleep = sleep;
    }

    @Override
    public Health isHealthy() {
        if (sleep > 0) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return Health.up().build();
    }

    @Override
    public String getComponentName() {
        return "sample";
    }
}

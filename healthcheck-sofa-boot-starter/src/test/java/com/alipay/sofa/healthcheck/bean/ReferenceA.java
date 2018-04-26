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
package com.alipay.sofa.healthcheck.bean;

import com.alipay.sofa.healthcheck.core.HealthChecker;
import org.springframework.boot.actuate.health.Health;

/**
 *
 * @author liangen
 * @version $Id: ReferenceABean.java, v 0.1 2018年03月10日 下午10:24 liangen Exp $
 */
public class ReferenceA implements HealthChecker {

    private int     count;

    private boolean isStrict;

    private int     retryCount;

    public ReferenceA(int count, boolean isStrict, int retryCount) {
        this.count = count;
        this.isStrict = isStrict;
        this.retryCount = retryCount;
    }

    @Override
    public Health isHealthy() {
        count++;

        if (count <= 5) {
            return Health.down().withDetail("memory", "memory is deficiency").build();

        } else {
            return Health.up().withDetail("memory", "memory is ok").build();
        }

    }

    @Override
    public String getComponentName() {
        return "AAA";
    }

    @Override
    public int getRetryCount() {
        return retryCount;
    }

    @Override
    public long getRetryTimeInterval() {
        return 500;
    }

    @Override
    public boolean isStrictCheck() {
        return isStrict;
    }

    public int getCount() {
        return count;
    }
}
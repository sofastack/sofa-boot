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
package com.alipay.sofa.healthcheck.service;

import com.alipay.sofa.healthcheck.core.SpringContextCheckProcessor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * The health check HTTP checker for spring context.
 * @author liangen
 * @version $Id: SpringContextHealthCheckInfo.java, v 0.1 2018年02月01日 下午9:38 liangen Exp $
 */
@Component
public class SpringContextHealthCheckInfo implements HealthIndicator {

    private final SpringContextCheckProcessor springContextCheckProcessor = new SpringContextCheckProcessor();

    @Override
    public Health health() {

        boolean checkSuccessful = springContextCheckProcessor.springContextCheck();

        if (checkSuccessful) {
            return Health.up().build();
        } else {
            return Health.down().build();
        }

    }

}
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

import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus;
import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus.HealthIndicatorDetail;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.OrderedHealthAggregator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The health check HTTP checker for start status.
 *
 * @author liangen
 * @version $Id: StartUpHealthCheckStatusCheckInfo.java, v 0.1 2018年02月02日 下午11:34 liangen Exp $
 */
@ConfigurationProperties(prefix = "com.alipay.sofa.healthcheck.readiness")
public class SofaBootReadinessCheckEndpoint extends AbstractEndpoint<Health> {

    private final HealthAggregator healthAggregator = new OrderedHealthAggregator();

    public SofaBootReadinessCheckEndpoint(String id, boolean sensitive) {
        super(id, sensitive);
    }

    @Override
    public Health invoke() {
        //spring
        boolean springContextStatus = StartUpHealthCheckStatus.getSpringContextStatus();

        //component
        boolean componentStatus = StartUpHealthCheckStatus.getComponentStatus();
        Map<String, Health> componentDetail = StartUpHealthCheckStatus.getComponentDetail();

        //HealthIndicator
        boolean healthIndicatorStatus = StartUpHealthCheckStatus.getHealthIndicatorStatus();
        List<HealthIndicatorDetail> healthIndicatorDetails = StartUpHealthCheckStatus
            .getHealthIndicatorDetails();

        //AfterHealthCheckCallback
        boolean afterHealthCheckCallbackStatus = StartUpHealthCheckStatus
            .getAfterHealthCheckCallbackStatus();
        Map<String, Health> afterHealthCheckCallbackDetails = StartUpHealthCheckStatus
            .getAfterHealthCheckCallbackDetails();

        Map<String, Health> healths = new HashMap<>();

        //spring
        if (springContextStatus) {
            healths.put("springContextHealthCheckInfo", Health.up().build());
        } else {
            healths.put("springContextHealthCheckInfo", Health.down().build());

        }

        //component and callback
        Builder builder;
        if (componentStatus && healthIndicatorStatus && afterHealthCheckCallbackStatus) {
            builder = Health.up();
        } else {
            builder = Health.down();
        }

        if (!CollectionUtils.isEmpty(componentDetail)) {
            builder = builder.withDetail("Middleware-start-period", componentDetail);
        }
        if (!CollectionUtils.isEmpty(afterHealthCheckCallbackDetails)) {
            builder = builder.withDetail("Middleware-operation-period",
                afterHealthCheckCallbackDetails);
        }

        healths.put("sofaBootComponentHealthCheckInfo", builder.build());

        //HealthIndicator
        for (HealthIndicatorDetail healthIndicatorDetail : healthIndicatorDetails) {
            String name = healthIndicatorDetail.getName();
            Health health = healthIndicatorDetail.getHealth();

            healths.put(name, health);

        }

        return this.healthAggregator.aggregate(healths);
    }

}
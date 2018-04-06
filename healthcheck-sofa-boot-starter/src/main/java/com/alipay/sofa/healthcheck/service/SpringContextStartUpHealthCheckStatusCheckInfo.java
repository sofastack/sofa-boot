/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
package com.alipay.sofa.healthcheck.service;

import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus;
import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus.HealthIndicatorDetail;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.OrderedHealthAggregator;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The health check HTTP checker for start status.
 * @author liangen
 * @version $Id: StartUpHealthCheckStatusCheckInfo.java, v 0.1 2018年02月02日 下午11:34 liangen Exp $
 */

public class SpringContextStartUpHealthCheckStatusCheckInfo extends AbstractEndpoint<Health> {

    private final HealthAggregator healthAggregator = new OrderedHealthAggregator();

    public SpringContextStartUpHealthCheckStatusCheckInfo(String id, boolean sensitive) {
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
        List<HealthIndicatorDetail> healthIndicatorDetails = StartUpHealthCheckStatus.getHealthIndicatorDetails();

        //AfterHealthCheckCallback
        boolean afterHealthCheckCallbackStatus = StartUpHealthCheckStatus.getAfterHealthCheckCallbackStatus();
        Map<String, Health> afterHealthCheckCallbackDetails = StartUpHealthCheckStatus
            .getAfterHealthCheckCallbackDetails();

        Map<String, Health> healths = new HashMap<String, Health>();

        //spring
        if (springContextStatus) {
            healths.put("springContextHealthCheckInfo", Health.up().build());
        } else {
            healths.put("springContextHealthCheckInfo", Health.down().build());

        }

        //component and callback
        Builder builder = null;
        if (componentStatus && healthIndicatorStatus && afterHealthCheckCallbackStatus) {

            builder = Health.up();
        } else {
            builder = Health.down();
        }

        if (!CollectionUtils.isEmpty(componentDetail)) {
            builder = builder.withDetail("Middleware-start-period", componentDetail);
        }
        if (!CollectionUtils.isEmpty(afterHealthCheckCallbackDetails)) {
            builder = builder.withDetail("Middleware-operation-period", afterHealthCheckCallbackDetails);
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
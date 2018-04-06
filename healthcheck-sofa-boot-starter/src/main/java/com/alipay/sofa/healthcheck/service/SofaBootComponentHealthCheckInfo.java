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
/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.alipay.sofa.healthcheck.service;

import com.alipay.sofa.healthcheck.core.ComponentCheckProcessor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * The health check HTTP checker for component.
 * @author liangen
 * @version $Id: HttpCheckService.java, v 0.1 2017年09月02日 下午1:29 liangen Exp $
 */
@Component
public class SofaBootComponentHealthCheckInfo implements HealthIndicator {

    private static final String           CHECK_RESULT_PREFIX     = "Middleware";

    private final ComponentCheckProcessor componentCheckProcessor = new ComponentCheckProcessor();

    @Override
    public Health health() {

        Map<String, Health> healths = new HashMap<String, Health>();
        boolean checkSuccessful = componentCheckProcessor.httpCheckComponent(healths);

        if (checkSuccessful) {
            return Health.up().withDetail(CHECK_RESULT_PREFIX, healths).build();
        } else {
            return Health.down().withDetail(CHECK_RESULT_PREFIX, healths).build();
        }

    }

}
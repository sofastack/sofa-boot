/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.sofa.healthcheck.test.bean;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * TimeOutHealthIndicator
 *
 * @author xunfang
 * @version TimeOutHealthIndicator.java, v 0.1 2022/12/27
 */
public class TimeoutHealthIndicator implements HealthIndicator {
    private boolean health;

    public TimeoutHealthIndicator(boolean health) {
        this.health = health;
    }

    @Override
    public Health health() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (health) {
            return Health.up().withDetail("timeout", "timeoutHealthIndicator is ok").build();
        } else {
            return Health.down().withDetail("timeout", "timeoutHealthIndicator is bad").build();
        }
    }
}

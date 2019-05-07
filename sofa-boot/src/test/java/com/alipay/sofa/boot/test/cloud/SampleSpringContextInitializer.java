/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.sofa.boot.test.cloud;

import com.alipay.sofa.boot.util.SofaBootEnvUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author qilong.zql
 * @author 2.5.0
 */
public class SampleSpringContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static AtomicLong bootstrapContext    = new AtomicLong(0L);

    public static AtomicLong applicationContext = new AtomicLong(0L);

    public static ConfigurableEnvironment bootstrapEnvironment;

    public static ConfigurableEnvironment applicationEnvironment;

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        if (SofaBootEnvUtils.isSpringCloudBootstrapEnvironment(configurableApplicationContext
                .getEnvironment())) {
            bootstrapEnvironment = configurableApplicationContext.getEnvironment();
            bootstrapContext.incrementAndGet();
            return;
        }
        applicationEnvironment = configurableApplicationContext.getEnvironment();
        applicationContext.incrementAndGet();
    }
}
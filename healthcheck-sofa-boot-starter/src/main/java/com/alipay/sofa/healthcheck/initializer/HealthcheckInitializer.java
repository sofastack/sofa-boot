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
package com.alipay.sofa.healthcheck.initializer;

import com.alipay.sofa.healthcheck.configuration.HealthCheckConfiguration;
import com.alipay.sofa.healthcheck.configuration.HealthCheckConfigurationConstants;
import com.alipay.sofa.healthcheck.service.SofaBootComponentHealthCheckInfo;
import com.alipay.sofa.healthcheck.startup.HealthCheckTrigger;
import com.alipay.sofa.common.log.Constants;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by liangen on 17/8/7.
 */
@Component
@Configuration
@ComponentScan(basePackageClasses = { HealthCheckTrigger.class, SofaBootComponentHealthCheckInfo.class })
public class HealthcheckInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        initializeLog();

        initializeConfiguration(applicationContext);

    }

    private void initializeLog() {
        if (StringUtils.isEmpty(System.getProperty(Constants.LOG_PATH)) &&
            StringUtils.hasText(HealthCheckConfiguration.getProperty(Constants.LOG_PATH))) {
            System.setProperty(Constants.LOG_PATH, HealthCheckConfiguration.getProperty(Constants.LOG_PATH));
        }

        String healthCheckLogLevelKey = Constants.LOG_LEVEL_PREFIX +
            HealthCheckConfigurationConstants.SOFABOOT_HEALTH_LOG_SPACE;
        if (StringUtils.isEmpty(System.getProperty(healthCheckLogLevelKey)) &&
            StringUtils.hasText(HealthCheckConfiguration.getProperty(healthCheckLogLevelKey))) {
            System.setProperty(healthCheckLogLevelKey, HealthCheckConfiguration.getProperty(healthCheckLogLevelKey));
        }

        if (StringUtils.isEmpty(System.getProperty(Constants.LOG_ENCODING_PROP_KEY)) &&
            StringUtils.hasText(HealthCheckConfiguration.getProperty(Constants.LOG_ENCODING_PROP_KEY))) {
            System.setProperty(Constants.LOG_ENCODING_PROP_KEY,
                HealthCheckConfiguration.getProperty(Constants.LOG_ENCODING_PROP_KEY));
        }

    }

    private void initializeConfiguration(ConfigurableApplicationContext applicationContext) {
        if (HealthCheckConfiguration.getEnvironment() == null) {
            HealthCheckConfiguration.setEnvironment(applicationContext.getEnvironment());
        }
    }
}

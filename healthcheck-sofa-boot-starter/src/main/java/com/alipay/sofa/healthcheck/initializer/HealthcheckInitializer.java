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

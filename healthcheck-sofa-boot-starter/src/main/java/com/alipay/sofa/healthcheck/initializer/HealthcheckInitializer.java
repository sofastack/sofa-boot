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

import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.healthcheck.configuration.HealthCheckConfiguration;
import com.alipay.sofa.healthcheck.configuration.HealthCheckConfigurationConstants;
import com.alipay.sofa.healthcheck.log.SofaBootHealthCheckLoggerFactory;
import com.alipay.sofa.healthcheck.service.SofaBootComponentHealthCheckInfo;
import com.alipay.sofa.healthcheck.startup.HealthCheckTrigger;
import com.alipay.sofa.infra.log.space.SofaBootLogSpaceIsolationInit;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by liangen on 17/8/7.
 */
@Component
@Configuration
@ComponentScan(basePackageClasses = { HealthCheckTrigger.class,
                                     SofaBootComponentHealthCheckInfo.class })
public class HealthcheckInitializer implements
                                   ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // init logging.level.com.alipay.sofa.runtime argument
        Environment environment = applicationContext.getEnvironment();
        String healthCheckLogLevelKey = Constants.LOG_LEVEL_PREFIX
                                        + HealthCheckConfigurationConstants.SOFABOOT_HEALTH_LOG_SPACE;
        SofaBootLogSpaceIsolationInit.initSofaBootLogger(environment, healthCheckLogLevelKey);

        // init HealthCheckConfiguration
        if (HealthCheckConfiguration.getEnvironment() == null) {
            HealthCheckConfiguration.setEnvironment(applicationContext.getEnvironment());
        }

        SofaBootHealthCheckLoggerFactory.getLogger(HealthcheckInitializer.class).info(
            "SOFABoot HealthCheck Starting!");
    }

}

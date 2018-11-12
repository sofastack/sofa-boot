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
import com.alipay.sofa.healthcheck.configuration.HealthCheckConstants;
import com.alipay.sofa.healthcheck.log.SofaBootHealthCheckLoggerFactory;
import com.alipay.sofa.infra.log.space.SofaBootLogSpaceIsolationInit;
import com.alipay.sofa.infra.utils.SOFABootEnvUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @author liangen
 * @author qilong.zql
 * @since 2.3.0
 */
public class SofaBootHealthCheckInitializer
                                           implements
                                           ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        Environment environment = applicationContext.getEnvironment();
        if (SOFABootEnvUtils.isSpringCloudBootstrapEnvironment(environment)) {
            return;
        }
        // init logging.level.com.alipay.sofa.runtime argument
        String healthCheckLogLevelKey = Constants.LOG_LEVEL_PREFIX
                                        + HealthCheckConstants.SOFABOOT_HEALTH_LOG_SPACE;
        SofaBootLogSpaceIsolationInit.initSofaBootLogger(environment, healthCheckLogLevelKey);

        SofaBootHealthCheckLoggerFactory.getLogger(SofaBootHealthCheckInitializer.class).info(
            "SOFABoot HealthCheck Starting!");
    }

}

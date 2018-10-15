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
package com.alipay.sofa.infra.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * SOFABootEnvUtils
 *
 * @author yangguanchao
 * @since 2.5.0
 */
public class SOFABootEnvUtils {

    /**
     * org.springframework.cloud.bootstrap.BootstrapApplicationListener#BOOTSTRAP_PROPERTY_SOURCE_NAME
     */
    private static final String BOOTSTRAP_PROPERTY_SOURCE_NAME = "bootstrap";

    private static final Logger LOGGER                         = LoggerFactory
                                                                   .getLogger(SOFABootEnvUtils.class);

    /**
     * Determine whether the {@link org.springframework.core.env.Environment} is Spring Cloud bootstrap environment.
     *
     * Reference doc is https://cloud.spring.io/spring-cloud-static/spring-cloud.html#_application_context_hierarchies and
     * issue https://github.com/spring-cloud/spring-cloud-config/issues/1151.
     *
     * Pay attention only can be used in one implementation which implements {@link ApplicationContextInitializer} because the bootstrap
     * properties will be removed after initialized.
     *
     * @param environment the environment get from spring context
     * @return true indicates Spring Cloud environment
     */
    public static boolean isSpringCloudBootstrapEnvironment(Environment environment) {
        if (environment instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment configurableEnvironment = (ConfigurableEnvironment) environment;
            if (configurableEnvironment.getPropertySources().contains(
                BOOTSTRAP_PROPERTY_SOURCE_NAME)) {
                //use app logger
                LOGGER.debug("Current application context environment is bootstrap");
                return true;
            }
        }
        return false;
    }
}

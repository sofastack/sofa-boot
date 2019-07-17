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
package com.alipay.sofa.boot.util;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

import com.alipay.sofa.boot.constant.SofaBootConstants;

/**
 * SofaBootEnvUtils
 *
 * @author yangguanchao
 * @since 2.5.0
 */
public class SofaBootEnvUtils {

    private final static String SPRING_CLOUD_MARK_NAME = "org.springframework.cloud.bootstrap.BootstrapConfiguration";

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
            return !((ConfigurableEnvironment) environment).getPropertySources().contains(
                SofaBootConstants.SOFA_BOOTSTRAP)
                   && isSpringCloud();
        }
        return false;
    }

    /**
     * Check whether import spring cloud BootstrapConfiguration
     * @return
     */
    public static boolean isSpringCloud() {
        return ClassUtils.isPresent(SPRING_CLOUD_MARK_NAME, null);
    }
}

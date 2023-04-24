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

import org.springframework.cloud.util.PropertyUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

/**
 * Utility methods that are useful to obtain SOFABoot framework running environment.
 *
 * @author yangguanchao
 * @author huzijie
 * @since 2.5.0
 */
public class SofaBootEnvUtils {

    /**
     * Property name for bootstrap configuration class name.
     */
    public static final String  CLOUD_BOOTSTRAP_CONFIGURATION_CLASS        = "org.springframework.cloud.bootstrap.BootstrapConfiguration";

    /**
     * Boolean if bootstrap configuration class exists.
     */
    public static final boolean CLOUD_BOOTSTRAP_CONFIGURATION_CLASS_EXISTS = ClassUtils
                                                                               .isPresent(
                                                                                   CLOUD_BOOTSTRAP_CONFIGURATION_CLASS,
                                                                                   null);

    /**
     * Property source name for bootstrap.
     */
    public static final String  BOOTSTRAP_PROPERTY_SOURCE_NAME             = "bootstrap";

    public static final String  LOCAL_ENV_KEY                              = "sofa.boot.useLocalEnv";

    public static final String  INTELLIJ_IDE_MAIN_CLASS                    = "com.intellij.rt.execution.application.AppMainV2";

    private static final String ARK_BIZ_CLASSLOADER_NAME                   = "com.alipay.sofa.ark.container.service.classloader.BizClassLoader";

    private static boolean      LOCAL_ENV                                  = false;

    private static boolean      TEST_ENV                                   = false;

    static {
        initLocalEnv();
        initSpringTestEnv();
    }

    private static void initSpringTestEnv() {
        // Detection of test environment
        StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            if ("loadContext".equals(stackTraceElement.getMethodName())
                && "org.springframework.boot.test.context.SpringBootContextLoader"
                    .equals(stackTraceElement.getClassName())) {
                TEST_ENV = true;
                break;
            }
        }
    }

    private static void initLocalEnv() {
        if (Boolean.getBoolean(LOCAL_ENV_KEY)) {
            LOCAL_ENV = true;
            return;
        }
        try {
            Class.forName(INTELLIJ_IDE_MAIN_CLASS);
            LOCAL_ENV = true;
        } catch (ClassNotFoundException e) {
            LOCAL_ENV = false;
        }
    }

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
        if (environment != null && isSpringCloudEnvironmentEnabled(environment)) {
            return ((ConfigurableEnvironment) environment).getPropertySources().contains(
                BOOTSTRAP_PROPERTY_SOURCE_NAME);
        } else {
            return false;
        }
    }

    /**
     * Check whether spring cloud Bootstrap environment enabled
     * @return true indicates spring cloud Bootstrap environment enabled
     */
    public static boolean isSpringCloudEnvironmentEnabled(Environment environment) {
        return CLOUD_BOOTSTRAP_CONFIGURATION_CLASS_EXISTS
               && PropertyUtils.bootstrapEnabled(environment);
    }

    /**
     * Check whether import spring cloud BootstrapConfiguration
     * @return true indicates spring cloud BootstrapConfiguration is imported
     */
    public static boolean isSpringCloud() {
        return CLOUD_BOOTSTRAP_CONFIGURATION_CLASS_EXISTS;
    }

    /**
     * Check whether running in spring test environment
     * @return true indicates in spring test environment
     */
    public static boolean isSpringTestEnv() {
        return TEST_ENV;
    }

    /**
     * Check whether running in local development environment
     * @return true indicates in local development environment
     */

    public static boolean isLocalEnv() {
        return LOCAL_ENV;
    }

    /**
     * Check whether running in sofa ark environment
     * @return true indicates in sofa ark environment
     */
    public static boolean isArkEnv() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return contextClassLoader != null
               && ARK_BIZ_CLASSLOADER_NAME.equals(contextClassLoader.getClass().getName());
    }

}

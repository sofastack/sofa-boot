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
package com.alipay.sofa.boot.constant;

/**
 * SofaBootConstants
 *
 * @author yangguanchao
 * @since 2017/09/07
 */
public class SofaBootConstants {

    public static final String APP_NAME_KEY                                     = "spring.application.name";
    public static final String SOFA_DEFAULT_PROPERTY_SOURCE                     = "sofaConfigurationProperties";
    public static final String SOFA_BOOTSTRAP                                   = "sofaBootstrap";
    public static final String SPRING_CLOUD_BOOTSTRAP                           = "bootstrap";
    public static final String SOFA_HIGH_PRIORITY_CONFIG                        = "sofaHighPriorityConfig";

    /** sofa boot space name **/
    public static String       SOFABOOT_SPACE_NAME                              = "sofa-boot";

    /**
     * {@link org.springframework.boot.ResourceBanner#getVersionsMap}
     */
    public static final String SOFA_BOOT_VERSION                                = "sofa-boot.version";
    public static final String SOFA_BOOT_FORMATTED_VERSION                      = "sofa-boot.formatted-version";

    /**
     * Default {@literal management.endpoints.web.exposure.include} value
     */
    public static final String ENDPOINTS_WEB_EXPOSURE_INCLUDE_CONFIG            = "management.endpoints.web.exposure.include";
    public static final String SOFA_DEFAULT_ENDPOINTS_WEB_EXPOSURE_VALUE        = "info, health, versions, components, beans, readiness, startup, triggerReadinessCallback";

    public static final String ENDPOINT_AVAILABILITY_GROUP_CONFIG_KEY           = "management.endpoint.health.group.liveness.include";
    public static final String DEFAULT_ENDPOINT_AVAILABILITY_GROUP_CONFIG_VALUE = "livenessState,sofaBoot";

    /**
     * root application context name
     */
    public static final String ROOT_APPLICATION_CONTEXT                         = "RootApplicationContext";

    /**
     * sofa configuration prefix
     */
    public static final String PREFIX                                           = "sofa.boot";

    /**
     * Thread Pool Core Size to execute async bean initialization
     */
    public static final String ASYNC_INIT_BEAN_CORE_SIZE                        = PREFIX
                                                                                  + ".asyncInitBeanCoreSize";

    /**
     * Thread Pool Max Size to execute async bean initialization
     */
    public static final String ASYNC_INIT_BEAN_MAX_SIZE                         = PREFIX
                                                                                  + ".asyncInitBeanMaxSize";

    /** framework constants **/
    public final static String APPLICATION                                      = "SOFABOOT-APPLICATION";
    public static String       SOFA_MODULE_FILE                                 = "sofa-module.properties";
    public static String       SPRING_CONTEXT_PATH                              = "META-INF/spring";
    public static String       DEFAULT_PROFILE_VALUE                            = "default";

    /** sofa-module.properties keywords **/
    public static String       SPRING_PARENT                                    = "Spring-Parent";
    public static String       MODULE_NAME                                      = "Module-Name";
    public static String       REQUIRE_MODULE                                   = "Require-Module";
    public static String       MODULE_PROFILE                                   = "Module-Profile";
}

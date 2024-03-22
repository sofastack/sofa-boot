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
 * Core constants for SOFABoot framework.
 *
 * @author yangguanchao
 * @author huzijie
 * @since 2017/09/07
 */
public class SofaBootConstants {

    /**
     * app name key
     */
    public static final String APP_NAME_KEY                                    = "spring.application.name";

    /**
     * SOFABoot startup log extra info
     */
    public static final String STARTUP_LOG_EXTRA_INFO                          = "startupLogExtraInfo";

    /**
     * SOFABoot config property source key
     */
    public static final String SOFA_DEFAULT_PROPERTY_SOURCE                    = "sofaConfigurationProperties";

    /**
     * SOFABoot exclude autoconfiguration property source key
     */
    public static final String SOFA_EXCLUDE_AUTO_CONFIGURATION_PROPERTY_SOURCE = "sofaExcludeAutoConfigurationProperties";

    /**
     * SOFABoot bootstrap property source key
     */
    public static final String SOFA_BOOTSTRAP                                  = "sofaBootstrap";

    /**
     * SpringCloud property source key
     */
    public static final String SPRING_CLOUD_BOOTSTRAP                          = "bootstrap";

    /**
     * SOFA High priority config key
     */
    public static final String SOFA_HIGH_PRIORITY_CONFIG                       = "sofaHighPriorityConfig";

    /**
     * Space name for SOFABoot framework used in sofa-common-tools
     */
    public static final String SOFA_BOOT_SPACE_NAME                            = "sofa-boot";

    /**
     * SOFABoot version property key
     */
    public static final String SOFA_BOOT_VERSION                               = "sofa-boot.version";

    /**
     * SOFABoot formatted version property key
     */
    public static final String SOFA_BOOT_FORMATTED_VERSION                     = "sofa-boot.formatted-version";

    /**
     * SOFABoot scenes file dir
     */
    public static final String SOFA_BOOT_SCENES_FILE_DIR                       = "sofa-boot/scenes";

    /**
     * Spring Boot web endpoint exposure property key
     */
    public static final String ENDPOINTS_WEB_EXPOSURE_INCLUDE_CONFIG           = "management.endpoints.web.exposure.include";

    /**
     * Default exposure web endpoint list
     */
    public static final String SOFA_DEFAULT_ENDPOINTS_WEB_EXPOSURE_VALUE       = "info,health,readiness,startup,beans,components,rpc,isle,threadpool";

    /**
     * CPU core
     */
    public static final int    CPU_CORE                                        = Runtime
                                                                                   .getRuntime()
                                                                                   .availableProcessors();
}

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
package com.alipay.sofa.infra.constants;

/**
 * SofaBootInfraConstants
 *
 * @author yangguanchao
 * @since 2017/09/07
 */
public class SofaBootInfraConstants {

    /***
     * 获取应用名: 备注 @Value("${spring.application.name:@null}")
     */
    public static final String APP_NAME_KEY                              = "spring.application.name";

    public static final String SOFA_DEFAULT_PROPERTY_SOURCE              = "sofaConfigurationProperties";

    /**
     * {@link org.springframework.boot.ResourceBanner#getVersionsMap}
     */
    public static final String SOFA_BOOT_VERSION                         = "sofa-boot.version";
    public static final String SOFA_BOOT_FORMATTED_VERSION               = "sofa-boot.formatted-version";

    /**
     * resource pattern of properties file which is used to save some information of starters.
     */
    public static final String SOFA_BOOT_VERSION_PROPERTIES              = "classpath*:META-INF/sofa.versions.properties";

    /**
     * Default {@literal management.endpoints.web.exposure.include} value
     */
    public static final String ENDPOINTS_WEB_EXPOSURE_INCLUDE_CONFIG     = "management.endpoints.web.exposure.include";
    public static final String SOFA_DEFAULT_ENDPOINTS_WEB_EXPOSURE_VALUE = "info, health, versions, readiness";
}

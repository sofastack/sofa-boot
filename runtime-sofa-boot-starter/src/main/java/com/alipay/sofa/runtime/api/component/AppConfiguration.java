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
package com.alipay.sofa.runtime.api.component;

import java.util.Map;

/**
 * The SOFA configurations of an application.
 *
 * @author xuanbei 18/2/28
 */
public interface AppConfiguration {

    /** The host name of the system. */
    String SYS_HOST_NAME = "sys_host_name";
    /** The id address of the system. */
    String SYS_IP        = "sys_ip";
    /** The application name. */
    String SYS_APP_NAME  = "app_name";
    /** Run mode of the application. */
    String SYS_RUN_MODE  = "run_mode";

    /**
     * Get the SOFA configuration as a safe map.
     *
     * @return The SOFA configuration as a safe map.
     */
    Map<String, String> getConfig();

    /**
     * Get a specific configuration value from SOFA configuration.
     *
     * @param key The key of the configuration.
     * @return The value of the configuration.
     */
    String getPropertyValue(String key);

    /**
     * Get a specific configuration value from SOFA configuration. If the value is null, return the specified default
     * value.
     *
     * @param key The key of the configuration.
     * @param defaultValue The default value to return when the value from SOFA configuration is null.
     * @return The value of the configuration if it is not null. If it is null, return the specified default value.
     */
    String getPropertyValue(String key, String defaultValue);

    /**
     * Get the application name from SOFA configuration.
     *
     * @return The application name.
     */
    String getSysAppName();

    /**
     * Get the system IP from SOFA configuration.
     *
     * @return The system IP.
     */
    String getSysIp();

    /**
     * Get the system run mode from SOFA configuration.
     *
     * @return The system run mode.
     */
    String getSysRunMode();

    /**
     * Get the host name of SOFA configuration.
     *
     * @return The host name.
     */
    String getSysHostName();
}

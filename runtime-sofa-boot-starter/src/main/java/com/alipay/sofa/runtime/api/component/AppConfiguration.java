/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
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

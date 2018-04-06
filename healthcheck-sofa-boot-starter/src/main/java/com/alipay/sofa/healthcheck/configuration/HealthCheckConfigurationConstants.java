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
package com.alipay.sofa.healthcheck.configuration;

/**
 * Created by liangen on 17/8/6.
 */
public class HealthCheckConfigurationConstants {

    public static final String SOFABOOT_SKIP_ALL_HEALTH_CHECK                   = "com.alipay.sofa.healthcheck.skip.all";
    public static final String SOFABOOT_SKIP_ALL_HEALTH_CHECK_UNDERLINE         = "com_alipay_sofa_healthcheck_skip_all";

    public static final String SOFABOOT_SKIP_COMPONENT_HEALTH_CHECK             = "com.alipay.sofa.healthcheck.skip.component";
    public static final String SOFABOOT_SKIP_COMPONENT_HEALTH_CHECK_UNDERLINE   = "com_alipay_sofa_healthcheck_skip_component";

    public static final String SOFABOOT_SKIP_HEALTH_INDICATOR_CHECK             = "com.alipay.sofa.healthcheck.skip.indicator";
    public static final String SOFABOOT_SKIP_HEALTH_INDICATOR_CHECK_UNDERLINE   = "com_alipay_sofa_healthcheck_skip_indicator";

    public static final String SOFABOOT_COMPONENT_HEALTH_CHECK_ROUND            = "com.alipay.sofa.healthcheck.component.check.round";
    public static final String SOFABOOT_COMPONENT_HEALTH_CHECK_ROUND_UNDERLINE  = "com_alipay_sofa_healthcheck_component_check_round";

    public static final String SOFABOOT_STRICT_COMPONENT_HEALTH_CHECK           = "com.alipay.sofa.healthcheck.strict.component.check";
    public static final String SOFABOOT_STRICT_COMPONENT_HEALTH_CHECK_UNDERLINE = "com_alipay_sofa_healthcheck_strict_component_check";

    //日志
    public static final String SOFABOOT_HEALTH_LOG_SPACE                        = "com.alipay.sofa.healthcheck";

    public static final String SPRING_APPLICATION_NAME                          = "spring.application.name";

    public static final int    DEFAULT_COMPONENT_HEALTH_CHECK_ROUND             = 20;
}

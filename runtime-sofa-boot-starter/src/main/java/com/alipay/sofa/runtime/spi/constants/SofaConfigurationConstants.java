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
package com.alipay.sofa.runtime.spi.constants;

/**
 * TODO need change key？
 *
 * @author xuanbei 18/3/1
 */
public interface SofaConfigurationConstants {
    /**
     * skip jvm reference check or not
     */
    String SOFA_RUNTIME_SKIP_JVM_REFERENCE_HEALTH_CHECK = "sofa_runtime_skip_jvm_reference_health_check";

    /**
     * disable local first or not
     **/
    String SOFA_RUNTIME_DISABLE_LOCAL_FIRST             = "sofa_runtime_disable_local_first";

    /**
     * profile active key
     */
    String SOFA_ACTIVE_PROFILES_PROPERTY_NAME           = "sofa_runtime_profiles_active";

    /**
     * start sofa module parallel or not
     */
    String SOFA_MODULE_START_UP_PARALLEL                = "sofa_module_start_up_parallel";

    /**
     * allow bean definition overriding
     */
    String ALLOW_BEAN_DEFINITION_OVERRIDING             = "allow_bean_definition_overriding";
}

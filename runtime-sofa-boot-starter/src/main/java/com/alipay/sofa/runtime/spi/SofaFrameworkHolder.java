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
package com.alipay.sofa.runtime.spi;

import java.util.concurrent.atomic.AtomicReference;

/**
 * SOFA Framework
 *
 * @author xuanbei 18/3/1
 */
public class SofaFrameworkHolder {
    /**
     * SOFA Framework
     */
    private static AtomicReference<SofaFramework> sofaFrameworkReference = new AtomicReference<>();

    /**
     * Get SOFA Framework
     *
     * @return SOFA Framework
     */
    public static SofaFramework getSofaFramework() {
        return sofaFrameworkReference.get();
    }

    /**
     * Set SOFA Framework
     *
     * @param sofaFramework SOFA Framework
     */
    public static void setSofaFramework(SofaFramework sofaFramework) {
        sofaFrameworkReference.compareAndSet(null, sofaFramework);
    }

    /**
     * contains SOFA Framework or not
     *
     * @return true or false
     */
    public static boolean containsSofaFramework() {
        return getSofaFramework() != null;
    }
}

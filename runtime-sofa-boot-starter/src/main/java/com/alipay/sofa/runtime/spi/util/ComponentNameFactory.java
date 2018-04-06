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
package com.alipay.sofa.runtime.spi.util;

import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.model.ComponentType;

/**
 * ComponentName Factory
 *
 * @author xuanbei 18/2/28
 */
public class ComponentNameFactory {
    /**
     * create ComponentName by component type and class type
     *
     * @param type
     * @return
     */
    public static ComponentName createComponentName(ComponentType type, Class<?> clazz) {
        return new ComponentName(type, mergeComponentName(clazz, null));
    }

    /**
     * create ComponentName by component type and component name
     *
     * @param type
     * @param name
     * @return
     */
    public static ComponentName createComponentName(ComponentType type, String name) {
        return new ComponentName(type, name);
    }

    /**
     * create ComponentName by component type,class type and unique id
     *
     * @param type
     * @return
     */
    public static ComponentName createComponentName(ComponentType type, Class<?> clazz,
                                                    String uniqueId) {
        return new ComponentName(type, mergeComponentName(clazz, uniqueId));
    }

    /**
     * create ComponentName by class type and unique id
     *
     * @param clazz
     * @param uniqueId
     * @return
     */
    private static String mergeComponentName(Class<?> clazz, String uniqueId) {
        String ret = clazz.getName();
        if (uniqueId != null && uniqueId.length() > 0) {
            ret += ":" + uniqueId;
        }
        return ret;
    }
}

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
package com.alipay.sofa.infra.constants;

import java.util.HashSet;
import java.util.Set;

/**
 * 改类中的静态变量的值为 SOFA Boot 初始化时默认会塞入系统属性中的 key 值,
 * SOFABoot 在初始化时，如发现配置源(SOFABoot 为 Environment, Embedded 为指定文件的 Properties) 中包含该类中的静态变量，
 * 将会自动将该属性 塞入到系统属性中
 * 后续如有其他功能需要扩展需要向系统属性中写入的 key-value ,直接在该类中添加即可。
 * @author luoguimu123
 * @version $Id: PropertyConstants.java, v 0.1 2017年12月12日 下午2:54 luoguimu123 Exp $
 */
public class SystemPropertyConstants {

    private static final String MIDDLEWARE_ACCESS_KEY = "com.antcloud.mw.access";

    private static final String MIDDLEWARE_SECRET_KEY = "com.antcloud.mw.secret";

    private static final String ANTCLOUD_ENDPOINT_KEY = "com.antcloud.antvip.endpoint";

    private static final String ANTCLOUD_INSTANCEID_KEY = "com.alipay.instanceid";

    private static final String ANTCLOUD_ENV_KEY = "com.alipay.env";

    public static final Set<String> KEYS = new HashSet<String>();

    static {
        KEYS.add(MIDDLEWARE_ACCESS_KEY);
        KEYS.add(MIDDLEWARE_SECRET_KEY);
        KEYS.add(ANTCLOUD_ENDPOINT_KEY);
        KEYS.add(ANTCLOUD_INSTANCEID_KEY);
        KEYS.add(ANTCLOUD_ENV_KEY);
    }


}
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
package com.alipay.sofa.infra.log;

import com.alipay.sofa.common.log.LoggerSpaceManager;

/**
 * Created by yangguanchao on 18/01/04.
 */
public class InfraHealthCheckLoggerFactory {

    public static final String INFRASTRUCTURE_LOG_SPACE = "com.alipay.sofa.infra";

    /***
     * 获取日志对象
     *
     * @param clazz 日志的名字
     * @return 日志实现
     */
    public static org.slf4j.Logger getLogger(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return getLogger(clazz.getCanonicalName());
    }

    /**
     * 获取日志对象
     *
     * @param name 日志的名字
     * @return 日志实现
     */
    public static org.slf4j.Logger getLogger(String name) {
        //从"com/alipay/boot/healthcheck/log"中获取 health check 的日志配置并寻找对应logger对象,log 为默认添加的后缀
        if (name == null || name.isEmpty()) {
            return null;
        }
        return LoggerSpaceManager.getLoggerBySpace(name, INFRASTRUCTURE_LOG_SPACE);
    }
}

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
package com.alipay.sofa.infra.log.base;

import com.alipay.sofa.infra.log.InfraHealthCheckLoggerFactory;
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.log.LoggerSpaceManager;

/**
 * AbstraceTestBase
 * <p/>
 * Created by yangguanchao on 18/01/04.
 */
public abstract class AbstraceTestBase {

    public static final String restLogLevel = Constants.LOG_LEVEL_PREFIX +
                                                InfraHealthCheckLoggerFactory.INFRASTRUCTURE_LOG_SPACE;

    public void before() throws Exception {
        System.getProperties().put("logging.path", "./logs");
    }

    public void after() throws Exception {

        System.err.println("\n " + Constants.LOG_ENCODING_PROP_KEY + " : " + System.getProperty("file.encoding"));
        System.err.println("\n " + Constants.LOG_PATH + " : " + System.getProperty("logging.path"));
        String restLogLevel = Constants.LOG_LEVEL_PREFIX + InfraHealthCheckLoggerFactory.INFRASTRUCTURE_LOG_SPACE;
        System.err.println("\n " + restLogLevel + " : " +
            System.getProperty(restLogLevel));

        System.clearProperty(Constants.LOG_PATH);
        System.clearProperty(restLogLevel);
        //关闭禁用开关
        System.clearProperty(Constants.LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
        System.clearProperty(Constants.LOG4J2_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
        System.clearProperty(Constants.LOG4J_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
        LoggerSpaceManager.removeILoggerFactoryBySpaceName(InfraHealthCheckLoggerFactory.INFRASTRUCTURE_LOG_SPACE);
    }
}

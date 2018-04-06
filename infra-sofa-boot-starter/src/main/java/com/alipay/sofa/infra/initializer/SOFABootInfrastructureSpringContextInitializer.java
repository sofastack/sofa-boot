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
package com.alipay.sofa.infra.initializer;

import com.alipay.sofa.infra.log.InfraHealthCheckLoggerFactory;
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.log.ReportUtil;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * SOFA Boot Infrastructure 启动初始化器
 * <p/>
 * 参考:org.springframework.core.io.support.SpringFactoriesLoader
 * <p/>
 * Created by yangguanchao on 18/01/04.
 */
public class SOFABootInfrastructureSpringContextInitializer
                                                           implements
                                                           ApplicationContextInitializer<ConfigurableApplicationContext>,
                                                           PriorityOrdered {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        //init log
        this.logInit(applicationContext.getEnvironment());

        InfraHealthCheckLoggerFactory.getLogger(SOFABootInfrastructureSpringContextInitializer.class)
            .info("SOFA Boot Infrastructure Starting!");

    }

    private void logInit(Environment environment) {
        String loggingPath = environment.getProperty(Constants.LOG_PATH);
        if (!StringUtils.isEmpty(loggingPath)) {
            initLoggingPath(loggingPath);
        }
        String infraLogLevelKey = Constants.LOG_LEVEL_PREFIX + InfraHealthCheckLoggerFactory.INFRASTRUCTURE_LOG_SPACE;
        String infraLogLevelValue = environment.getProperty(infraLogLevelKey);
        if (!StringUtils.isEmpty(infraLogLevelValue)) {
            System.setProperty(infraLogLevelKey, infraLogLevelValue);
        }
    }

    @Override
    public int getOrder() {
        //设置为最高优先级
        return HIGHEST_PRECEDENCE;
    }

    public static void initLoggingPath(String middlewareLoggingPath) {
        if (StringUtils.isEmpty((String) System.getProperty(Constants.LOG_PATH)) &&
            !StringUtils.isEmpty(middlewareLoggingPath)) {
            System.setProperty(Constants.LOG_PATH, middlewareLoggingPath);
            ReportUtil.report("Actual " + Constants.LOG_PATH + " is [ " + middlewareLoggingPath + " ]");
        }
    }
}

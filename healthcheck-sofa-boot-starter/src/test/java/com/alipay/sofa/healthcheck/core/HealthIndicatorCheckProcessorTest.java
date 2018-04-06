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
package com.alipay.sofa.healthcheck.core;

import com.alipay.sofa.healthcheck.bean.HealthIndicatorB;
import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author liangen
 * @version $Id: HealthIndicatorCheckProcessorTest.java, v 0.1 2018年03月11日 下午1:39 liangen Exp $
 */
public class HealthIndicatorCheckProcessorTest {

    private static ClassPathXmlApplicationContext applicationContext;
    private final HealthIndicatorCheckProcessor   healthIndicatorCheckProcessor = new HealthIndicatorCheckProcessor();

    @BeforeClass
    public static void init() {
        applicationContext = new ClassPathXmlApplicationContext(
            "com/alipay/sofa/healthcheck/application_healthcheck_test.xml");
        HealthCheckManager.init(applicationContext);
    }

    @Test
    public void testCheckIndicator() {
        HealthIndicatorB.setHealth(true);
        boolean result_1 = healthIndicatorCheckProcessor.checkIndicator();
        Assert.assertEquals(true, result_1);

        HealthIndicatorB.setHealth(false);
        boolean result_2 = healthIndicatorCheckProcessor.checkIndicator();
        Assert.assertEquals(false, result_2);

    }

    @AfterClass
    public static void clean() {
        applicationContext.close();
        HealthCheckManager.init(null);
        StartUpHealthCheckStatus.clean();
    }

}
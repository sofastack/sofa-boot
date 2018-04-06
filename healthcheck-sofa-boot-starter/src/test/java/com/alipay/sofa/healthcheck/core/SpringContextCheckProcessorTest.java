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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author liangen
 * @version $Id: SpringContextCheckProcessorTest.java, v 0.1 2018年03月12日 下午2:38 liangen Exp $
 */
public class SpringContextCheckProcessorTest {

    private static ClassPathXmlApplicationContext applicationContext;
    private final SpringContextCheckProcessor     springContextCheckProcessor = new SpringContextCheckProcessor();

    @BeforeClass
    public static void init() {
        try {
            applicationContext = new ClassPathXmlApplicationContext(
                "com/alipay/sofa/healthcheck/application_healthcheck_test_error.xml");

        } catch (Exception e) {
            System.out.println(e);
        }
        HealthCheckManager.init(applicationContext);
    }

    @Test
    public void testSpringContextCheck() {
        boolean result = springContextCheckProcessor.springContextCheck();
        Assert.assertEquals(false, result);
    }

    @AfterClass
    public static void clean() {
        if (applicationContext != null) {
            applicationContext.close();
            HealthCheckManager.init(null);
        }
    }
}
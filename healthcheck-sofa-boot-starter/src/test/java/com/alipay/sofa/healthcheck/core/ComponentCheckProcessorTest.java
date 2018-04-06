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

import com.alipay.sofa.healthcheck.bean.ReferenceA;
import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author liangen
 * @version $Id: ComponentCheckProcessor.java, v 0.1 2018年03月10日 下午10:15 liangen Exp $
 */
public class ComponentCheckProcessorTest {

    private static ClassPathXmlApplicationContext applicationContext;
    private final ComponentCheckProcessor         componentCheckProcessor = new ComponentCheckProcessor();

    @BeforeClass
    public static void init() {
        applicationContext = new ClassPathXmlApplicationContext(
            "com/alipay/sofa/healthcheck/application_healthcheck_test.xml");
        HealthCheckManager.init(applicationContext);
    }

    @Test
    public void testSartupCheckComponentForRetry() {
        ReferenceA.setCount(0);
        ReferenceA.setRetryCount(20);
        ReferenceA.setStrict(true);

        boolean result = componentCheckProcessor.startupCheckComponent();

        Assert.assertEquals(true, result);
        Assert.assertEquals(6, ReferenceA.getCount());

    }

    @Test
    public void testSartupCheckComponentForNotStrict() {
        ReferenceA.setCount(0);
        ReferenceA.setStrict(true);
        ReferenceA.setRetryCount(4);

        boolean result_1 = componentCheckProcessor.startupCheckComponent();
        Assert.assertEquals(false, result_1);
        Assert.assertEquals(5, ReferenceA.getCount());

        ReferenceA.setCount(0);
        ReferenceA.setStrict(false);
        ReferenceA.setRetryCount(4);

        boolean result_2 = componentCheckProcessor.startupCheckComponent();
        Assert.assertEquals(true, result_2);
        Assert.assertEquals(5, ReferenceA.getCount());

    }

    @Test
    public void testHttpCheckComponent() {
        ReferenceA.setCount(4);
        ReferenceA.setStrict(true);
        ReferenceA.setRetryCount(5);

        Map<String, Health> details_1 = new HashMap<String, Health>();
        boolean result_1 = componentCheckProcessor.httpCheckComponent(details_1);
        Assert.assertEquals(result_1, false);

        Assert.assertEquals(5, ReferenceA.getCount());
        Assert.assertEquals(2, details_1.size());
        Assert.assertEquals(Status.DOWN, details_1.get("AAA").getStatus());
        Assert.assertEquals("memory is deficiency", details_1.get("AAA").getDetails().get("memory"));
        Assert.assertEquals(Status.UP, details_1.get("BBB").getStatus());
        Assert.assertEquals("network is ok", details_1.get("BBB").getDetails().get("network"));

    }

    @AfterClass
    public static void clean() {
        applicationContext.close();
        HealthCheckManager.init(null);
        StartUpHealthCheckStatus.clean();
    }
}
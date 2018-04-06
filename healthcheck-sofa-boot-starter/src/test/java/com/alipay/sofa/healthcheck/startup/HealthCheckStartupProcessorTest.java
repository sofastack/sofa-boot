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
package com.alipay.sofa.healthcheck.startup;

import com.alipay.sofa.healthcheck.bean.AfterHealthCheckCallbackA;
import com.alipay.sofa.healthcheck.bean.HealthIndicatorB;
import com.alipay.sofa.healthcheck.bean.ReferenceA;
import com.alipay.sofa.healthcheck.configuration.HealthCheckConfigurationConstants;
import com.alipay.sofa.healthcheck.core.HealthCheckManager;
import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus.HealthIndicatorDetail;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Map;

/**
 *
 * @author liangen
 * @version $Id: HealthCheckStartupProcessorTest.java, v 0.1 2018年03月12日 下午4:11 liangen Exp $
 */
public class HealthCheckStartupProcessorTest {

    private static ClassPathXmlApplicationContext applicationContext;
    private final HealthCheckStartupProcessor     healthCheckStartupProcessor = new HealthCheckStartupProcessor();

    @BeforeClass
    public static void init() {
        applicationContext = new ClassPathXmlApplicationContext(
            "com/alipay/sofa/healthcheck/application_healthcheck_test_1.xml");

        HealthCheckManager.init(applicationContext);
    }

    @Test
    public void testCheckHealthSuccess() {

        ReferenceA.setCount(5);
        HealthIndicatorB.setHealth(true);
        AfterHealthCheckCallbackA.setHealth(true);
        healthCheckStartupProcessor.checkHealth();

        boolean springStatus = StartUpHealthCheckStatus.getSpringContextStatus();
        boolean componentStatus = StartUpHealthCheckStatus.getComponentStatus();
        boolean healthIndicatorStatus = StartUpHealthCheckStatus.getHealthIndicatorStatus();
        boolean afterStatus = StartUpHealthCheckStatus.getAfterHealthCheckCallbackStatus();
        Map<String, Health> componentDetail = StartUpHealthCheckStatus.getComponentDetail();
        List<HealthIndicatorDetail> healthIndicatorDetail = StartUpHealthCheckStatus.getHealthIndicatorDetails();
        Map<String, Health> afterDetail = StartUpHealthCheckStatus.getAfterHealthCheckCallbackDetails();

        Assert.assertEquals(springStatus, true);
        Assert.assertEquals(componentStatus, true);
        Assert.assertEquals(healthIndicatorStatus, true);
        Assert.assertEquals(afterStatus, true);

        Assert.assertEquals(1, componentDetail.size());
        Assert.assertEquals(1, healthIndicatorDetail.size());
        Assert.assertEquals(1, afterDetail.size());

        Assert.assertEquals("memory is ok", componentDetail.get("AAA").getDetails().get("memory"));
        Assert.assertEquals("HealthIndicatorB", healthIndicatorDetail.get(0).getName());
        Assert.assertEquals("hard disk is ok", healthIndicatorDetail.get(0).getHealth().getDetails().get("hard disk"));
        Assert.assertEquals("server is ok", afterDetail.get("AfterHealthCheckCallbackA").getDetails().get("server"));

        StartUpHealthCheckStatus.clean();
    }

    @Test
    public void testCheckHealthFail() {
        ReferenceA.setCount(0);
        ReferenceA.setRetryCount(4);
        ReferenceA.setStrict(true);
        HealthIndicatorB.setHealth(false);
        AfterHealthCheckCallbackA.setHealth(true);
        healthCheckStartupProcessor.checkHealth();

        boolean springStatus = StartUpHealthCheckStatus.getSpringContextStatus();
        boolean componentStatus = StartUpHealthCheckStatus.getComponentStatus();
        boolean healthIndicatorStatus = StartUpHealthCheckStatus.getHealthIndicatorStatus();
        boolean afterStatus = StartUpHealthCheckStatus.getAfterHealthCheckCallbackStatus();
        Map<String, Health> componentDetail = StartUpHealthCheckStatus.getComponentDetail();
        List<HealthIndicatorDetail> healthIndicatorDetail = StartUpHealthCheckStatus.getHealthIndicatorDetails();
        Map<String, Health> afterDetail = StartUpHealthCheckStatus.getAfterHealthCheckCallbackDetails();

        Assert.assertEquals(springStatus, true);
        Assert.assertEquals(componentStatus, false);
        Assert.assertEquals(healthIndicatorStatus, false);
        Assert.assertEquals(afterStatus, false);

        Assert.assertEquals(1, componentDetail.size());
        Assert.assertEquals(1, healthIndicatorDetail.size());
        Assert.assertEquals(0, afterDetail.size());

        Assert.assertEquals("memory is deficiency", componentDetail.get("AAA").getDetails().get("memory"));
        Assert.assertEquals("HealthIndicatorB", healthIndicatorDetail.get(0).getName());
        Assert.assertEquals("hard disk is bad", healthIndicatorDetail.get(0).getHealth().getDetails().get("hard disk"));

        StartUpHealthCheckStatus.clean();

    }

    @AfterClass
    public static void clean() {
        if (applicationContext != null) {
            applicationContext.close();
            HealthCheckManager.init(null);
        }
    }

}
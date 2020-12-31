package com.alipay.sofa.startup.test;

import com.alipay.sofa.boot.startup.*;
import com.alipay.sofa.startup.StartupReporter;
import com.alipay.sofa.startup.stage.StartupContextRefreshedListener;
import com.alipay.sofa.startup.test.beans.InitCostBean;
import com.alipay.sofa.startup.test.configuration.SofaStartupAutoConfiguration;
import com.alipay.sofa.startup.test.configuration.SofaStartupHealthCheckAutoConfiguration;
import com.alipay.sofa.startup.test.spring.StartupApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


/**
 * @author huzijie
 * @version StartupReporterTest.java, v 0.1 2021年01月04日 8:31 下午 huzijie Exp $
 */
@SpringBootTest(classes = StartupApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
@Import(value = {SofaStartupAutoConfiguration.class, SofaStartupHealthCheckAutoConfiguration.class})
public class HealthCheckStartupReporterTest {
    @Autowired
    private StartupReporter startupReporter;

    @Test
    public void testStartupReporter() {
        Assert.assertNotNull(startupReporter);
        StartupReporter.StartupStaticsModel startupStaticsModel = startupReporter.report();
        Assert.assertNotNull(startupStaticsModel);
        Assert.assertEquals(6, startupStaticsModel.getStageStats().size());

        StageStat healthCheckStage = startupReporter.getStageNyName(BootStageConstants.HEALTH_CHECK_STAGE);
        Assert.assertNotNull(healthCheckStage);
        Assert.assertTrue(healthCheckStage.getElapsedTime() > 0);

    }
}

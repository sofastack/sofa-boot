package com.alipay.sofa.healthcheck.test;

import com.alipay.sofa.healthcheck.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.healthcheck.HealthCheckProperties;
import com.alipay.sofa.healthcheck.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.HealthIndicatorProcessor;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;
import com.alipay.sofa.healthcheck.test.bean.DiskHealthIndicator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2021/4/26
 */
public class HealthIndicatorExclusionTest {
    private ApplicationContext applicationContext;

    @Configuration
    @EnableConfigurationProperties(HealthCheckProperties.class)
    static class HealthIndicatorConfiguration {
        @Bean
        public DiskHealthIndicator diskHealthIndicator() {
            return new DiskHealthIndicator(false);
        }

        @Bean
        public AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor() {
            return new AfterReadinessCheckCallbackProcessor();
        }

        @Bean
        public ReadinessCheckListener readinessCheckListener() {
            return new ReadinessCheckListener();
        }

        @Bean
        public HealthCheckerProcessor healthCheckerProcessor() {
            return new HealthCheckerProcessor();
        }

        @Bean
        public HealthIndicatorProcessor healthIndicatorProcessor() {
            return new HealthIndicatorProcessor();
        }
    }

    @Test
    public void testCheckIndicatorFailed() {
        initApplicationContext(false);
        HealthIndicatorProcessor healthIndicatorProcessor = applicationContext
                .getBean(HealthIndicatorProcessor.class);
        HashMap<String, Health> hashMap = new HashMap<>();
        boolean result = healthIndicatorProcessor.readinessHealthCheck(hashMap);
        Health diskHealth = hashMap.get("disk");
        Assert.assertFalse(result);
        Assert.assertEquals(1, hashMap.size());
        Assert.assertNotNull(diskHealth);
        Assert.assertEquals(diskHealth.getStatus(), Status.DOWN);
        Assert.assertEquals("hard disk is bad", diskHealth.getDetails().get("disk"));
    }

    @Test
    public void testCheckIndicatorPassed() {
        initApplicationContext(true);
        HealthIndicatorProcessor healthIndicatorProcessor = applicationContext
                .getBean(HealthIndicatorProcessor.class);
        HashMap<String, Health> hashMap = new HashMap<>();
        boolean result = healthIndicatorProcessor.readinessHealthCheck(hashMap);
        Health diskHealth = hashMap.get("disk");
        Assert.assertTrue(result);
        Assert.assertEquals(0, hashMap.size());
        Assert.assertNull(diskHealth);
    }

    private void initApplicationContext(boolean exclude) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("spring.application.name", "HealthIndicatorCheckProcessorTest");
        if (exclude) {
            properties.put("com.alipay.sofa.boot.excludedIndicators", "com.alipay.sofa.healthcheck.test.bean.DiskHealthIndicator");
        }
        SpringApplication springApplication = new SpringApplication(
                HealthIndicatorExclusionTest.HealthIndicatorConfiguration.class);
        springApplication.setDefaultProperties(properties);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        applicationContext = springApplication.run();
    }
}

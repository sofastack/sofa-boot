package com.alipay.sofa.healthcheck.test;

import com.alipay.sofa.healthcheck.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.healthcheck.HealthCheckProperties;
import com.alipay.sofa.healthcheck.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.HealthIndicatorProcessor;
import com.alipay.sofa.healthcheck.ReadinessCheckListener;
import com.alipay.sofa.healthcheck.core.HealthChecker;
import com.alipay.sofa.healthcheck.test.bean.DiskHealthIndicator;
import com.alipay.sofa.runtime.configure.SofaRuntimeConfigurationProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/11/17
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = {"spring.application.name=ManualReadinessCheckListenerTest", "com.alipay.sofa.boot.manualReadinessCallback=true"})
public class ManualReadinessCheckListenerTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Configuration
    @EnableConfigurationProperties({HealthCheckProperties.class, SofaRuntimeConfigurationProperties.class})
    static class HealthCheckConfiguration {
        @Bean
        public HealthChecker myHealthChecker() {
            return new HealthChecker() {
                @Override
                public Health isHealthy() {
                    return Health.down().withDetail("health", "failed").build();
                }

                @Override
                public String getComponentName() {
                    return "myHealthChecker";
                }
            };
        }

        @Bean
        public DiskHealthIndicator diskHealthIndicator(@Value("${disk-health-indicator.health:true}") boolean health) {
            return new DiskHealthIndicator(health);
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
    public void testReadinessCheck() throws BeansException {
        ReadinessCheckListener readinessCheckListener = applicationContext
                .getBean(ReadinessCheckListener.class);
        Assert.assertFalse(readinessCheckListener.getReadinessCallbackTriggered().get());
        Assert.assertFalse(readinessCheckListener.getHealthCheckerStatus());
        Assert.assertTrue(readinessCheckListener.getHealthIndicatorStatus());

        ReadinessCheckListener.ManualReadinessCallbackResult result = readinessCheckListener.triggerReadinessCallback();
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.getDetails().contains("checker or indicator failed"));
        Assert.assertFalse(readinessCheckListener.getReadinessCallbackTriggered().get());
    }
}

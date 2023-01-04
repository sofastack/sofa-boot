package com.alipay.sofa.boot.actuator.autoconfigure.startup;

import com.alipay.sofa.boot.actuator.autoconfigure.health.HealthProperties;
import com.alipay.sofa.boot.actuator.autoconfigure.health.ReadinessAutoConfiguration;
import com.alipay.sofa.boot.actuator.health.AfterReadinessCheckCallbackProcessor;
import com.alipay.sofa.boot.actuator.health.HealthCheckerProcessor;
import com.alipay.sofa.boot.actuator.health.HealthIndicatorProcessor;
import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import com.alipay.sofa.boot.actuator.health.ReadinessEndpoint;
import com.alipay.sofa.boot.actuator.startup.StartupReporter;
import com.alipay.sofa.boot.actuator.startup.health.StartupReadinessCheckListener;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for startup health components.
 *
 * @author huzijie
 * @version StartupHealthAutoConfiguration.java, v 0.1 2023年01月04日 2:40 PM huzijie Exp $
 */
@AutoConfiguration(before = ReadinessAutoConfiguration.class)
@ConditionalOnAvailableEndpoint(endpoint = ReadinessEndpoint.class)
public class StartupHealthAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = ReadinessCheckListener.class, search = SearchStrategy.CURRENT)
    public StartupReadinessCheckListener startupReadinessCheckListener(Environment environment,
                                                                       HealthCheckerProcessor healthCheckerProcessor,
                                                                       HealthIndicatorProcessor healthIndicatorProcessor,
                                                                       AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor,
                                                                       HealthProperties healthCheckProperties,
                                                                       StartupReporter startupReporter) {
        StartupReadinessCheckListener readinessCheckListener = new StartupReadinessCheckListener(
                environment, healthCheckerProcessor, healthIndicatorProcessor,
                afterReadinessCheckCallbackProcessor, startupReporter);
        readinessCheckListener.setManualReadinessCallback(healthCheckProperties
                .isManualReadinessCallback());
        readinessCheckListener.setThrowExceptionWhenHealthCheckFailed(healthCheckProperties
                .isInsulator());
        return readinessCheckListener;
    }
}

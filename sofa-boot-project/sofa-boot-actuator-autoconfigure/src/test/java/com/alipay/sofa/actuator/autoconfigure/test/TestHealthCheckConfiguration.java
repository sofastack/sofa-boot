package com.alipay.sofa.actuator.autoconfigure.test;

import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorProperties;
import org.springframework.boot.actuate.health.HealthStatusHttpMapper;
import org.springframework.boot.actuate.health.HealthWebEndpointResponseMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author huzijie
 * @version TestHealthCheckConfiguration.java, v 0.1 2021年01月05日 12:02 下午 huzijie Exp $
 */
@Configuration
@EnableConfigurationProperties(value = {HealthIndicatorProperties.class, HealthEndpointProperties.class})
public class TestHealthCheckConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HealthStatusHttpMapper createHealthStatusHttpMapper(HealthIndicatorProperties healthIndicatorProperties) {
        HealthStatusHttpMapper statusHttpMapper = new HealthStatusHttpMapper();
        if (healthIndicatorProperties.getHttpMapping() != null) {
            statusHttpMapper.addStatusMapping(healthIndicatorProperties.getHttpMapping());
        }
        return statusHttpMapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public HealthWebEndpointResponseMapper healthWebEndpointResponseMapper(HealthStatusHttpMapper statusHttpMapper,
                                                                           HealthEndpointProperties properties) {
        return new HealthWebEndpointResponseMapper(statusHttpMapper, properties.getShowDetails(),
                properties.getRoles());
    }
}
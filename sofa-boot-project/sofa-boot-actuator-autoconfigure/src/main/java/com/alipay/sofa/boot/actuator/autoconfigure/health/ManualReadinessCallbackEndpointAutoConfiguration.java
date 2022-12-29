package com.alipay.sofa.boot.actuator.autoconfigure.health;

import com.alipay.sofa.boot.actuator.health.ManualReadinessCallbackEndPoint;
import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author huzijie
 * @version ManualReadinessCallbackEndpointAutoConfiguration.java, v 0.1 2022年12月29日 4:56 PM huzijie Exp $
 */
@AutoConfiguration
@ConditionalOnAvailableEndpoint(endpoint = ManualReadinessCallbackEndPoint.class)
public class ManualReadinessCallbackEndpointAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ReadinessCheckListener.class)
    public ManualReadinessCallbackEndPoint manualReadinessCallbackEndPoint(ReadinessCheckListener readinessCheckListener) {
        return new ManualReadinessCallbackEndPoint(readinessCheckListener);
    }
}
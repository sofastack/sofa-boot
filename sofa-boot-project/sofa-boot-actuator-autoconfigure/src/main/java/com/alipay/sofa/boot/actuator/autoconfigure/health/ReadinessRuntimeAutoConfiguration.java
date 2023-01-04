package com.alipay.sofa.boot.actuator.autoconfigure.health;

import com.alipay.sofa.boot.actuator.health.ComponentHealthChecker;
import com.alipay.sofa.boot.actuator.health.ReadinessEndpoint;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for readiness runtime components.
 *
 * @author huzijie
 * @version ReadinessRuntimeAutoConfiguration.java, v 0.1 2023年01月04日 2:43 PM huzijie Exp $
 */
@AutoConfiguration(after = SofaRuntimeAutoConfiguration.class)
@ConditionalOnClass(SofaRuntimeContext.class)
@ConditionalOnAvailableEndpoint(endpoint = ReadinessEndpoint.class)
public class ReadinessRuntimeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SofaRuntimeContext.class)
    public ComponentHealthChecker sofaComponentHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
        return new ComponentHealthChecker(sofaRuntimeContext);
    }
}

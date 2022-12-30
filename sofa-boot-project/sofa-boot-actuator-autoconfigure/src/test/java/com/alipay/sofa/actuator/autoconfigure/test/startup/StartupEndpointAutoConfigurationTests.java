package com.alipay.sofa.actuator.autoconfigure.test.startup;

import com.alipay.sofa.boot.actuator.autoconfigure.startup.StartupAutoConfiguration;
import com.alipay.sofa.boot.actuator.autoconfigure.startup.StartupEndPointAutoConfiguration;
import com.alipay.sofa.boot.actuator.startup.StartupEndPoint;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StartupEndPointAutoConfiguration}.
 *
 * @author huzijie
 * @version StartupEndpointAutoConfigurationTests.java, v 0.1 2022年12月29日 5:58 PM huzijie Exp $
 */
public class StartupEndpointAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(StartupEndPointAutoConfiguration.class,
                    StartupAutoConfiguration.class));

    @Test
    void runShouldHaveEndpointBean() {
        this.contextRunner.withPropertyValues("management.endpoints.web.exposure.include=startup")
                .run((context) -> assertThat(context).hasSingleBean(StartupEndPoint.class));
    }

    @Test
    void runWhenNotExposedShouldNotHaveEndpointBean() {
        this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(StartupEndPoint.class));
    }

    @Test
    void runWhenEnabledPropertyIsFalseShouldNotHaveEndpointBean() {
        this.contextRunner.withPropertyValues("management.endpoint.startup.enabled:false")
                .withPropertyValues("management.endpoints.web.exposure.include=*")
                .run((context) -> assertThat(context).doesNotHaveBean(StartupEndPoint.class));
    }
}
package com.alipay.sofa.actuator.autoconfigure.test.components;

import com.alipay.sofa.boot.actuator.autoconfigure.beans.IsleBeansEndpointAutoConfiguration;
import com.alipay.sofa.boot.actuator.autoconfigure.components.ComponentsEndpointAutoConfiguration;
import com.alipay.sofa.boot.actuator.components.ComponentsEndPoint;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link IsleBeansEndpointAutoConfiguration}.
 *
 * @author huzijie
 * @version ComponentsEndpointAutoConfigurationTests.java, v 0.1 2022年12月29日 5:56 PM huzijie Exp $
 */
public class ComponentsEndpointAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ComponentsEndpointAutoConfiguration.class,
                    SofaRuntimeAutoConfiguration.class));

    @Test
    void runShouldHaveEndpointBean() {
        this.contextRunner.withPropertyValues("management.endpoints.web.exposure.include=components")
                .run((context) -> assertThat(context).hasSingleBean(ComponentsEndPoint.class));
    }


    @Test
    void runWhenNotExposedShouldNotHaveEndpointBean() {
        this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(ComponentsEndPoint.class));
    }

    @Test
    void runWhenNotExposedShouldNotHaveIsleClass() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(SofaRuntimeContext.class))
                .run((context) -> assertThat(context).doesNotHaveBean(ComponentsEndPoint.class));
    }

    @Test
    void runWhenEnabledPropertyIsFalseShouldNotHaveEndpointBean() {
        this.contextRunner.withPropertyValues("management.endpoint.components.enabled:false")
                .withPropertyValues("management.endpoints.web.exposure.include=*")
                .run((context) -> assertThat(context).doesNotHaveBean(ComponentsEndPoint.class));
    }

}

package com.alipay.sofa.boot.actuator.autoconfigure.rpc;

import com.alipay.sofa.boot.actuator.rpc.RpcAfterHealthCheckCallback;
import com.alipay.sofa.rpc.boot.context.RpcStartApplicationListener;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RpcActuatorAutoConfiguration}.
 *
 * @author huzijie
 * @version RpcActuatorAutoConfigurationTests.java, v 0.1 2023年02月22日 10:34 AM huzijie Exp $
 */
public class RpcActuatorAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(
                    AutoConfigurations
                            .of(RpcActuatorAutoConfiguration.class))
            .withPropertyValues(
                    "management.endpoints.web.exposure.include=readiness");

    @Test
    void runShouldHaveRpcActuatorBeans() {
        this.contextRunner
                .withBean(RpcStartApplicationListener.class)
                .run((context) -> assertThat(context)
                        .hasSingleBean(RpcAfterHealthCheckCallback.class));
    }

    @Test
    void runWhenNotExposedShouldNotHaveReadinessBeans() {
        this.contextRunner
                .withBean(RpcStartApplicationListener.class)
                .withPropertyValues("management.endpoints.web.exposure.include=info")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(RpcAfterHealthCheckCallback.class));
    }

    @Test
    void runWhenRpcClassNotExist() {
        this.contextRunner
                .withBean(RpcStartApplicationListener.class)
                .withClassLoader(new FilteredClassLoader(RpcStartApplicationListener.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(RpcAfterHealthCheckCallback.class));
    }

    @Test
    void runWhenRpcBeanNotExist() {
        this.contextRunner
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(RpcAfterHealthCheckCallback.class));
    }
}

package com.alipay.sofa.actuator.autoconfigure.test.startup;

import com.alipay.sofa.boot.actuator.autoconfigure.startup.StartupAutoConfiguration;
import com.alipay.sofa.boot.actuator.startup.StartupReporter;
import com.alipay.sofa.boot.actuator.startup.stage.BeanCostBeanPostProcessor;
import com.alipay.sofa.boot.actuator.startup.stage.StartupContextRefreshedListener;
import com.alipay.sofa.boot.actuator.startup.stage.health.StartupReadinessCheckListener;
import com.alipay.sofa.boot.actuator.startup.stage.isle.StartupModelCreatingStage;
import com.alipay.sofa.boot.actuator.startup.stage.isle.StartupSpringContextInstallStage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.availability.LivenessStateHealthIndicator;
import org.springframework.boot.actuate.availability.ReadinessStateHealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StartupAutoConfiguration}.
 *
 * @author huzijie
 * @version StartupAutoConfigurationTests.java, v 0.1 2022年12月29日 6:00 PM huzijie Exp $
 */
public class StartupAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(StartupAutoConfiguration.class))
            .withPropertyValues("management.endpoints.web.exposure.include=startup");

    @Test
    void runShouldHaveStartupBeans() {
        this.contextRunner.run((context) -> assertThat(context)
                .hasSingleBean(StartupReporter.class)
                .hasSingleBean(BeanCostBeanPostProcessor.class)
                .hasSingleBean(StartupContextRefreshedListener.class));
    }

    @Test
    void runWhenHaveReadinessEndpoints() {
        this.contextRunner
                .withPropertyValues("management.endpoints.web.exposure.include=readiness")
                .run((context) -> assertThat(context)
                        .hasSingleBean(StartupReadinessCheckListener.class));
    }

    @Test
    void runWhenHaveIsleClasses() {
        this.contextRunner
                .run((context) -> assertThat(context).hasSingleBean(ApplicationAvailability.class)
                        .hasSingleBean(StartupSpringContextInstallStage.class)
                        .hasSingleBean(StartupModelCreatingStage.class));
    }
}
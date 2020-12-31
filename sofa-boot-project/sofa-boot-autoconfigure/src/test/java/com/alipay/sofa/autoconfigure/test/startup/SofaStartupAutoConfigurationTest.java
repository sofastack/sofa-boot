package com.alipay.sofa.autoconfigure.test.startup;

import com.alipay.sofa.boot.autoconfigure.startup.SofaStartupAutoConfiguration;
import com.alipay.sofa.startup.StartupProperties;
import com.alipay.sofa.startup.StartupReporter;
import com.alipay.sofa.startup.stage.BeanCostBeanPostProcessor;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author huzijie
 * @version SofaStartupAutoConfigurationTest.java, v 0.1 2021年01月05日 11:39 上午 huzijie Exp $
 */
public class SofaStartupAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SofaStartupAutoConfiguration.class));

    @Test
    public void startupReporterExist() {
        contextRunner.run((context -> {
            assertThat(context).hasSingleBean(StartupReporter.class);
            assertThat(context).hasSingleBean(BeanCostBeanPostProcessor.class);
            assertThat(context).hasSingleBean(StartupProperties.class);
        }));
    }

    @Test
    public void startupReporterNotExist() {
        contextRunner.withClassLoader(new FilteredClassLoader(StartupReporter.class))
                    .run((context -> {
            assertThat(context).doesNotHaveBean(StartupReporter.class);
            assertThat(context).doesNotHaveBean(BeanCostBeanPostProcessor.class);
            assertThat(context).doesNotHaveBean(StartupReporter.class);
        }));
    }
}

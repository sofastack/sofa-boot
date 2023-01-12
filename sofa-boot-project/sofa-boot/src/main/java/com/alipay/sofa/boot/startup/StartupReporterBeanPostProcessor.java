package com.alipay.sofa.boot.startup;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;

/**
 * @author huzijie
 * @version StartupReporterBeanPostProcessor.java, v 0.1 2023年01月12日 6:13 PM huzijie Exp $
 */
public class StartupReporterBeanPostProcessor implements BeanPostProcessor {

    private final StartupReporter startupReporter;

    public StartupReporterBeanPostProcessor(StartupReporter startupReporter) {
        this.startupReporter = startupReporter;
    }

    @Override
    @Nullable
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof StartupReporterAware) {
            ((StartupReporterAware) bean).setStartupReporter(startupReporter);
        }
        return bean;
    }
}

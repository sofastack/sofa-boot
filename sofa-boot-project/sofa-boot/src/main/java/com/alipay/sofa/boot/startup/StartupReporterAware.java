package com.alipay.sofa.boot.startup;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;

/**
 * @author huzijie
 * @version StartupReporterAware.java, v 0.1 2023年01月12日 6:12 PM huzijie Exp $
 */
public interface StartupReporterAware extends Aware {

    void setStartupReporter(StartupReporter startupReporter) throws BeansException;
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.boot.isle.stage;

import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.startup.BaseStat;
import com.alipay.sofa.boot.startup.StartupReporter;
import com.alipay.sofa.boot.startup.StartupReporterAware;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

/**
 * {@link AbstractPipelineStage} is a common base class for {@link PipelineStage} implementations.
 *
 * @author xuanbei 18/3/1
 * @author huzijie
 */
public abstract class AbstractPipelineStage implements PipelineStage, ApplicationContextAware,
                                           BeanFactoryAware, StartupReporterAware, InitializingBean {

    protected final ClassLoader               appClassLoader = Thread.currentThread()
                                                                 .getContextClassLoader();

    protected ApplicationRuntimeModel         application;

    protected ConfigurableApplicationContext  applicationContext;

    protected StartupReporter                 startupReporter;

    protected ConfigurableListableBeanFactory beanFactory;

    protected BaseStat                        baseStat;

    @Override
    public void process() throws Exception {
        BaseStat stat = createBaseStat();
        stat.setName(getName());
        stat.setStartTime(System.currentTimeMillis());
        this.baseStat = stat;
        try {
            doProcess();
        } finally {
            stat.setEndTime(System.currentTimeMillis());
            if (startupReporter != null) {
                startupReporter.addCommonStartupStat(stat);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(application, "applicationRuntimeModel must not be null");
    }

    @Override
    public void setStartupReporter(StartupReporter startupReporter) throws BeansException {
        this.startupReporter = startupReporter;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Assert.isTrue(applicationContext instanceof ConfigurableApplicationContext,
            "applicationContext must implement ConfigurableApplicationContext");
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        Assert.isTrue(beanFactory instanceof ConfigurableListableBeanFactory,
            "beanFactory must implement ConfigurableListableBeanFactory");
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    public ApplicationRuntimeModel getApplicationRuntimeModel() {
        return application;
    }

    public void setApplicationRuntimeModel(ApplicationRuntimeModel application) {
        this.application = application;
    }

    protected BaseStat createBaseStat() {
        return new BaseStat();
    }

    /**
     * Do process pipeline stage, subclasses should override this method.
     *
     * @throws Exception if a failure occurred
     */
    protected abstract void doProcess() throws Exception;
}

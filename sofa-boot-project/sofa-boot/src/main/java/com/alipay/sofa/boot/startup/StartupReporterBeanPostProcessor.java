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
package com.alipay.sofa.boot.startup;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;

/**
 * Implementation of {@link BeanPostProcessor} to inject StartupReporter to {@link StartupReporterAware} beans.
 *
 * @author huzijie
 * @version StartupReporterBeanPostProcessor.java, v 0.1 2023年01月12日 6:13 PM huzijie Exp $
 * @since 4.0.0
 */
public class StartupReporterBeanPostProcessor implements BeanPostProcessor {

    private final StartupReporter startupReporter;

    public StartupReporterBeanPostProcessor(StartupReporter startupReporter) {
        this.startupReporter = startupReporter;
    }

    @Override
    @Nullable
    public Object postProcessBeforeInitialization(Object bean, String beanName)
                                                                               throws BeansException {
        if (bean instanceof StartupReporterAware) {
            ((StartupReporterAware) bean).setStartupReporter(startupReporter);
        }
        return bean;
    }
}

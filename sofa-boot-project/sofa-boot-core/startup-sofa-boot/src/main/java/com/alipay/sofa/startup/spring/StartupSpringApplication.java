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
package com.alipay.sofa.startup.spring;

import com.alipay.sofa.boot.startup.CommonStartupCost;
import com.alipay.sofa.startup.SofaStartupContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.Assert;

/**
 * Wrapper for SpringApplication to calculate startup time
 *
 * @Author: Zhijie
 * @Date: 2020/7/8
 */
public class StartupSpringApplication extends SpringApplication {

    public StartupSpringApplication(Class<?>... primarySources) {
        super(primarySources);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void applyInitializers(ConfigurableApplicationContext context) {
        for (ApplicationContextInitializer initializer : getInitializers()) {
            Class<?> requiredType = GenericTypeResolver.resolveTypeArgument(initializer.getClass(),
                ApplicationContextInitializer.class);
            Assert.isInstanceOf(requiredType, context, "Unable to call initializer.");
            CommonStartupCost commonStartupCost = new CommonStartupCost();
            commonStartupCost.setName(initializer.getClass().getName());
            commonStartupCost.setBeginTime(System.currentTimeMillis());
            initializer.initialize(context);
            commonStartupCost.setEndTime(System.currentTimeMillis());
            SofaStartupContext.addInitializer(commonStartupCost);
        }
    }

    public static ConfigurableApplicationContext run(Class<?> primarySource, String[] args) {
        SofaStartupContext.setJvmStartupTime();
        ConfigurableApplicationContext context = run(new Class[] { primarySource }, args);
        ;
        SofaStartupContext.setAppStartupTime();
        return context;
    }

    public static ConfigurableApplicationContext run(Class<?> primarySource, String[] args,
                                                     WebApplicationType type) {
        SofaStartupContext.setJvmStartupTime();
        ConfigurableApplicationContext context = run(new Class[] { primarySource }, args, type);
        ;
        SofaStartupContext.setAppStartupTime();
        return context;
    }

    public static ConfigurableApplicationContext run(Class<?>[] primarySource, String[] args) {
        return new StartupSpringApplication(primarySource).run(args);
    }

    public static ConfigurableApplicationContext run(Class<?>[] primarySource, String[] args,
                                                     WebApplicationType type) {
        StartupSpringApplication startupSpringApplication = new StartupSpringApplication(
            primarySource);
        startupSpringApplication.setWebApplicationType(type);
        return startupSpringApplication.run(args);
    }
}
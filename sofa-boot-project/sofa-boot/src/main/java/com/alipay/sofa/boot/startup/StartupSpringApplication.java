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

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Extend of {@link SpringApplication} to compute {@link ApplicationContextInitializer} initialize cost time.
 *
 * @author huzijie
 * @version StartupSpringApplication.java, v 0.1 2022年03月14日 2:27 PM huzijie Exp $
 */
public class StartupSpringApplication extends SpringApplication {

    private final List<BaseStat> initializerStartupStatList = new ArrayList<>();

    public StartupSpringApplication(Class<?>... primarySources) {
        super(primarySources);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void applyInitializers(ConfigurableApplicationContext context) {
        for (ApplicationContextInitializer initializer : getInitializers()) {
            Class<?> requiredType = GenericTypeResolver.resolveTypeArgument(initializer.getClass(),
                ApplicationContextInitializer.class);
            if (requiredType != null) {
                Assert.isInstanceOf(requiredType, context, "Unable to call initializer.");
                BaseStat stat = new BaseStat();
                stat.setName(initializer.getClass().getName());
                stat.setStartTime(System.currentTimeMillis());
                initializer.initialize(context);
                stat.setEndTime(System.currentTimeMillis());
                initializerStartupStatList.add(stat);
            }
        }
    }

    public List<BaseStat> getInitializerStartupStatList() {
        return initializerStartupStatList;
    }
}

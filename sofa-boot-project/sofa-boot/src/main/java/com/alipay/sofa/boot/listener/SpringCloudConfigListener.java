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
package com.alipay.sofa.boot.listener;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.util.SofaBootEnvUtils;
import com.alipay.sofa.common.log.env.LogEnvUtils;
import com.alipay.sofa.boot.constant.ApplicationListenerOrderConstants;
import org.springframework.boot.Banner;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.env.EnvironmentPostProcessorApplicationListener;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Implementation of {@link ApplicationListener<ApplicationEnvironmentPreparedEvent>} to suit spring cloud environment.
 * <p> used to register sofa logs properties to spring cloud bootstrap env.
 * @see org.springframework.cloud.bootstrap.BootstrapApplicationListener
 * 
 * @author qilong.zql
 * @since 3.0.0
 */
public class SpringCloudConfigListener implements
                                      ApplicationListener<ApplicationEnvironmentPreparedEvent>,
                                      Ordered {

    private final static MapPropertySource HIGH_PRIORITY_CONFIG = new MapPropertySource(
                                                                    SofaBootConstants.SOFA_HIGH_PRIORITY_CONFIG,
                                                                    new HashMap<>());

    /**
     * config log settings
     */
    private void assemblyLogSetting(ConfigurableEnvironment environment) {
        StreamSupport.stream(environment.getPropertySources().spliterator(), false)
            .filter(propertySource -> propertySource instanceof EnumerablePropertySource)
            .map(propertySource -> Arrays
                .asList(((EnumerablePropertySource<?>) propertySource).getPropertyNames()))
                .flatMap(Collection::stream).filter(LogEnvUtils::isSofaCommonLoggingConfig)
                .forEach((key) -> HIGH_PRIORITY_CONFIG.getSource().put(key, environment.getProperty(key)));
    }

    /**
     * config required properties
     */
    private void assemblyRequireProperties(ConfigurableEnvironment environment) {
        if (StringUtils.hasText(environment.getProperty(SofaBootConstants.APP_NAME_KEY))) {
            HIGH_PRIORITY_CONFIG.getSource().put(SofaBootConstants.APP_NAME_KEY,
                environment.getProperty(SofaBootConstants.APP_NAME_KEY));
        }
    }

    @Override
    public int getOrder() {
        return ApplicationListenerOrderConstants.SOFA_BOOTSTRAP_RUN_LISTENER_ORDER;
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        // only work in spring cloud application
        if (SofaBootEnvUtils.isSpringCloudEnvironmentEnabled(environment)) {
            if (environment.getPropertySources().contains(SofaBootConstants.SPRING_CLOUD_BOOTSTRAP)) {
                // in bootstrap application context, add high priority config
                environment.getPropertySources().addLast(HIGH_PRIORITY_CONFIG);
            } else {
                // in application context, build high priority config
                SpringApplication application = event.getSpringApplication();
                StandardEnvironment bootstrapEnvironment = new StandardEnvironment();
                StreamSupport.stream(environment.getPropertySources().spliterator(), false)
                        .filter(source -> !(source instanceof PropertySource.StubPropertySource))
                        .forEach(source -> bootstrapEnvironment.getPropertySources().addLast(source));

                List<Class<?>> sources = new ArrayList<>();
                for (Object s : application.getAllSources()) {
                    if (s instanceof Class) {
                        sources.add((Class<?>) s);
                    } else if (s instanceof String) {
                        sources.add(ClassUtils.resolveClassName((String) s, null));
                    }
                }

                SpringApplication bootstrapApplication = new SpringApplicationBuilder()
                        .profiles(environment.getActiveProfiles()).bannerMode(Banner.Mode.OFF)
                        .environment(bootstrapEnvironment).sources(sources.toArray(new Class[]{}))
                        .registerShutdownHook(false).logStartupInfo(false).web(WebApplicationType.NONE)
                        .listeners().initializers().build(event.getArgs());

                ConfigurableBootstrapContext bootstrapContext = event.getBootstrapContext();
                ApplicationEnvironmentPreparedEvent bootstrapEvent = new ApplicationEnvironmentPreparedEvent(
                        bootstrapContext, bootstrapApplication, event.getArgs(), bootstrapEnvironment);

                application.getListeners().stream()
                        .filter(listener -> listener instanceof EnvironmentPostProcessorApplicationListener)
                        .forEach(listener -> ((EnvironmentPostProcessorApplicationListener) listener)
                                .onApplicationEvent(bootstrapEvent));

                assemblyLogSetting(bootstrapEnvironment);
                assemblyRequireProperties(bootstrapEnvironment);
            }
        }
    }
}

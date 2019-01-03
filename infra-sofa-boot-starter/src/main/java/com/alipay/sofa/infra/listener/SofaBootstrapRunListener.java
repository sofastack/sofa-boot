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
package com.alipay.sofa.infra.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alipay.sofa.common.log.env.LogEnvUtils;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.alipay.sofa.infra.constants.CommonMiddlewareConstants;
import com.alipay.sofa.infra.utils.SOFABootEnvUtils;

/**
 * @author qilong.zql
 * @since 3.0.0
 */
public class SofaBootstrapRunListener implements
                                     ApplicationListener<ApplicationEnvironmentPreparedEvent>,
                                     Ordered {

    private static AtomicBoolean           executed             = new AtomicBoolean(false);
    private final static MapPropertySource HIGH_PRIORITY_CONFIG = new MapPropertySource(
                                                                    CommonMiddlewareConstants.SOFA_HIGH_PRIORITY_CONFIG,
                                                                    new HashMap<String, Object>());

    /**
     * config log settings
     */
    private void assemblyLogSetting(ConfigurableEnvironment environment) {
        for (PropertySource propertySource : environment.getPropertySources()) {
            if (!(propertySource instanceof EnumerablePropertySource)) {
                continue;
            }
            for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
                if (LogEnvUtils.filterAllLogConfig(key)) {
                    HIGH_PRIORITY_CONFIG.getSource().put(key, environment.getProperty(key));
                }
            }
        }
    }

    /**
     * config required properties
     * @param environment
     */
    private void assemblyRequireProperties(ConfigurableEnvironment environment) {
        if (StringUtils.hasText(environment.getProperty(CommonMiddlewareConstants.APP_NAME_KEY))) {
            HIGH_PRIORITY_CONFIG.getSource().put(CommonMiddlewareConstants.APP_NAME_KEY,
                environment.getProperty(CommonMiddlewareConstants.APP_NAME_KEY));
        }
    }

    /**
     * Mark this environment as SOFA bootstrap environment
     * @param environment
     */
    private void assemblyEnvironmentMark(ConfigurableEnvironment environment) {
        environment.getPropertySources().addFirst(
            new MapPropertySource(CommonMiddlewareConstants.SOFA_BOOTSTRAP,
                new HashMap<String, Object>()));
    }

    /**
     * Un-Mark this environment as SOFA bootstrap environment
     * @param environment
     */
    private void unAssemblyEnvironmentMark(ConfigurableEnvironment environment) {
        environment.getPropertySources().remove(CommonMiddlewareConstants.SOFA_BOOTSTRAP);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        SpringApplication application = event.getSpringApplication();
        if (SOFABootEnvUtils.isSpringCloud() && executed.compareAndSet(false, true)) {
            StandardEnvironment bootstrapEnvironment = new StandardEnvironment();
            for (PropertySource propertySource : bootstrapEnvironment.getPropertySources()) {
                if (propertySource instanceof PropertySource.StubPropertySource) {
                    continue;
                }
                bootstrapEnvironment.getPropertySources().addLast(propertySource);
            }

            List<Class> sources = new ArrayList<>();
            for (Object s : application.getSources()) {
                if (s instanceof Class) {
                    sources.add((Class) s);
                } else if (s instanceof String) {
                    sources.add(ClassUtils.resolveClassName((String) s, null));
                }
            }
            SpringApplication bootstrapApplication = new SpringApplicationBuilder()
                .profiles(environment.getActiveProfiles()).bannerMode(Banner.Mode.OFF)
                .environment(bootstrapEnvironment).sources(sources.toArray(new Class[] {}))
                .registerShutdownHook(false).logStartupInfo(false).web(false).listeners()
                .initializers().build(event.getArgs());

            ApplicationEnvironmentPreparedEvent bootstrapEvent = new ApplicationEnvironmentPreparedEvent(
                bootstrapApplication, event.getArgs(), bootstrapEnvironment);

            for (ApplicationListener applicationListener : application.getListeners()) {
                if (applicationListener instanceof ConfigFileApplicationListener) {
                    applicationListener.onApplicationEvent(bootstrapEvent);
                }
            }

            assemblyLogSetting(bootstrapEnvironment);
            assemblyRequireProperties(bootstrapEnvironment);
            assemblyEnvironmentMark(environment);
        } else {
            unAssemblyEnvironmentMark(environment);
        }
        if (environment.getPropertySources().contains(
            CommonMiddlewareConstants.SPRING_CLOUD_BOOTSTRAP)) {
            environment.getPropertySources().addLast(HIGH_PRIORITY_CONFIG);
        }
    }
}
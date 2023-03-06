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
package com.alipay.sofa.boot.env;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Implementation of {@link EnvironmentPostProcessor} to set some properties such as
 * sofa-boot.version,
 *
 * <p> They would be added as a property source named sofaConfigurationProperties.
 *
 * @author qilong.zql
 * @author huzijie
 * @since 2.5.0
 */
public class SofaBootEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String       PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE        = "spring.autoconfigure.exclude";

    private static final List<String> SOFABOOT_EXCLUDE_AUTOCONFIGURATION_CLASSES = List
                                                                                     .of("org.springframework.boot.actuate.autoconfigure.startup.StartupEndpointAutoConfiguration");

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        if (environment.getPropertySources().get(SofaBootConstants.SOFA_DEFAULT_PROPERTY_SOURCE) != null) {
            return;
        }

        addSofaBootDefaultPropertySource(environment);

        addSpringExcludeConfigurationPropertySource(environment);

        // set required properties, {@link MissingRequiredPropertiesException}
        environment.setRequiredProperties(SofaBootConstants.APP_NAME_KEY);
    }

    private void addSofaBootDefaultPropertySource(ConfigurableEnvironment environment) {
        // Get SOFABoot version properties
        Properties defaultConfiguration = getSofaBootVersionProperties();

        // Config default value of {@literal management.endpoints.web.exposure.include}
        defaultConfiguration.put(SofaBootConstants.ENDPOINTS_WEB_EXPOSURE_INCLUDE_CONFIG,
            SofaBootConstants.SOFA_DEFAULT_ENDPOINTS_WEB_EXPOSURE_VALUE);
        PropertiesPropertySource propertySource = new PropertiesPropertySource(
            SofaBootConstants.SOFA_DEFAULT_PROPERTY_SOURCE, defaultConfiguration);
        environment.getPropertySources().addLast(propertySource);
    }

    private void addSpringExcludeConfigurationPropertySource(ConfigurableEnvironment environment) {
        // Append exclude autoconfigure classes config
        Binder binder = Binder.get(environment);
        List<String> excludeConfigs = new ArrayList<>();
        List<String> stringList = binder.bind(PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE, String[].class).map(List::of)
                .orElse(new ArrayList<>());
        excludeConfigs.addAll(SOFABOOT_EXCLUDE_AUTOCONFIGURATION_CLASSES);
        excludeConfigs.addAll(stringList);

        // Update spring.autoconfigure.exclude
        Properties properties = new Properties();
        properties.put(PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE, StringUtils.collectionToCommaDelimitedString(excludeConfigs));
        PropertiesPropertySource propertySource = new PropertiesPropertySource(
                SofaBootConstants.SOFA_EXCLUDE_AUTO_CONFIGURATION_PROPERTY_SOURCE, properties);
        environment.getPropertySources().addFirst(propertySource);
    }

    /**
     * Get SOFABoot Version and print it on banner
     */
    protected Properties getSofaBootVersionProperties() {
        Properties properties = new Properties();
        String sofaBootVersion = getSofaBootVersion();
        // generally, it would not be null and just for test.
        sofaBootVersion = !StringUtils.hasText(sofaBootVersion) ? "" : sofaBootVersion;
        String sofaBootFormattedVersion = sofaBootVersion.isEmpty() ? "" : String.format(" (v%s)",
            sofaBootVersion);
        properties.setProperty(SofaBootConstants.SOFA_BOOT_VERSION, sofaBootVersion);
        properties.setProperty(SofaBootConstants.SOFA_BOOT_FORMATTED_VERSION,
            sofaBootFormattedVersion);
        return properties;
    }

    /**
     * Get SOFABoot Version string.
     */
    protected String getSofaBootVersion() {
        return SofaBootEnvironmentPostProcessor.class.getPackage().getImplementationVersion();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }
}

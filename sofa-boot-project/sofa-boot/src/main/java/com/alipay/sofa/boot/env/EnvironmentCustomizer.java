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
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Used to reset some special key property, such as management.endpoints.web.exposure.include and
 * sofa-boot.version etc. They would be added as a property source named sofaConfigurationProperties
 *
 * @author qilong.zql
 * @since 2.5.0
 */
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class EnvironmentCustomizer implements EnvironmentPostProcessor {

    private static final String       PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE        = "spring.autoconfigure.exclude";

    private static final List<String> SOFABOOT_EXCLUDE_AUTOCONFIGURATION_CLASSES = new ArrayList<>();

    static {
        SOFABOOT_EXCLUDE_AUTOCONFIGURATION_CLASSES
            .add("org.springframework.boot.actuate.autoconfigure.startup.StartupEndpointAutoConfiguration");
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        if (environment.getPropertySources().get(SofaBootConstants.SOFA_DEFAULT_PROPERTY_SOURCE) != null) {
            return;
        }

        // add exclude configuration properties
        addSpringExcludeConfigurationPropertySource(environment);

        // Get SOFABoot version properties
        Properties defaultConfiguration = getSofaBootVersionProperties();

        // Config default value of {@literal management.endpoints.web.exposure.include}
        defaultConfiguration.put(SofaBootConstants.ENDPOINTS_WEB_EXPOSURE_INCLUDE_CONFIG,
            SofaBootConstants.SOFA_DEFAULT_ENDPOINTS_WEB_EXPOSURE_VALUE);
        defaultConfiguration.put(SofaBootConstants.ENDPOINT_AVAILABILITY_GROUP_CONFIG_KEY,
            SofaBootConstants.DEFAULT_ENDPOINT_AVAILABILITY_GROUP_CONFIG_VALUE);

        PropertiesPropertySource propertySource = new PropertiesPropertySource(
            SofaBootConstants.SOFA_DEFAULT_PROPERTY_SOURCE, defaultConfiguration);
        environment.getPropertySources().addLast(propertySource);

        // set required properties, {@link MissingRequiredPropertiesException}
        environment.setRequiredProperties(SofaBootConstants.APP_NAME_KEY);
    }

    /**
     * {@link org.springframework.boot.ResourceBanner#getVersionsMap}
     * Get SOFABoot Version and print it on banner
     */
    protected Properties getSofaBootVersionProperties() {
        Properties properties = new Properties();
        String sofaBootVersion = getSofaBootVersion();
        // generally, it would not be null and just for test.
        sofaBootVersion = StringUtils.isEmpty(sofaBootVersion) ? "" : sofaBootVersion;
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
        return EnvironmentCustomizer.class.getPackage().getImplementationVersion();
    }

    protected void addSpringExcludeConfigurationPropertySource(ConfigurableEnvironment environment) {
        // Append exclude autoconfigure classes config
        Binder binder = Binder.get(environment);
        List<String> excludeConfigs = new ArrayList<>();
        List<String> stringList = binder.bind(PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE, String[].class).map(Arrays::asList)
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
}

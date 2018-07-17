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
package com.alipay.sofa.healthcheck.configuration;

import com.alipay.sofa.healthcheck.log.SofaBootHealthCheckLoggerFactory;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by liangen on 17/8/6.
 */
public class HealthCheckConfiguration {
    private static final Logger logger      = SofaBootHealthCheckLoggerFactory
                                                .getLogger(HealthCheckConfiguration.class);

    //Used to store the read properties in a configuration file.
    private static Properties   properties  = new Properties();

    private static Environment  environment = null;

    public static String getProperty(String key) {
        String result = null;
        if (environment != null) {
            result = environment.getProperty(key);
            if (result != null) {
                return result;
            }
        }
        return (String) properties.get(key);
    }

    public static boolean containsKey(String key) {
        if (environment != null && environment.containsProperty(key)) {
            return true;
        }
        return properties.containsKey(key);
    }

    public static void embededInit() {
        ClassLoader classLoader = null;
        if (Thread.currentThread().getContextClassLoader() != null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        } else {
            classLoader = ClassUtils.getDefaultClassLoader();
        }
        InputStream inputStream = null;
        try {
            inputStream = classLoader != null ? classLoader
                .getResourceAsStream("META-INF/application.properties") : ClassLoader
                .getSystemResourceAsStream("META-INF/application.properties");
            if (inputStream != null) {
                properties.load(inputStream);
                inputStream.close();
            }
        } catch (Exception e) {
            logger
                .error("the default properties of application.properties does not exist in fisrt level of the resource path ");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger
                        .error("the inputStream of read the application.properties close exception");

                }
            }
        }
    }

    public static Environment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(Environment environment) {
        HealthCheckConfiguration.environment = environment;
    }

    /**
     * Reads the value of the specified key from the system properties and configuration files.
     * If this value exists in the system property, the value of the system attribute is preferred.
     * I will try to read the value in the key naming mode of XXX. XXX. If I do not try xxx_xxx_xxx, I will read it.
     * The mapping of the two naming methods is defined {@link HealthCheckConfigurationMapping}
     * @param key
     * @return
     */
    public static String getPropertyAllCircumstances(String key) {
        if (!StringUtils.hasText(key)) {
            return null;
        }

        String value = System.getProperty(key);
        if (!StringUtils.hasText(value)) {
            value = System.getProperty(HealthCheckConfigurationMapping.dotMap.get(key));
        }
        if (!StringUtils.hasText(value)) {
            value = HealthCheckConfiguration.getProperty(key);
        }
        if (!StringUtils.hasText(value)) {
            value = HealthCheckConfiguration.getProperty(HealthCheckConfigurationMapping.dotMap
                .get(key));
        }

        return value;
    }

}

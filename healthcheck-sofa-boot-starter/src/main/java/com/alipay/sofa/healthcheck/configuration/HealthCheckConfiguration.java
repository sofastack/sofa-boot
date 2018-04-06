/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
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
    private static final Logger logger      = SofaBootHealthCheckLoggerFactory.getLogger(HealthCheckConfiguration.class
                                                .getCanonicalName());

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
            inputStream = classLoader != null ? classLoader.getResourceAsStream("META-INF/application.properties")
                : ClassLoader.getSystemResourceAsStream("META-INF/application.properties");
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
                    logger.error("the inputStream of read the application.properties close exception");

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
            value = HealthCheckConfiguration.getProperty(HealthCheckConfigurationMapping.dotMap.get(key));
        }

        return value;
    }

}

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
package com.alipay.sofa.boot.logging;

import com.alipay.sofa.boot.util.SofaBootEnvUtils;
import com.alipay.sofa.common.log.CommonLoggingConfigurations;
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.log.env.LogEnvUtils;
import com.alipay.sofa.common.thread.SofaThreadPoolConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link EnvironmentPostProcessor}
 * to register spring environment to {@link CommonLoggingConfigurations}.
 * 
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/11/7
 */
public class LogEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    public static final int    ORDER                            = ConfigDataEnvironmentPostProcessor.ORDER + 1;

    /**
     * support use config to disable sofa common thread pool monitor.
     */
    public static final String SOFA_THREAD_POOL_MONITOR_DISABLE = "sofa.boot.tools.threadpool.monitor.disable";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        defaultConsoleLoggers();
        initLoggingConfig(environment);
        initSofaCommonThread(environment);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    private void initLoggingConfig(ConfigurableEnvironment environment) {
        Map<String, String> context = new HashMap<>();
        loadLogConfiguration(Constants.LOG_PATH, environment.getProperty(Constants.LOG_PATH),
            Constants.LOGGING_PATH_DEFAULT, context);
        loadLogConfiguration(Constants.OLD_LOG_PATH,
            environment.getProperty(Constants.OLD_LOG_PATH), context.get(Constants.LOG_PATH),
            context);
        loadLogConfiguration(Constants.LOG_ENCODING_PROP_KEY,
            environment.getProperty(Constants.LOG_ENCODING_PROP_KEY), context);
        // Don't delete this!
        // Some old SOFA SDKs rely on JVM system properties to determine log configurations.
        LogEnvUtils.keepCompatible(context, true);

        for (Map.Entry<String, String> entry : context.entrySet()) {
            CommonLoggingConfigurations.loadExternalConfiguration(entry.getKey(), entry.getValue());
        }

        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            if (propertySource instanceof EnumerablePropertySource) {
                for (String key : ((EnumerablePropertySource) propertySource).getPropertyNames()) {
                    if (LogEnvUtils.isSofaCommonLoggingConfig(key)) {
                        CommonLoggingConfigurations.loadExternalConfiguration(key,
                            environment.getProperty(key));
                    }
                }
            }
        }
    }

    private void defaultConsoleLoggers() {
        if (SofaBootEnvUtils.isLocalEnv() || SofaBootEnvUtils.isSpringTestEnv()) {
            CommonLoggingConfigurations.loadExternalConfiguration(
                Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH, "true");
        }
    }

    private void loadLogConfiguration(String key, String value, String defaultValue,
                                      Map<String, String> context) {
        if (StringUtils.hasText(value)) {
            context.put(key, value);
        } else {
            context.put(key, defaultValue);
        }
    }

    private void loadLogConfiguration(String key, String value, Map<String, String> context) {
        if (StringUtils.hasText(value)) {
            context.put(key, value);
        }
    }

    private void initSofaCommonThread(ConfigurableEnvironment environment) {
        if (Boolean.parseBoolean(environment.getProperty(SOFA_THREAD_POOL_MONITOR_DISABLE))) {
            System
                .setProperty(SofaThreadPoolConstants.SOFA_THREAD_POOL_LOGGING_CAPABILITY, "false");
        }
    }
}

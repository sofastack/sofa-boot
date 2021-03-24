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

import com.alipay.sofa.common.log.CommonLoggingConfigurations;
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.log.env.LogEnvUtils;
import com.alipay.sofa.common.utils.StringUtil;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/11/7
 */
public class LogEnvironmentPreparingListener
                                            implements
                                            ApplicationListener<ApplicationEnvironmentPreparedEvent>,
                                            Ordered {
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        defaultConsoleLoggers();
        prepare(event.getEnvironment());
    }

    @Override
    public int getOrder() {
        // Must be invoked after ConfigFileApplicationListener
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }

    private void prepare(ConfigurableEnvironment environment) {
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
        if (LocalEnvUtil.isLocalEnv()) {
            CommonLoggingConfigurations.loadExternalConfiguration(
                Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH, "true");
        }
    }

    private void loadLogConfiguration(String key, String value, String defaultValue,
                                      Map<String, String> context) {
        if (!StringUtil.isBlank(value)) {
            context.put(key, value);
        } else {
            context.put(key, defaultValue);
        }
    }

    private void loadLogConfiguration(String key, String value, Map<String, String> context) {
        if (!StringUtil.isBlank(value)) {
            context.put(key, value);
        }
    }
}

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
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

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
        prepare(event.getEnvironment());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }

    private void prepare(ConfigurableEnvironment environment) {
        CommonLoggingConfigurations.loadExternalConfiguration(Constants.LOG_PATH,
            environment.getProperty(Constants.LOG_PATH));
        CommonLoggingConfigurations.loadExternalConfiguration(Constants.OLD_LOG_PATH,
            environment.getProperty(Constants.OLD_LOG_PATH));
        CommonLoggingConfigurations.loadExternalConfiguration(Constants.LOG_ENCODING_PROP_KEY,
            environment.getProperty(Constants.LOG_ENCODING_PROP_KEY));

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
}

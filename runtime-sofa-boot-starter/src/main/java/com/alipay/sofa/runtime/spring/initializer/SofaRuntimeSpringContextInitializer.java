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
package com.alipay.sofa.runtime.spring.initializer;

import com.alipay.boot.sofarpc.configuration.Slite2Configuration;
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.runtime.initializer.SofaFrameworkInitializer;
import com.alipay.sofa.runtime.spi.log.SofaRuntimeLoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author xuanbei 18/3/13
 */
public class SofaRuntimeSpringContextInitializer
                                                implements
                                                ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (Slite2Configuration.getEnvironment() == null) {
            Slite2Configuration.setEnvironment(applicationContext.getEnvironment());
        }
        initLogger();
        SofaFrameworkInitializer.initialize(Slite2Configuration.getAppName(), applicationContext);
    }

    private void initLogger() {
        // init logging.path argument
        if (Slite2Configuration.containsKey(Constants.LOG_PATH)) {
            System.setProperty(Constants.LOG_PATH,
                Slite2Configuration.getProperty(Constants.LOG_PATH));
        }

        // init logging.level.com.alipay.sofa.runtime argument
        String runtimeLogLevelKey = Constants.LOG_LEVEL_PREFIX
                                    + SofaRuntimeLoggerFactory.SOFA_RUNTIME_LOG_SPACE;
        String runtimeLogLevelValue = Slite2Configuration.getProperty(runtimeLogLevelKey);
        if (runtimeLogLevelValue != null) {
            System.setProperty(runtimeLogLevelKey, runtimeLogLevelValue);
        }

        // init file.encoding
        String fileEncoding = Slite2Configuration.getProperty(Constants.LOG_ENCODING_PROP_KEY);
        if (fileEncoding != null) {
            System.setProperty(Constants.LOG_ENCODING_PROP_KEY, fileEncoding);
        }
    }
}

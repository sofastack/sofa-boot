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

import com.alipay.sofa.common.config.SofaConfigs;
import com.alipay.sofa.common.config.source.AbstractConfigSource;
import com.alipay.sofa.boot.util.ApplicationListenerOrderConstants;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * add a config source based on {@link ConfigurableEnvironment}
 * @author huzijie
 * @version SofaConfigSourceListener.java, v 0.1 2020年12月22日 7:34 下午 huzijie Exp $
 */
public class SofaConfigSourceSupportListener
                                            implements
                                            ApplicationListener<ApplicationEnvironmentPreparedEvent>,
                                            Ordered {
    private static final int    SOFA_BOOT_CONFIG_SOURCE_ORDER = ApplicationListenerOrderConstants.SOFA_CONFIG_SOURCE_SUPPORT_LISTENER_ORDER;

    private final AtomicBoolean registered                    = new AtomicBoolean();

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        registerSofaConfigs(event.getEnvironment());
    }

    private void registerSofaConfigs(ConfigurableEnvironment environment) {
        if (registered.compareAndSet(false, true)) {
            SofaConfigs.addConfigSource(new AbstractConfigSource() {

                @Override
                public int getOrder() {
                    return SOFA_BOOT_CONFIG_SOURCE_ORDER;
                }

                @Override
                public String getName() {
                    return "SOFABootEnv";
                }

                @Override
                public String doGetConfig(String key) {
                    return environment.getProperty(key);
                }

                @Override
                public boolean hasKey(String key) {
                    return !StringUtils.isEmpty(environment.getProperty(key));
                }
            });
        }
    }

    @Override
    public int getOrder() {
        return ApplicationListenerOrderConstants.SOFA_CONFIG_SOURCE_SUPPORT_LISTENER_ORDER;
    }
}

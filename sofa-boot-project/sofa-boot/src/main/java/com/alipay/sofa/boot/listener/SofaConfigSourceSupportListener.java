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

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.constant.ApplicationListenerOrderConstants;
import com.alipay.sofa.common.config.SofaConfigs;
import com.alipay.sofa.common.config.source.AbstractConfigSource;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Register a config source based on {@link ConfigurableEnvironment} to {@link SofaConfigs}.
 *
 * @author huzijie
 * @version SofaConfigSourceListener.java, v 0.1 2020年12月22日 7:34 下午 huzijie Exp $
 */
public class SofaConfigSourceSupportListener
                                            implements
                                            ApplicationListener<ApplicationEnvironmentPreparedEvent>,
                                            Ordered {

    private final AtomicBoolean registered = new AtomicBoolean();

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        if (registered.compareAndSet(false, true)) {
            registerSofaConfigs(event.getEnvironment());
        }
    }

    private void registerSofaConfigs(ConfigurableEnvironment environment) {
        SofaConfigs.addConfigSource(new AbstractConfigSource() {

            @Override
            public int getOrder() {
                return LOWEST_PRECEDENCE;
            }

            @Override
            public String getName() {
                return SofaBootConstants.SOFA_BOOT_SPACE_NAME;
            }

            @Override
            public String doGetConfig(String key) {
                return environment.getProperty(key);
            }

            @Override
            public boolean hasKey(String key) {
                return StringUtils.hasText(environment.getProperty(key));
            }
        });
    }

    @Override
    public int getOrder() {
        return ApplicationListenerOrderConstants.SOFA_CONFIG_SOURCE_SUPPORT_LISTENER_ORDER;
    }
}

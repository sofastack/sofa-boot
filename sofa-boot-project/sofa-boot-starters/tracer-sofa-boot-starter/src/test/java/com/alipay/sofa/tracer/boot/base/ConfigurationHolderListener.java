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
package com.alipay.sofa.tracer.boot.base;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import com.alipay.common.tracer.core.configuration.SofaTracerConfiguration;
import com.alipay.common.tracer.core.utils.StringUtils;
import com.alipay.sofa.boot.util.SofaBootEnvUtils;
import com.alipay.sofa.tracer.boot.properties.SofaTracerProperties;

/**
 * @author qilong.zql
 * @since 2.2.2
 */
public class ConfigurationHolderListener implements
                                        ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        if (SofaBootEnvUtils.isSpringCloudBootstrapEnvironment(environment)) {
            return;
        }
        SofaTracerProperties sofaTracerProperties = new SofaTracerProperties();
        sofaTracerProperties.setDisableDigestLog(SofaTracerConfiguration
            .getProperty(SofaTracerConfiguration.DISABLE_MIDDLEWARE_DIGEST_LOG_KEY));
        sofaTracerProperties.setDisableConfiguration(SofaTracerConfiguration
            .getMapEmptyIfNull(SofaTracerConfiguration.DISABLE_DIGEST_LOG_KEY));
        sofaTracerProperties.setTracerGlobalRollingPolicy(SofaTracerConfiguration
            .getProperty(SofaTracerConfiguration.TRACER_GLOBAL_ROLLING_KEY));
        sofaTracerProperties.setTracerGlobalLogReserveDay(SofaTracerConfiguration
            .getProperty(SofaTracerConfiguration.TRACER_GLOBAL_LOG_RESERVE_DAY));
        sofaTracerProperties.setStatLogInterval(SofaTracerConfiguration
            .getProperty(SofaTracerConfiguration.STAT_LOG_INTERVAL));
        sofaTracerProperties.setBaggageMaxLength(SofaTracerConfiguration
            .getProperty(SofaTracerConfiguration.TRACER_PENETRATE_ATTRIBUTE_MAX_LENGTH));
        sofaTracerProperties.setSamplerName(SofaTracerConfiguration
            .getProperty(SofaTracerConfiguration.SAMPLER_STRATEGY_NAME_KEY));
        sofaTracerProperties.setSamplerCustomRuleClassName(SofaTracerConfiguration
            .getProperty(SofaTracerConfiguration.SAMPLER_STRATEGY_CUSTOM_RULE_CLASS_NAME));
        String property = SofaTracerConfiguration
            .getProperty(SofaTracerConfiguration.SAMPLER_STRATEGY_PERCENTAGE_KEY);
        sofaTracerProperties
            .setSamplerPercentage(Float.valueOf(StringUtils.isBlank(property) ? "100" : property));
        ConfigurationHolder.setSofaTracerProperties(sofaTracerProperties);
    }
}
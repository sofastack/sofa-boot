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
package com.alipay.sofa.tracer.boot.listener;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;

import com.alipay.common.tracer.core.configuration.SofaTracerConfiguration;
import com.alipay.common.tracer.core.utils.StringUtils;
import com.alipay.sofa.boot.util.SofaBootEnvUtils;
import com.alipay.sofa.tracer.boot.properties.SofaTracerProperties;

/**
 * Parse SOFATracer Configuration in early stage.
 *
 * @author qilong.zql
 * @since 2.2.2
 */
public class SofaTracerConfigurationListener
                                            implements
                                            ApplicationListener<ApplicationEnvironmentPreparedEvent>,
                                            Ordered {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();

        if (SofaBootEnvUtils.isSpringCloudBootstrapEnvironment(environment)) {
            return;
        }

        // check spring.application.name
        String applicationName = environment
            .getProperty(SofaTracerConfiguration.TRACER_APPNAME_KEY);
        Assert.isTrue(!StringUtils.isBlank(applicationName),
            SofaTracerConfiguration.TRACER_APPNAME_KEY + " must be configured!");
        SofaTracerConfiguration.setProperty(SofaTracerConfiguration.TRACER_APPNAME_KEY,
            applicationName);

        // static binding
        SofaTracerProperties tempTarget = new SofaTracerProperties();
        ConfigurationProperties configurationProperties = AnnotationUtils.findAnnotation(
            SofaTracerProperties.class, ConfigurationProperties.class);
        Binder binder = Binder.get(environment);
        Bindable bindable = Bindable.of(SofaTracerProperties.class).withExistingValue(tempTarget)
            .withAnnotations(configurationProperties);
        binder.bind(SofaTracerProperties.SOFA_TRACER_CONFIGURATION_PREFIX, bindable);

        //properties convert to tracer
        SofaTracerConfiguration.setProperty(
            SofaTracerConfiguration.DISABLE_MIDDLEWARE_DIGEST_LOG_KEY,
            tempTarget.getDisableDigestLog());
        SofaTracerConfiguration.setProperty(SofaTracerConfiguration.DISABLE_DIGEST_LOG_KEY,
            tempTarget.getDisableConfiguration());
        SofaTracerConfiguration.setProperty(SofaTracerConfiguration.TRACER_GLOBAL_ROLLING_KEY,
            tempTarget.getTracerGlobalRollingPolicy());
        SofaTracerConfiguration.setProperty(SofaTracerConfiguration.TRACER_GLOBAL_LOG_RESERVE_DAY,
            tempTarget.getTracerGlobalLogReserveDay());
        //stat log interval
        SofaTracerConfiguration.setProperty(SofaTracerConfiguration.STAT_LOG_INTERVAL,
            tempTarget.getStatLogInterval());
        //baggage length
        SofaTracerConfiguration.setProperty(
            SofaTracerConfiguration.TRACER_PENETRATE_ATTRIBUTE_MAX_LENGTH,
            tempTarget.getBaggageMaxLength());
        SofaTracerConfiguration.setProperty(
            SofaTracerConfiguration.TRACER_SYSTEM_PENETRATE_ATTRIBUTE_MAX_LENGTH,
            tempTarget.getBaggageMaxLength());

        //sampler config
        if (tempTarget.getSamplerName() != null) {
            SofaTracerConfiguration.setProperty(SofaTracerConfiguration.SAMPLER_STRATEGY_NAME_KEY,
                tempTarget.getSamplerName());
        }
        if (StringUtils.isNotBlank(tempTarget.getSamplerCustomRuleClassName())) {
            SofaTracerConfiguration.setProperty(
                SofaTracerConfiguration.SAMPLER_STRATEGY_CUSTOM_RULE_CLASS_NAME,
                tempTarget.getSamplerCustomRuleClassName());
        }
        SofaTracerConfiguration.setProperty(
            SofaTracerConfiguration.SAMPLER_STRATEGY_PERCENTAGE_KEY,
            String.valueOf(tempTarget.getSamplerPercentage()));
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 30;
    }
}
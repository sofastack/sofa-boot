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
package com.alipay.sofa.boot.autoconfigure.tracer;

import com.alipay.common.tracer.core.configuration.SofaTracerConfiguration;
import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.constant.ApplicationListenerOrderConstants;
import com.alipay.sofa.boot.util.SofaBootEnvUtils;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Parse SOFATracer Configuration in early stage.
 *
 * @author qilong.zql
 * @author huzijie
 * @since 2.2.2
 */
public class SofaTracerConfigurationListener
                                            implements
                                            ApplicationListener<ApplicationEnvironmentPreparedEvent>,
                                            Ordered {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        if (SofaBootEnvUtils.isSpringCloudBootstrapEnvironment(event.getEnvironment())) {
            return;
        }

        ConfigurableEnvironment environment = event.getEnvironment();

        // check spring.application.name
        String applicationName = environment.getProperty(SofaBootConstants.APP_NAME_KEY);
        Assert.isTrue(StringUtils.hasText(applicationName), SofaBootConstants.APP_NAME_KEY
                                                            + " must be configured!");
        SofaTracerConfiguration.setProperty(SofaTracerConfiguration.TRACER_APPNAME_KEY,
            applicationName);

        Binder binder = Binder.get(environment);
        SofaTracerProperties sofaTracerProperties = new SofaTracerProperties();
        Bindable<SofaTracerProperties> bindable = Bindable.of(SofaTracerProperties.class)
            .withExistingValue(sofaTracerProperties);
        binder.bind("sofa.boot.tracer", bindable);

        //properties convert to tracer
        SofaTracerConfiguration.setProperty(
            SofaTracerConfiguration.DISABLE_MIDDLEWARE_DIGEST_LOG_KEY,
            sofaTracerProperties.getDisableDigestLog());
        SofaTracerConfiguration.setProperty(SofaTracerConfiguration.DISABLE_DIGEST_LOG_KEY,
            sofaTracerProperties.getDisableConfiguration());
        SofaTracerConfiguration.setProperty(SofaTracerConfiguration.TRACER_GLOBAL_ROLLING_KEY,
            sofaTracerProperties.getTracerGlobalRollingPolicy());
        SofaTracerConfiguration.setProperty(SofaTracerConfiguration.TRACER_GLOBAL_LOG_RESERVE_DAY,
            sofaTracerProperties.getTracerGlobalLogReserveDay());
        //stat log interval
        SofaTracerConfiguration.setProperty(SofaTracerConfiguration.STAT_LOG_INTERVAL,
            sofaTracerProperties.getStatLogInterval());
        //baggage length
        SofaTracerConfiguration.setProperty(
            SofaTracerConfiguration.TRACER_PENETRATE_ATTRIBUTE_MAX_LENGTH,
            sofaTracerProperties.getBaggageMaxLength());
        SofaTracerConfiguration.setProperty(
            SofaTracerConfiguration.TRACER_SYSTEM_PENETRATE_ATTRIBUTE_MAX_LENGTH,
            sofaTracerProperties.getBaggageMaxLength());

        //sampler config
        if (sofaTracerProperties.getSamplerName() != null) {
            SofaTracerConfiguration.setProperty(SofaTracerConfiguration.SAMPLER_STRATEGY_NAME_KEY,
                sofaTracerProperties.getSamplerName());
        }
        if (StringUtils.hasText(sofaTracerProperties.getSamplerCustomRuleClassName())) {
            SofaTracerConfiguration.setProperty(
                SofaTracerConfiguration.SAMPLER_STRATEGY_CUSTOM_RULE_CLASS_NAME,
                sofaTracerProperties.getSamplerCustomRuleClassName());
        }
        SofaTracerConfiguration.setProperty(
            SofaTracerConfiguration.SAMPLER_STRATEGY_PERCENTAGE_KEY,
            String.valueOf(sofaTracerProperties.getSamplerPercentage()));

        SofaTracerConfiguration.setProperty(SofaTracerConfiguration.JSON_FORMAT_OUTPUT,
            String.valueOf(sofaTracerProperties.isJsonOutput()));
    }

    @Override
    public int getOrder() {
        return ApplicationListenerOrderConstants.SOFA_TRACER_CONFIGURATION_LISTENER_ORDER;
    }
}

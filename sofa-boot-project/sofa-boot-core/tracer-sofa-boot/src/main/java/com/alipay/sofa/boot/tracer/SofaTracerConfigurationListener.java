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
package com.alipay.sofa.boot.tracer;

import com.alipay.common.tracer.core.appender.file.TimedRollingFileAppender;
import com.alipay.common.tracer.core.configuration.SofaTracerConfiguration;
import com.alipay.common.tracer.core.reporter.stat.manager.SofaTracerStatisticReporterManager;
import com.alipay.sofa.boot.constant.SofaBootConstants;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.alipay.common.tracer.core.configuration.SofaTracerConfiguration.DEFAULT_LOG_RESERVE_DAY;

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

    private static final boolean       IS_SPRING_CLOUD = ClassUtils
                                                           .isPresent(
                                                               "org.springframework.cloud.bootstrap.BootstrapConfiguration",
                                                               null);

    private static final AtomicBoolean EXECUTED        = new AtomicBoolean(false);

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        if (!EXECUTED.compareAndSet(false, true)) {
            return;
        }

        ConfigurableEnvironment environment = event.getEnvironment();

        // check spring.application.name
        String applicationName = environment
            .getProperty(SofaTracerConfiguration.TRACER_APPNAME_KEY);
        Assert.isTrue(StringUtils.hasText(applicationName),
            SofaTracerConfiguration.TRACER_APPNAME_KEY + " must be configured!");
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
        return HIGHEST_PRECEDENCE + 30;
    }

    private boolean isSpringCloudBootstrapEnvironment(Environment environment) {
        if (!(environment instanceof ConfigurableEnvironment)) {
            return false;
        } else {
            return !((ConfigurableEnvironment) environment).getPropertySources().contains(
                SofaBootConstants.SOFA_BOOTSTRAP);
        }
    }

    static class SofaTracerProperties {

        private String              disableDigestLog          = "false";

        private Map<String, String> disableConfiguration      = new HashMap<>();

        private String              tracerGlobalRollingPolicy = TimedRollingFileAppender.DAILY_ROLLING_PATTERN;

        private String              tracerGlobalLogReserveDay = String
                                                                  .valueOf(DEFAULT_LOG_RESERVE_DAY);

        private String              statLogInterval           = String
                                                                  .valueOf(SofaTracerStatisticReporterManager.DEFAULT_CYCLE_SECONDS);

        private String              baggageMaxLength          = String
                                                                  .valueOf(SofaTracerConfiguration.PEN_ATTRS_LENGTH_TRESHOLD);

        private String              samplerName;

        private float               samplerPercentage         = 100;

        private String              samplerCustomRuleClassName;

        private boolean             jsonOutput                = true;

        public String getDisableDigestLog() {
            return disableDigestLog;
        }

        public void setDisableDigestLog(String disableDigestLog) {
            this.disableDigestLog = disableDigestLog;
        }

        public Map<String, String> getDisableConfiguration() {
            return disableConfiguration;
        }

        public void setDisableConfiguration(Map<String, String> disableConfiguration) {
            this.disableConfiguration = disableConfiguration;
        }

        public String getTracerGlobalRollingPolicy() {
            return tracerGlobalRollingPolicy;
        }

        public void setTracerGlobalRollingPolicy(String tracerGlobalRollingPolicy) {
            this.tracerGlobalRollingPolicy = tracerGlobalRollingPolicy;
        }

        public String getTracerGlobalLogReserveDay() {
            return tracerGlobalLogReserveDay;
        }

        public void setTracerGlobalLogReserveDay(String tracerGlobalLogReserveDay) {
            this.tracerGlobalLogReserveDay = tracerGlobalLogReserveDay;
        }

        public String getStatLogInterval() {
            return statLogInterval;
        }

        public void setStatLogInterval(String statLogInterval) {
            this.statLogInterval = statLogInterval;
        }

        public String getBaggageMaxLength() {
            return baggageMaxLength;
        }

        public void setBaggageMaxLength(String baggageMaxLength) {
            this.baggageMaxLength = baggageMaxLength;
        }

        public String getSamplerName() {
            return samplerName;
        }

        public void setSamplerName(String samplerName) {
            this.samplerName = samplerName;
        }

        public float getSamplerPercentage() {
            return samplerPercentage;
        }

        public void setSamplerPercentage(float samplerPercentage) {
            this.samplerPercentage = samplerPercentage;
        }

        public String getSamplerCustomRuleClassName() {
            return samplerCustomRuleClassName;
        }

        public void setSamplerCustomRuleClassName(String samplerCustomRuleClassName) {
            this.samplerCustomRuleClassName = samplerCustomRuleClassName;
        }

        public boolean isJsonOutput() {
            return jsonOutput;
        }

        public void setJsonOutput(boolean jsonOutput) {
            this.jsonOutput = jsonOutput;
        }
    }
}

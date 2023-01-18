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

import com.alipay.common.tracer.core.appender.file.TimedRollingFileAppender;
import com.alipay.common.tracer.core.configuration.SofaTracerConfiguration;
import com.alipay.common.tracer.core.reporter.stat.manager.SofaTracerStatisticReporterManager;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

import static com.alipay.common.tracer.core.configuration.SofaTracerConfiguration.DEFAULT_LOG_RESERVE_DAY;

/**
 * Configuration properties to configure tracer.
 *
 * @author yangguanchao
 * @author huzijie
 * @since 2018/04/30
 */
@ConfigurationProperties("sofa.boot.tracer")
public class SofaTracerProperties {

    /**
     * Disable digest log.
     */
    private String              disableDigestLog          = "false";

    /**
     * Disable configuration.
     */
    private Map<String, String> disableConfiguration      = new HashMap<String, String>();

    /**
     * Tracer's global rolling configuration.
     */
    private String              tracerGlobalRollingPolicy = TimedRollingFileAppender.DAILY_ROLLING_PATTERN;

    /**
     * Tracer's global log retention days configuration.
     */
    private String              tracerGlobalLogReserveDay = String.valueOf(DEFAULT_LOG_RESERVE_DAY);

    /**
     * The interval for printing the stat log.
     */
    private String              statLogInterval           = String
                                                              .valueOf(SofaTracerStatisticReporterManager.DEFAULT_CYCLE_SECONDS);

    /**
     * Threshold, the length of the service transparent field
     */
    private String              baggageMaxLength          = String
                                                              .valueOf(SofaTracerConfiguration.PEN_ATTRS_LENGTH_TRESHOLD);

    /**
     * Sampling policy name.
     */
    private String              samplerName;

    /**
     * Sampling policy percentage.
     */
    private float               samplerPercentage         = 100;

    /**
     * Custom sampling rule class name.
     */
    private String              samplerCustomRuleClassName;

    /**
     * Custom reporter class name.
     */
    private String              reporterName;

    /**
     * Use json to print log.
     */
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

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public boolean isJsonOutput() {
        return jsonOutput;
    }

    public void setJsonOutput(boolean jsonOutput) {
        this.jsonOutput = jsonOutput;
    }
}

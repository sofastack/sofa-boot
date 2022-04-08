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

import com.alipay.common.tracer.core.listener.SpanReportListener;
import com.alipay.common.tracer.core.listener.SpanReportListenerHolder;
import com.alipay.common.tracer.core.reporter.facade.Reporter;
import com.alipay.common.tracer.core.samplers.Sampler;
import com.alipay.common.tracer.core.samplers.SamplerFactory;
import com.alipay.common.tracer.core.utils.StringUtils;
import com.alipay.sofa.tracer.boot.properties.SofaTracerProperties;
import com.alipay.sofa.tracer.plugin.flexible.FlexibleTracer;
import io.opentracing.Tracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * SofaTracerAutoConfiguration
 *
 * @author yangguanchao
 * @since 2018/05/08
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SofaTracerProperties.class)
@ConditionalOnClass({ SpanReportListenerHolder.class, Tracer.class, SofaTracerProperties.class,
                     FlexibleTracer.class })
public class SofaTracerAutoConfiguration {

    private final List<SpanReportListener> spanReportListenerList;

    public SofaTracerAutoConfiguration(List<SpanReportListener> spanReportListenerList) {
        this.spanReportListenerList = spanReportListenerList;
    }

    @Bean
    @ConditionalOnMissingBean
    public SpanReportListenerHolder sofaTracerSpanReportListener() {
        if (this.spanReportListenerList != null && this.spanReportListenerList.size() > 0) {
            //cache in tracer listener core
            SpanReportListenerHolder.addSpanReportListeners(spanReportListenerList);
        }
        return null;
    }

    @Bean
    @ConditionalOnMissingBean
    public Tracer sofaTracer(SofaTracerProperties sofaTracerProperties) throws Exception {
        String reporterName = sofaTracerProperties.getReporterName();
        if (StringUtils.isNotBlank(reporterName)) {
            Reporter reporter = (Reporter) Class.forName(reporterName).newInstance();
            Sampler sampler = SamplerFactory.getSampler();
            return new FlexibleTracer(sampler, reporter);
        }
        Tracer tracer = new FlexibleTracer();
        return tracer;
    }
}

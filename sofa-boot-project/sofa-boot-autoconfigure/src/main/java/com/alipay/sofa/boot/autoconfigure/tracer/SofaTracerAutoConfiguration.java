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
import io.opentracing.Tracer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for sofa tracer.
 *
 * @author yangguanchao
 * @author huzijie
 * @since 2018/05/08
 */
@AutoConfiguration
@EnableConfigurationProperties(SofaTracerProperties.class)
@ConditionalOnClass({ SpanReportListenerHolder.class, Tracer.class })
public class SofaTracerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SpanReportListenerHolder sofaTracerSpanReportListener(List<SpanReportListener> spanReportListenerList) {
        if (!CollectionUtils.isEmpty(spanReportListenerList)) {
            //cache in tracer listener core
            SpanReportListenerHolder.addSpanReportListeners(spanReportListenerList);
        }
        return null;
    }
}

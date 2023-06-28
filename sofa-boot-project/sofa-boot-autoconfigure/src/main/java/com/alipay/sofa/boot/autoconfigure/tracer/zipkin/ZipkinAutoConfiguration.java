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
package com.alipay.sofa.boot.autoconfigure.tracer.zipkin;

import com.alipay.sofa.tracer.plugins.zipkin.ZipkinSofaTracerRestTemplateCustomizer;
import com.alipay.sofa.tracer.plugins.zipkin.ZipkinSofaTracerSpanRemoteReporter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for zipkin.
 *
 * @author yangguanchao
 * @author huzijie
 * @since 2018/05/01
 */
@AutoConfiguration
@EnableConfigurationProperties(ZipkinProperties.class)
@ConditionalOnClass({ zipkin2.Span.class, zipkin2.reporter.AsyncReporter.class, RestTemplate.class,
                     ZipkinSofaTracerRestTemplateCustomizer.class })
@ConditionalOnProperty(name = "sofa.boot.tracer.zipkin.enabled", havingValue = "true", matchIfMissing = true)
public class ZipkinAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ZipkinSofaTracerRestTemplateCustomizer zipkinSofaTracerRestTemplateCustomizer(ZipkinProperties zipkinProperties) {
        return new ZipkinSofaTracerRestTemplateCustomizer(zipkinProperties.isGzipped());
    }

    @Bean
    @ConditionalOnMissingBean
    public ZipkinSofaTracerSpanRemoteReporter zipkinSofaTracerSpanReporter(ZipkinSofaTracerRestTemplateCustomizer zipkinSofaTracerRestTemplateCustomizer,
                                                                           ZipkinProperties zipkinProperties) {
        RestTemplate restTemplate = new RestTemplate();
        zipkinSofaTracerRestTemplateCustomizer.customize(restTemplate);
        return new ZipkinSofaTracerSpanRemoteReporter(restTemplate, zipkinProperties.getBaseUrl());
    }
}

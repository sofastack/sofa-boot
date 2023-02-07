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
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestTemplate;
import zipkin2.reporter.AsyncReporter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ZipkinAutoConfiguration}.
 *
 * @author huzijie
 * @version ZipkinAutoConfigurationTests.java, v 0.1 2023年01月11日 10:38 AM huzijie Exp $
 */
public class ZipkinAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(ZipkinAutoConfiguration.class));

    @Test
    public void registerZipkinBeans() {
        this.contextRunner
                .run((context) -> assertThat(context)
                        .hasSingleBean(ZipkinSofaTracerRestTemplateCustomizer.class)
                        .hasSingleBean(ZipkinSofaTracerSpanRemoteReporter.class));
    }

    @Test
    public void noZipkinBeansWhenZipkin2SpanClassNotExist() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(zipkin2.Span.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(ZipkinSofaTracerRestTemplateCustomizer.class)
                        .doesNotHaveBean(ZipkinSofaTracerSpanRemoteReporter.class));
    }

    @Test
    public void noZipkinBeansWhenAsyncReporterClassNotExist() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(AsyncReporter.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(ZipkinSofaTracerRestTemplateCustomizer.class)
                        .doesNotHaveBean(ZipkinSofaTracerSpanRemoteReporter.class));
    }

    @Test
    public void noZipkinBeansWhenZipkinSofaTracerRestTemplateCustomizerClassNotExist() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(ZipkinSofaTracerRestTemplateCustomizer.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(ZipkinSofaTracerRestTemplateCustomizer.class)
                        .doesNotHaveBean(ZipkinSofaTracerSpanRemoteReporter.class));
    }

    @Test
    public void noZipkinBeansWhenRestTemplateClassNotExist() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(RestTemplate.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(ZipkinSofaTracerRestTemplateCustomizer.class)
                        .doesNotHaveBean(ZipkinSofaTracerSpanRemoteReporter.class));
    }

    @Test
    public void noZipkinBeansWhenPropertySetFalse() {
        this.contextRunner
                .withPropertyValues("sofa.boot.tracer.zipkin.enabled=false")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(ZipkinSofaTracerRestTemplateCustomizer.class)
                        .doesNotHaveBean(ZipkinSofaTracerSpanRemoteReporter.class));
    }
}

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
package com.alipay.sofa.boot.autoconfigure.tracer.resttemplate;

import com.alipay.sofa.boot.tracer.resttemplate.RestTemplateBeanPostProcessor;
import com.alipay.sofa.boot.tracer.resttemplate.RestTemplateEnhance;
import com.sofa.alipay.tracer.plugins.rest.RestTemplateTracer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RestTemplateAutoConfigurationTests}.
 *
 * @author huzijie
 * @version RestTemplateAutoConfigurationTests.java, v 0.1 2023年01月11日 10:38 AM huzijie Exp $
 */
public class RestTemplateAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(RestTemplateAutoConfiguration.class));

    @Test
    public void registerRestTemplateBeans() {
        this.contextRunner
                .run((context) -> assertThat(context)
                        .hasSingleBean(RestTemplateEnhance.class)
                        .hasSingleBean(RestTemplateBeanPostProcessor.class));
    }

    @Test
    public void noRestTemplateBeansWhenRestTemplateTracerClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(RestTemplateTracer.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(RestTemplateEnhance.class)
                        .doesNotHaveBean(RestTemplateBeanPostProcessor.class));
    }

    @Test
    public void noRestTemplateBeansWhenRestTemplateEnhanceClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(RestTemplateEnhance.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(RestTemplateEnhance.class)
                        .doesNotHaveBean(RestTemplateBeanPostProcessor.class));
    }

    @Test
    public void noRestTemplateBeansWhenRestTemplateClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(RestTemplate.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(RestTemplateEnhance.class)
                        .doesNotHaveBean(RestTemplateBeanPostProcessor.class));
    }

    @Test
    public void noRestTemplateBeansWhenPropertySetFalse() {
        this.contextRunner
                .withPropertyValues("sofa.boot.tracer.resttemplate.enabled=false")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(RestTemplateEnhance.class)
                        .doesNotHaveBean(RestTemplateBeanPostProcessor.class));
    }
}

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
package com.alipay.sofa.boot.autoconfigure.tracer.springmvc;

import com.alipay.sofa.tracer.plugins.springmvc.SpringMvcSofaTracerFilter;
import com.alipay.sofa.tracer.plugins.webflux.WebfluxSofaTracerFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link OpenTracingSpringMvcAutoConfiguration}.
 *
 * @author huzijie
 * @version OpenTracingSpringMvcAutoConfigurationTests.java, v 0.1 2023年01月11日 3:57 PM huzijie Exp $
 */
@SuppressWarnings("rawtypes")
public class OpenTracingSpringMvcAutoConfigurationTests {

    @Test
    public void registerServletFilterInServletApplication() {
        WebApplicationContextRunner contextRunner = createServletContextRunner()
                .withConfiguration(AutoConfigurations
                        .of(OpenTracingSpringMvcAutoConfiguration.class));
        contextRunner.run((context) -> {
            assertThat(context).hasBean("springMvcSofaTracerFilter");
            FilterRegistrationBean bean = context.getBean("springMvcSofaTracerFilter", FilterRegistrationBean.class);
            assertThat(bean.getFilter()).isInstanceOf(SpringMvcSofaTracerFilter.class);
            assertThat(context).doesNotHaveBean(WebfluxSofaTracerFilter.class);
        });
    }

    @Test
    public void noServletFilterWhenSpringMvcSofaTracerFilterClassNotExist() {
        WebApplicationContextRunner contextRunner = createServletContextRunner()
                .withConfiguration(AutoConfigurations
                        .of(OpenTracingSpringMvcAutoConfiguration.class))
                .withClassLoader(new FilteredClassLoader(SpringMvcSofaTracerFilter.class));
        contextRunner.run((context) -> assertThat(context).doesNotHaveBean("springMvcSofaTracerFilter"));
    }

    @Test
    public void noServletFilterWhenPropertiesSetFalse() {
        WebApplicationContextRunner contextRunner = createServletContextRunner()
                .withConfiguration(AutoConfigurations
                        .of(OpenTracingSpringMvcAutoConfiguration.class))
                .withPropertyValues("sofa.boot.tracer.springmvc.enabled=false");
        contextRunner.run((context) -> assertThat(context).doesNotHaveBean("springMvcSofaTracerFilter"));
    }

    @Test
    public void customSpringMvcSofaTracerFilterProperties() {
        WebApplicationContextRunner contextRunner = createServletContextRunner()
                .withConfiguration(AutoConfigurations
                        .of(OpenTracingSpringMvcAutoConfiguration.class))
                .withPropertyValues("sofa.boot.tracer.springmvc.filterOrder=200")
                .withPropertyValues("sofa.boot.tracer.springmvc.urlPatterns=/test,/test/**");
        contextRunner.run((context) -> {
            assertThat(context).hasBean("springMvcSofaTracerFilter");
            FilterRegistrationBean bean = context.getBean("springMvcSofaTracerFilter", FilterRegistrationBean.class);
            assertThat(bean.getOrder()).isEqualTo(200);
            assertThat(bean.getUrlPatterns()).contains("/test", "/test/**");
        });
    }

    @Test
    public void registerReactorFilterInReactorApplication() {
        ReactiveWebApplicationContextRunner contextRunner = createReactorContextRunner()
                .withConfiguration(AutoConfigurations
                        .of(OpenTracingSpringMvcAutoConfiguration.class));
        contextRunner.run((context) -> {
            assertThat(context).hasSingleBean(WebfluxSofaTracerFilter.class);
            assertThat(context).doesNotHaveBean("springMvcSofaTracerFilter");
        });
    }

    @Test
    public void noReactorFilterWhenWebFilterClassNotExist() {
        ReactiveWebApplicationContextRunner contextRunner = createReactorContextRunner()
                .withConfiguration(AutoConfigurations
                        .of(OpenTracingSpringMvcAutoConfiguration.class))
                .withClassLoader(new FilteredClassLoader(WebFilter.class));
        contextRunner.run((context) -> {
            assertThat(context).doesNotHaveBean(WebfluxSofaTracerFilter.class);
        });
    }

    @Test
    public void noReactorFilterWhenWebfluxSofaTracerFilterClassNotExist() {
        ReactiveWebApplicationContextRunner contextRunner = createReactorContextRunner()
                .withConfiguration(AutoConfigurations
                        .of(OpenTracingSpringMvcAutoConfiguration.class))
                .withClassLoader(new FilteredClassLoader(WebfluxSofaTracerFilter.class));
        contextRunner.run((context) -> {
            assertThat(context).doesNotHaveBean(WebfluxSofaTracerFilter.class);
        });
    }

    @Test
    public void noReactorFilterWhenPropertiesSetFalse() {
        ReactiveWebApplicationContextRunner contextRunner = createReactorContextRunner()
                .withConfiguration(AutoConfigurations
                        .of(OpenTracingSpringMvcAutoConfiguration.class))
                .withPropertyValues("sofa.boot.tracer.springmvc.enabled=false");
        contextRunner.run((context) -> {
            assertThat(context).doesNotHaveBean(WebfluxSofaTracerFilter.class);
        });
    }

    private WebApplicationContextRunner createServletContextRunner() {
        return new WebApplicationContextRunner(AnnotationConfigServletWebServerApplicationContext::new)
                .withUserConfiguration(ServletConfiguration.class)
                .withConfiguration(AutoConfigurations.of(DispatcherServletAutoConfiguration.class,
                        HttpMessageConvertersAutoConfiguration.class, WebMvcAutoConfiguration.class));
    }

    private ReactiveWebApplicationContextRunner createReactorContextRunner() {
        return new ReactiveWebApplicationContextRunner(AnnotationConfigReactiveWebServerApplicationContext::new)
                .withConfiguration(AutoConfigurations.of(WebFluxAutoConfiguration.class,
                        HttpHandlerAutoConfiguration.class, ReactiveWebServerFactoryAutoConfiguration.class))
                .withPropertyValues("server.port=0");
    }

    @Configuration(proxyBeanMethods = false)
    static class ServletConfiguration {

        @Bean
        TomcatServletWebServerFactory tomcat() {
            return new TomcatServletWebServerFactory(0);
        }

    }
}

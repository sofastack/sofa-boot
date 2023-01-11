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

import com.alipay.sofa.boot.autoconfigure.tracer.SofaTracerAutoConfiguration;
import com.alipay.sofa.tracer.plugins.springmvc.SpringMvcSofaTracerFilter;
import com.alipay.sofa.tracer.plugins.webflux.WebfluxSofaTracerFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.WebFilter;

import java.util.List;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for spring mvc.
 *
 * @author yangguanchao
 * @author huzijie
 * @since 2018/05/01
 */
@AutoConfiguration(after = SofaTracerAutoConfiguration.class)
@EnableConfigurationProperties({ OpenTracingSpringMvcProperties.class })
@ConditionalOnProperty(name = "sofa.boot.tracer.springmvc.enabled", havingValue = "true", matchIfMissing = true)
public class OpenTracingSpringMvcAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(SpringMvcSofaTracerFilter.class)
    static class SpringMvcDelegatingFilterProxyConfiguration {

        @Bean
        public FilterRegistrationBean<SpringMvcSofaTracerFilter> springMvcSofaTracerFilter(OpenTracingSpringMvcProperties openTracingSpringMvcProperties) {
            FilterRegistrationBean<SpringMvcSofaTracerFilter> filterRegistrationBean = new FilterRegistrationBean<>();
            SpringMvcSofaTracerFilter filter = new SpringMvcSofaTracerFilter();
            filterRegistrationBean.setFilter(filter);
            List<String> urlPatterns = openTracingSpringMvcProperties.getUrlPatterns();
            if (urlPatterns == null || urlPatterns.size() <= 0) {
                filterRegistrationBean.addUrlPatterns("/*");
            } else {
                filterRegistrationBean.setUrlPatterns(urlPatterns);
            }
            filterRegistrationBean.setName(filter.getFilterName());
            filterRegistrationBean.setAsyncSupported(true);
            filterRegistrationBean.setOrder(openTracingSpringMvcProperties.getFilterOrder());
            return filterRegistrationBean;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @ConditionalOnClass({ WebFilter.class, WebfluxSofaTracerFilter.class })
    static class WebfluxSofaTracerFilterConfiguration {

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE + 10)
        public WebFilter webfluxSofaTracerFilter() {
            return new WebfluxSofaTracerFilter();
        }
    }
}

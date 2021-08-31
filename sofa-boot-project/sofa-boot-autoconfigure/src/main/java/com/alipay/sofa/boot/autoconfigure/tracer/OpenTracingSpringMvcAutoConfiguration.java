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

import com.alipay.sofa.tracer.boot.properties.SofaTracerProperties;
import com.alipay.sofa.tracer.boot.springmvc.properties.OpenTracingSpringMvcProperties;
import com.alipay.sofa.tracer.plugins.springmvc.SpringMvcSofaTracerFilter;
import com.alipay.sofa.tracer.plugins.webflux.WebfluxSofaTracerFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
 * OpenTracingSpringMvcAutoConfiguration
 *
 * @author yangguanchao
 * @since 2018/05/01
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ OpenTracingSpringMvcProperties.class, SofaTracerProperties.class })
@ConditionalOnProperty(prefix = "com.alipay.sofa.tracer.springmvc", value = "enable", matchIfMissing = true)
@AutoConfigureAfter(SofaTracerAutoConfiguration.class)
@ConditionalOnClass(SofaTracerProperties.class)
public class OpenTracingSpringMvcAutoConfiguration {

    @Autowired
    private OpenTracingSpringMvcProperties openTracingSpringProperties;

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(SpringMvcSofaTracerFilter.class)
    public class SpringMvcDelegatingFilterProxyConfiguration {
        @Bean
        public FilterRegistrationBean springMvcDelegatingFilterProxy() {
            FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
            SpringMvcSofaTracerFilter filter = new SpringMvcSofaTracerFilter();
            filterRegistrationBean.setFilter(filter);
            List<String> urlPatterns = openTracingSpringProperties.getUrlPatterns();
            if (urlPatterns == null || urlPatterns.size() <= 0) {
                filterRegistrationBean.addUrlPatterns("/*");
            } else {
                filterRegistrationBean.setUrlPatterns(urlPatterns);
            }
            filterRegistrationBean.setName(filter.getFilterName());
            filterRegistrationBean.setAsyncSupported(true);
            filterRegistrationBean.setOrder(openTracingSpringProperties.getFilterOrder());
            return filterRegistrationBean;
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @ConditionalOnClass(WebfluxSofaTracerFilter.class)
    public class WebfluxSofaTracerFilterConfiguration {
        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE + 10)
        public WebFilter webfluxSofaTracerFilter() {
            return new WebfluxSofaTracerFilter();
        }
    }
}

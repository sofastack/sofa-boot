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
package com.alipay.sofa.boot.autoconfigure.problem;

import com.alipay.sofa.rpc.core.exception.RpcErrorType;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MVC tests for {@link SofaProblemDetailAutoConfiguration}.
 *
 * @author OpenAI
 */
public class SofaProblemDetailMvcAutoConfigurationTests {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner(
        AnnotationConfigServletWebServerApplicationContext::new)
                                                                   .withUserConfiguration(
                                                                       ServletConfiguration.class,
                                                                       ProblemController.class)
                                                                   .withConfiguration(AutoConfigurations
                                                                       .of(DispatcherServletAutoConfiguration.class,
                                                                           HttpMessageConvertersAutoConfiguration.class,
                                                                           WebMvcAutoConfiguration.class,
                                                                           SofaProblemDetailAutoConfiguration.class));

    @Test
    void mvcHandlerShouldReturnProblemDetailResponse() {
        this.contextRunner.withPropertyValues("spring.application.name=problem-app")
            .run(context -> {
                mockMvc(context).perform(get("/rpc-timeout")).andExpect(status().isGatewayTimeout())
                    .andExpect(content().contentType("application/problem+json"))
                    .andExpect(jsonPath("$.type")
                        .value("https://sofastack.io/errors/sofa-rpc/client-timeout"))
                    .andExpect(jsonPath("$.title").value("RPC request timed out"))
                    .andExpect(jsonPath("$.detail").value("provider timeout"))
                    .andExpect(jsonPath("$.instance").value("/rpc-timeout"))
                    .andExpect(jsonPath("$.errorCode").value("client-timeout"))
                    .andExpect(jsonPath("$.errorType").value(RpcErrorType.CLIENT_TIMEOUT))
                    .andExpect(jsonPath("$.service").value("problem-app"));
            });
    }

    @Test
    void mvcHandlerShouldSupportI18nMessages() {
        this.contextRunner
            .withUserConfiguration(ChineseProblemDetailMessageSourceConfiguration.class)
            .run(context -> {
                mockMvc(context).perform(get("/rpc-timeout").header("Accept-Language", "zh-CN"))
                    .andExpect(status().isGatewayTimeout())
                    .andExpect(jsonPath("$.title").value("RPC 请求超时"))
                    .andExpect(jsonPath("$.detail").value("远程调用超时: provider timeout"));
            });
    }

    @Test
    void customAdviceShouldWinBeforeDefaultProblemDetailHandler() {
        this.contextRunner.withUserConfiguration(CustomRpcExceptionHandlerConfiguration.class)
            .run(context -> {
                mockMvc(context).perform(get("/rpc-timeout")).andExpect(status().isIAmATeapot())
                    .andExpect(jsonPath("$.message").value("custom-handler"));
            });
    }

    @Test
    void stackTraceShouldBeExposedWhenConfigured() {
        this.contextRunner
            .withPropertyValues("sofa.boot.problem-detail.include-stack-trace=true")
            .run(context -> {
                mockMvc(context).perform(get("/rpc-timeout"))
                    .andExpect(status().isGatewayTimeout())
                    .andExpect(jsonPath("$.stackTrace").exists());
            });
    }

    private MockMvc mockMvc(WebApplicationContext context) {
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Configuration(proxyBeanMethods = false)
    static class ServletConfiguration {

        @Bean
        TomcatServletWebServerFactory tomcat() {
            return new TomcatServletWebServerFactory(0);
        }
    }

    @RestController
    static class ProblemController {

        @GetMapping("/rpc-timeout")
        String rpcTimeout() {
            throw new SofaRpcException(RpcErrorType.CLIENT_TIMEOUT, "provider timeout");
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class ChineseProblemDetailMessageSourceConfiguration {

        @Bean
        MessageSource messageSource() {
            StaticMessageSource messageSource = new StaticMessageSource();
            messageSource.addMessage(
                "sofa.boot.problem-detail.title.sofa-rpc.client-timeout",
                java.util.Locale.SIMPLIFIED_CHINESE, "RPC 请求超时");
            messageSource.addMessage(
                "sofa.boot.problem-detail.detail.sofa-rpc.client-timeout",
                java.util.Locale.SIMPLIFIED_CHINESE, "远程调用超时: {0}");
            return messageSource;
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomRpcExceptionHandlerConfiguration {

        @RestControllerAdvice
        @Order(Ordered.HIGHEST_PRECEDENCE)
        static class CustomRpcExceptionHandler {

            @ExceptionHandler(SofaRpcException.class)
            org.springframework.http.ResponseEntity<java.util.Map<String, Object>> handle(
                                                                                             SofaRpcException exception) {
                java.util.Map<String, Object> body = new java.util.LinkedHashMap<String, Object>();
                body.put("message", "custom-handler");
                return org.springframework.http.ResponseEntity.status(418).body(body);
            }
        }
    }
}

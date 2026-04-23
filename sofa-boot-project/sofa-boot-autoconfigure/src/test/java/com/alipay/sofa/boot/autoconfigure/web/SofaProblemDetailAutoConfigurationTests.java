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
package com.alipay.sofa.boot.autoconfigure.web;

import com.alipay.sofa.rpc.boot.common.SofaBootRpcRuntimeException;
import com.alipay.sofa.rpc.core.exception.RpcErrorType;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SofaProblemDetailAutoConfigurationTests {

    private final WebApplicationContextRunner servletContextRunner = new WebApplicationContextRunner(
        AnnotationConfigServletWebServerApplicationContext::new)
            .withUserConfiguration(ServletConfiguration.class, TestController.class)
            .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class,
                HttpMessageConvertersAutoConfiguration.class,
                DispatcherServletAutoConfiguration.class, WebMvcAutoConfiguration.class,
                SofaProblemDetailAutoConfiguration.class))
            .withPropertyValues("server.port=0", "spring.application.name=demo-service");

    @Test
    void registersExceptionHandlerByDefault() {
        this.servletContextRunner.run(context -> {
            assertThat(context).hasBean("sofaProblemDetailExceptionHandler");
            assertThat(context).hasSingleBean(SofaProblemDetailProperties.class);
            assertThat(context).hasSingleBean(SofaRuntimeProblemDetailExceptionHandler.class);
            assertThat(context).hasSingleBean(SofaRpcProblemDetailExceptionHandler.class);
            assertThat(context.getBean("sofaProblemDetailExceptionHandler"))
                .isExactlyInstanceOf(SofaProblemDetailExceptionHandler.class);

            SofaProblemDetailProperties properties = context.getBean(SofaProblemDetailProperties.class);
            assertThat(properties.isEnabled()).isTrue();
            assertThat(properties.getDefaultType()).isEqualTo(URI.create("about:blank"));
            assertThat(properties.isIncludeStackTrace()).isFalse();
            assertThat(properties.isIncludeServiceInfo()).isTrue();
        });
    }

    @Test
    void backsOffWhenUserProvidesExceptionHandler() {
        this.servletContextRunner.withUserConfiguration(CustomHandlerConfiguration.class).run(context -> {
            assertThat(context).doesNotHaveBean("sofaProblemDetailExceptionHandler");
            assertThat(context).hasSingleBean(CustomResponseEntityExceptionHandler.class);
            assertThat(context).hasSingleBean(SofaRuntimeProblemDetailExceptionHandler.class);
            assertThat(context).hasSingleBean(SofaRpcProblemDetailExceptionHandler.class);
        });
    }

    @Test
    void doesNotRegisterWhenDisabled() {
        this.servletContextRunner.withPropertyValues("sofa.web.problem-detail.enabled=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean(SofaProblemDetailExceptionHandler.class);
                assertThat(context).doesNotHaveBean(SofaRuntimeProblemDetailExceptionHandler.class);
                assertThat(context).doesNotHaveBean(SofaRpcProblemDetailExceptionHandler.class);
            });
    }

    @Test
    void bindsCustomProperties() {
        this.servletContextRunner.withPropertyValues(
            "sofa.web.problem-detail.default-type=https://sofastack.io/errors/default",
            "sofa.web.problem-detail.include-stack-trace=true",
            "sofa.web.problem-detail.include-service-info=false").run(context -> {
                SofaProblemDetailProperties properties = context
                    .getBean(SofaProblemDetailProperties.class);
                assertThat(properties.getDefaultType())
                    .isEqualTo(URI.create("https://sofastack.io/errors/default"));
                assertThat(properties.isIncludeStackTrace()).isTrue();
                assertThat(properties.isIncludeServiceInfo()).isFalse();
            });
    }

    @Test
    void doesNotRegisterInReactiveApplication() {
        new ReactiveWebApplicationContextRunner(
            AnnotationConfigReactiveWebServerApplicationContext::new)
                .withConfiguration(AutoConfigurations.of(WebFluxAutoConfiguration.class,
                    HttpHandlerAutoConfiguration.class,
                    ReactiveWebServerFactoryAutoConfiguration.class,
                    SofaProblemDetailAutoConfiguration.class))
                .run(context -> assertThat(context)
                    .doesNotHaveBean(SofaProblemDetailExceptionHandler.class));
    }

    @Test
    void rendersRuntimeExceptionsAsProblemDetails() throws Exception {
        this.servletContextRunner.run(context -> {
            mockMvc(context).perform(get("/problem-detail/runtime"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type")
                    .value("https://sofastack.io/errors/runtime-exception"))
                .andExpect(jsonPath("$.title").value("SOFA Runtime Error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail")
                    .value("SOFA-BOOT-01-00000: runtime failed"))
                .andExpect(jsonPath("$.instance").value("/problem-detail/runtime"))
                .andExpect(jsonPath("$.service").value("demo-service"))
                .andExpect(jsonPath("$.errorCode").value("SOFA-BOOT-01-00000"));
        });
    }

    @Test
    void rendersRpcExceptionsAsProblemDetails() throws Exception {
        this.servletContextRunner.run(context -> {
            mockMvc(context).perform(get("/problem-detail/rpc"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type")
                    .value("https://sofastack.io/errors/rpc-configuration-exception"))
                .andExpect(jsonPath("$.title").value("SOFA RPC Configuration Error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("filter name[testFilter] is not ref a Filter."))
                .andExpect(jsonPath("$.instance").value("/problem-detail/rpc"))
                .andExpect(jsonPath("$.service").value("demo-service"));
        });
    }

    @Test
    void rendersRemoteRpcAvailabilityFailuresAs503() throws Exception {
        this.servletContextRunner.run(context -> {
            mockMvc(context).perform(get("/problem-detail/rpc-unavailable"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://sofastack.io/errors/rpc-exception"))
                .andExpect(jsonPath("$.title").value("SOFA RPC Error"))
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.detail").value("remote rpc failed"))
                .andExpect(jsonPath("$.instance").value("/problem-detail/rpc-unavailable"))
                .andExpect(jsonPath("$.service").value("demo-service"));
        });
    }

    @Test
    void appliesDefaultTypeToFrameworkExceptions() throws Exception {
        this.servletContextRunner
            .withPropertyValues("sofa.web.problem-detail.default-type=https://sofastack.io/errors/default")
            .run(context -> {
                mockMvc(context).perform(get("/problem-detail/mvc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.type").value("https://sofastack.io/errors/default"))
                    .andExpect(jsonPath("$.title").value("Bad Request"))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.detail")
                        .value("Required parameter 'name' is not present."))
                    .andExpect(jsonPath("$.instance").value("/problem-detail/mvc"))
                    .andExpect(jsonPath("$.service").value("demo-service"));
            });
    }

    @Test
    void keepsResponseStatusExceptionsUntouched() throws Exception {
        this.servletContextRunner.run(context -> {
            mockMvc(context).perform(get("/problem-detail/response-status"))
                .andExpect(status().isIAmATeapot())
                .andExpect(result -> {
                    assertThat(result.getResolvedException()).isInstanceOf(TeapotException.class);
                    assertThat(result.getResponse().getContentType())
                        .isNotEqualTo(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
                });
        });
    }

    @Test
    void allowsUserControllerAdviceToOverrideSofaHandlers() throws Exception {
        this.servletContextRunner.withUserConfiguration(UserControllerAdvice.class).run(context -> {
            mockMvc(context).perform(get("/problem-detail/runtime"))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("https://example.com/custom"))
                .andExpect(jsonPath("$.title").value("Custom Runtime Error"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("handled by user advice"))
                .andExpect(jsonPath("$.service").doesNotExist());
        });
    }

    private MockMvc mockMvc(ConfigurableWebApplicationContext context) {
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Configuration(proxyBeanMethods = false)
    static class ServletConfiguration {

        @Bean
        TomcatServletWebServerFactory tomcat() {
            return new TomcatServletWebServerFactory(0);
        }

    }

    @Configuration(proxyBeanMethods = false)
    static class CustomHandlerConfiguration {

        @Bean
        CustomResponseEntityExceptionHandler customProblemDetailExceptionHandler() {
            return new CustomResponseEntityExceptionHandler();
        }
    }

    @RestControllerAdvice
    static class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    }

    @RestControllerAdvice
    @Order(Ordered.HIGHEST_PRECEDENCE)
    static class UserControllerAdvice {

        @ExceptionHandler(ServiceRuntimeException.class)
        ProblemDetail handleServiceRuntimeException(ServiceRuntimeException ex) {
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "handled by user advice");
            problemDetail.setType(URI.create("https://example.com/custom"));
            problemDetail.setTitle("Custom Runtime Error");
            return problemDetail;
        }
    }

    @RestController
    static class TestController {

        @GetMapping("/problem-detail/runtime")
        ResponseEntity<Void> runtime() {
            throw new ServiceRuntimeException("SOFA-BOOT-01-00000: runtime failed");
        }

        @GetMapping("/problem-detail/rpc")
        ResponseEntity<Void> rpc() {
            throw new SofaBootRpcRuntimeException("filter name[testFilter] is not ref a Filter.");
        }

        @GetMapping("/problem-detail/rpc-unavailable")
        ResponseEntity<Void> rpcUnavailable() {
            throw new SofaBootRpcRuntimeException("remote rpc failed", new SofaRpcException(
                RpcErrorType.CLIENT_TIMEOUT, "request timeout"));
        }

        @GetMapping("/problem-detail/mvc")
        ResponseEntity<Void> mvc(@RequestParam("name") String name) {
            return ResponseEntity.ok().build();
        }

        @GetMapping("/problem-detail/response-status")
        ResponseEntity<Void> responseStatus() {
            throw new TeapotException();
        }
    }

    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    static class TeapotException extends RuntimeException {
    }
}

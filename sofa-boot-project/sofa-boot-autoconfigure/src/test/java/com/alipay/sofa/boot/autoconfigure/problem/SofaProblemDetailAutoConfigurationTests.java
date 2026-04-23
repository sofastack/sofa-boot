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
import com.alipay.sofa.rpc.config.JAXRSProviderManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import javax.ws.rs.core.Response;
import java.util.LinkedHashSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaProblemDetailAutoConfiguration}.
 *
 * @author OpenAI
 */
public class SofaProblemDetailAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(SofaProblemDetailAutoConfiguration.class));

    @AfterEach
    void clearJaxrsProviders() {
        for (Object provider : new LinkedHashSet<Object>(
            JAXRSProviderManager.getCustomProviderInstances())) {
            JAXRSProviderManager.removeCustomProviderInstance(provider);
        }
    }

    @Test
    void defaultBeansCreatedAndJaxrsProvidersRegistered() {
        this.contextRunner.withPropertyValues("spring.application.name=problem-app").run(context -> {
            assertThat(context).hasSingleBean(SofaProblemDetailFactory.class);
            assertThat(context).hasSingleBean(SofaRpcExceptionProblemDetailExceptionMapper.class);
            assertThat(context)
                .hasSingleBean(SofaRpcRuntimeExceptionProblemDetailExceptionMapper.class);
            assertThat(context)
                .hasSingleBean(SofaBootRpcRuntimeExceptionProblemDetailExceptionMapper.class);
            assertThat(JAXRSProviderManager.getCustomProviderInstances()).hasSize(3);
        });
    }

    @Test
    void disabledConfigurationShouldBackOff() {
        this.contextRunner.withPropertyValues("sofa.boot.problem-detail.enabled=false")
            .run(context -> {
                assertThat(context).doesNotHaveBean(SofaProblemDetailFactory.class);
                assertThat(context)
                    .doesNotHaveBean(SofaRpcExceptionProblemDetailExceptionMapper.class);
                assertThat(JAXRSProviderManager.getCustomProviderInstances()).isEmpty();
            });
    }

    @SuppressWarnings("unchecked")
    @Test
    void jaxrsMapperShouldRenderProblemJsonShape() {
        this.contextRunner.withPropertyValues("spring.application.name=problem-app").run(context -> {
            SofaRpcExceptionProblemDetailExceptionMapper mapper = context
                .getBean(SofaRpcExceptionProblemDetailExceptionMapper.class);
            Response response = mapper.toResponse(new SofaRpcException(RpcErrorType.CLIENT_TIMEOUT,
                "provider timeout"));

            assertThat(response.getStatus()).isEqualTo(504);
            assertThat(response.getMediaType().toString()).isEqualTo("application/problem+json");

            Map<String, Object> entity = (Map<String, Object>) response.getEntity();
            assertThat(entity.get("type"))
                .isEqualTo("https://sofastack.io/errors/sofa-rpc/client-timeout");
            assertThat(entity.get("title")).isEqualTo("RPC request timed out");
            assertThat(entity.get("detail")).isEqualTo("provider timeout");
            assertThat(entity.get("errorCode")).isEqualTo("client-timeout");
            assertThat(entity.get("errorType")).isEqualTo(RpcErrorType.CLIENT_TIMEOUT);
            assertThat(entity.get("service")).isEqualTo("problem-app");
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    void stackTraceCanBeIncludedWhenEnabled() {
        this.contextRunner
            .withPropertyValues("sofa.boot.problem-detail.include-stack-trace=true")
            .run(context -> {
                SofaRpcExceptionProblemDetailExceptionMapper mapper = context
                    .getBean(SofaRpcExceptionProblemDetailExceptionMapper.class);
                Response response = mapper.toResponse(new SofaRpcException(
                    RpcErrorType.CLIENT_UNDECLARED_ERROR, "unexpected"));

                Map<String, Object> entity = (Map<String, Object>) response.getEntity();
                assertThat(entity).containsKey("stackTrace");
                assertThat(entity.get("stackTrace").toString()).contains("SofaRpcException");
            });
    }
}

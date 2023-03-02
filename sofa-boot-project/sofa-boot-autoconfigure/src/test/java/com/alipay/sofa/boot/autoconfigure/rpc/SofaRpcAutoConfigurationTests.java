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
package com.alipay.sofa.boot.autoconfigure.rpc;

import com.alipay.sofa.boot.autoconfigure.condition.ConditionalOnSwitch;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.rpc.boot.config.RegistryConfigureProcessor;
import com.alipay.sofa.rpc.boot.container.ProviderConfigContainer;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ConsumerMockProcessor;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.DynamicConfigProcessor;
import com.alipay.sofa.rpc.boot.swagger.BoltSwaggerServiceApplicationListener;
import com.alipay.sofa.rpc.boot.swagger.SwaggerServiceApplicationListener;
import com.alipay.sofa.rpc.config.JAXRSProviderManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import io.swagger.models.Swagger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import java.util.Map;

import static com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_SOFA;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaRpcAutoConfiguration}.
 *
 * @author yuanxuan
 * @version : SofaRpcAutoConfigurationTests.java, v 0.1 202 15:42 yuanxuan Exp $
 */
public class SofaRpcAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(SofaRpcAutoConfiguration.class,
                                                                     SofaRuntimeAutoConfiguration.class));

    @Test
    void defaultProviderConfigContainer() {
        this.contextRunner.run(context -> {
            assertThat(context).hasBean("providerConfigContainer");
        });
    }

    @Test
    void providerConfigContainerBackOff() {
        this.contextRunner.withUserConfiguration(CustomProviderConfigContainerConfiguration.class).run(context -> {
            assertThat(context).doesNotHaveBean("providerConfigContainer");
            assertThat(context.getBean(ProviderConfigContainer.class).isAllowPublish()).isTrue();
        });
    }

    @Test
    void registryConfigureProcessors() {

        this.contextRunner.run(context -> {
            assertThat(context).hasBean("registryConfigMap");
            Map<String, RegistryConfigureProcessor> registryConfigureProcessorMap
                    = (Map<String, RegistryConfigureProcessor>) context.getBean("registryConfigMap");
            assertThat(registryConfigureProcessorMap).isNotNull();
            assertThat(registryConfigureProcessorMap.get(REGISTRY_PROTOCOL_SOFA)).isNotNull();
        });

    }

    @Test
    void consumerMockProcessorBeanCreatedWhenConfigured() {
        this.contextRunner.withPropertyValues("sofa.boot.rpc.mock-url=rpc-mocked-url").run(context -> {
            assertThat(context).hasSingleBean(ConsumerMockProcessor.class);
            assertThat(context.getBean(ConsumerMockProcessor.class).getMockUrl()).isEqualTo("rpc-mocked-url");
        });
    }

    @Test
    void dynamicConfigProcessorBeanCreatedWhenConfigured() {
        this.contextRunner.withPropertyValues("sofa.boot.rpc.dynamic-config=apollo").run(context -> {
            assertThat(context).hasSingleBean(DynamicConfigProcessor.class);
            assertThat(context.getBean(DynamicConfigProcessor.class).getDynamicConfig()).isEqualTo("apollo");
        });
    }

    @Test
    void swaggerServiceApplicationListenerBeanCreatedWhenEnable() {
        this.contextRunner.withConfiguration(AutoConfigurations
                .of(SofaRuntimeAutoConfiguration.class)).withPropertyValues("sofa.boot.rpc.rest-swagger=true").run(context -> {
            assertThat(context).hasSingleBean(SwaggerServiceApplicationListener.class);
        });
    }

    @Test
    void boltSwaggerServiceApplicationListenerBeanCreatedWhenEnable() {
        this.contextRunner.withConfiguration(AutoConfigurations
                .of(SofaRuntimeAutoConfiguration.class)).withPropertyValues("sofa.boot.rpc.enable-swagger=true").run(context -> {
            assertThat(context).hasSingleBean(BoltSwaggerServiceApplicationListener.class);
        });
    }

    @Test
    void boltSwaggerBeanNotCreatedWhenSwitchMatchMissing() {
        this.contextRunner.withConfiguration(AutoConfigurations
                .of(SofaRuntimeAutoConfiguration.class)).withConfiguration(AutoConfigurations
                .of(SwaggerTestConfiguration.class)).run(context -> {
            assertThat(context).hasSingleBean(BoltSwaggerServiceApplicationListener.class);
            assertThat(context).hasSingleBean(SwaggerServiceApplicationListener.class);
        });
    }

    @Test
    void boltSwaggerBeanNotCreatedWhenSwitchMatchDisable() {
        this.contextRunner.withConfiguration(AutoConfigurations
                .of(SofaRuntimeAutoConfiguration.class)).withConfiguration(AutoConfigurations
                .of(SwaggerTestConfiguration.class)).withPropertyValues("sofa.boot.switch.bean.boltSwaggerListener.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(BoltSwaggerServiceApplicationListener.class);
            assertThat(context).hasSingleBean(SwaggerServiceApplicationListener.class);
        });
    }

    @Test
    void boltSwaggerBeanNotCreatedWhenSwitchConfigurationDisable() {
        this.contextRunner.withConfiguration(AutoConfigurations
                .of(SofaRuntimeAutoConfiguration.class)).withConfiguration(AutoConfigurations
                .of(SwaggerTestConfiguration.class)).withPropertyValues("sofa.boot.switch.bean.testSwagger.enabled=false").run(context -> {
            assertThat(context).doesNotHaveBean(BoltSwaggerServiceApplicationListener.class);
            assertThat(context).doesNotHaveBean(SwaggerServiceApplicationListener.class);
        });
    }

    @Test
    void customRestFilters() {
        this.contextRunner.withUserConfiguration(RestFilterTestConfiguration.class).run(context -> {
            //Object object = context.getBean("clientResponseFilters");
            assertThat(JAXRSProviderManager.getCustomProviderInstances().size()).isEqualTo(4);

        });
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomProviderConfigContainerConfiguration {
        @Bean
        public ProviderConfigContainer customProviderConfigContainer() {
            ProviderConfigContainer providerConfigContainer = new ProviderConfigContainer();
            providerConfigContainer.setAllowPublish(true);
            return providerConfigContainer;
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class RestFilterTestConfiguration {

        @Bean
        ContainerRequestFilter customContainerRequestFilter() {
            return requestContext -> {

            };
        }

        @Bean
        ContainerResponseFilter customContainerResponseFilter() {
            return (requestContext, responseContext) -> {

            };
        }

        @Bean
        ClientRequestFilter customClientRequestFilter() {
            return requestContext -> {
            };
        }

        @Bean
        ClientResponseFilter customClientResponseFilter() {
            return (requestContext, responseContext) -> {
            };
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(Swagger.class)
    @ConditionalOnSwitch(value = "testSwagger")
    static class SwaggerTestConfiguration {

        @Bean
        @ConditionalOnSwitch
        public ApplicationListener<ApplicationStartedEvent> swaggerServiceListener(SofaRuntimeManager sofaRuntimeManager) {
            return new SwaggerServiceApplicationListener(sofaRuntimeManager);
        }

        @Bean
        @ConditionalOnSwitch
        public ApplicationListener<ApplicationStartedEvent> boltSwaggerListener(SofaRuntimeManager sofaRuntimeManager) {
            return new BoltSwaggerServiceApplicationListener(sofaRuntimeManager);
        }
    }

}

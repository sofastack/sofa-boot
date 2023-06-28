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
import com.alipay.sofa.rpc.boot.config.FaultToleranceConfigurator;
import com.alipay.sofa.rpc.boot.config.RegistryConfigureProcessor;
import com.alipay.sofa.rpc.boot.container.ProviderConfigContainer;
import com.alipay.sofa.rpc.boot.container.RegistryConfigContainer;
import com.alipay.sofa.rpc.boot.container.ServerConfigContainer;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ConsumerMockProcessor;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.DynamicConfigProcessor;
import com.alipay.sofa.rpc.boot.swagger.BoltSwaggerServiceApplicationListener;
import com.alipay.sofa.rpc.boot.swagger.SwaggerServiceApplicationListener;
import com.alipay.sofa.rpc.client.aft.FaultToleranceConfig;
import com.alipay.sofa.rpc.client.aft.FaultToleranceConfigManager;
import com.alipay.sofa.rpc.common.RpcConstants;
import com.alipay.sofa.rpc.config.JAXRSProviderManager;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import io.swagger.models.Swagger;
import io.swagger.v3.jaxrs2.integration.JaxrsOpenApiContextBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.test.context.FilteredClassLoader;
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
    void swaggerV2BeanCreatedWhenEnable() {
        this.contextRunner.withConfiguration(AutoConfigurations
                .of(SofaRuntimeAutoConfiguration.class)).withPropertyValues("sofa.boot.rpc.rest-swagger=true").run(context -> {
            assertThat(context).hasSingleBean(SwaggerServiceApplicationListener.class);
        });
    }

    @Test
    void swaggerV2BeanCreatedWhenEnableWithoutSwaggerV1() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(JaxrsOpenApiContextBuilder.class))
                .withConfiguration(AutoConfigurations
                .of(SofaRuntimeAutoConfiguration.class)).withPropertyValues("sofa.boot.rpc.rest-swagger=true").run(context -> {
            assertThat(context).doesNotHaveBean(SwaggerServiceApplicationListener.class);
        });
    }

    @Test
    void swaggerV1BeanCreatedWhenEnable() {
        this.contextRunner.withConfiguration(AutoConfigurations
                .of(SofaRuntimeAutoConfiguration.class)).withPropertyValues("sofa.boot.rpc.enable-swagger=true").run(context -> {
            assertThat(context).hasSingleBean(BoltSwaggerServiceApplicationListener.class);
        });
    }

    @Test
    void swaggerV1BeanCreatedWhenEnableWithoutSwaggerV2() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(Swagger.class))
                .withConfiguration(AutoConfigurations
                .of(SofaRuntimeAutoConfiguration.class)).withPropertyValues("sofa.boot.rpc.enable-swagger=true").run(context -> {
            assertThat(context).doesNotHaveBean(BoltSwaggerServiceApplicationListener.class);
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

    @Test
    void customAftConfig() {
        this.contextRunner.withPropertyValues("spring.application.name=rpc-test",
                        "sofa.boot.rpc.aftRegulationEffective=true",
                        "sofa.boot.rpc.aftDegradeEffective=true",
                        "sofa.boot.rpc.aftTimeWindow=20",
                        "sofa.boot.rpc.aftLeastWindowCount=20",
                        "sofa.boot.rpc.aftLeastWindowExceptionRateMultiple=8.0",
                        "sofa.boot.rpc.aftWeightDegradeRate=0.06",
                        "sofa.boot.rpc.aftWeightRecoverRate=3.0",
                        "sofa.boot.rpc.aftDegradeLeastWeight=2",
                        "sofa.boot.rpc.aftDegradeMaxIpCount=3").
                run(context -> {
                    context.getBean(FaultToleranceConfigurator.class).startFaultTolerance();
                    FaultToleranceConfig faultToleranceConfig = FaultToleranceConfigManager.getConfig("rpc-test");
                    assertThat(faultToleranceConfig).isNotNull();
                    assertThat(faultToleranceConfig.isRegulationEffective()).isTrue();
                    assertThat(faultToleranceConfig.isDegradeEffective()).isTrue();
                    assertThat(faultToleranceConfig.getTimeWindow()).isEqualTo(20);
                    assertThat(faultToleranceConfig.getLeastWindowCount()).isEqualTo(20);
                    assertThat(faultToleranceConfig.getLeastWindowExceptionRateMultiple()).isEqualTo(8.0);
                    assertThat(faultToleranceConfig.getWeightDegradeRate()).isEqualTo(0.06);
                    assertThat(faultToleranceConfig.getWeightRecoverRate()).isEqualTo(3.0);
                    assertThat(faultToleranceConfig.getDegradeLeastWeight()).isEqualTo(2);
                    assertThat(faultToleranceConfig.getDegradeMaxIpCount()).isEqualTo(3);
                });
    }

    @Test
    void customServerConfigContainer() {
        this.contextRunner.withPropertyValues(
                        "sofa.boot.rpc.boundHost=boundHost",
                        "sofa.boot.rpc.virtualHost=virtualHost",
                        "sofa.boot.rpc.virtualPort=10000").
                run(context -> {
                    ServerConfigContainer serverConfigContainer = context.getBean(ServerConfigContainer.class);
                    ServerConfig serverConfig = serverConfigContainer.getServerConfig("bolt");
                    assertThat(serverConfig.getBoundHost()).isEqualTo("boundHost");
                    assertThat(serverConfig.getVirtualHost()).isEqualTo("virtualHost");
                    assertThat(serverConfig.getVirtualPort()).isEqualTo(10000);
                });
    }

    @Test
    void customBoltServerConfig() {
        this.contextRunner.withPropertyValues(
                        "sofa.boot.rpc.boltPort=12201",
                        "sofa.boot.rpc.boltThreadPoolCoreSize=30",
                        "sofa.boot.rpc.boltThreadPoolMaxSize=300",
                        "sofa.boot.rpc.boltThreadPoolQueueSize=100",
                        "sofa.boot.rpc.boltAcceptsSize=50000",
                        "sofa.boot.rpc.boltProcessInIoThread=true").
                run(context -> {
                    ServerConfigContainer serverConfigContainer = context.getBean(ServerConfigContainer.class);
                    ServerConfig serverConfig = serverConfigContainer.getServerConfig("bolt");
                    assertThat(serverConfig.getPort()).isEqualTo(12201);
                    assertThat(serverConfig.getCoreThreads()).isEqualTo(30);
                    assertThat(serverConfig.getMaxThreads()).isEqualTo(300);
                    assertThat(serverConfig.getQueues()).isEqualTo(100);
                    assertThat(serverConfig.getAccepts()).isEqualTo(50000);
                    assertThat(serverConfig.getParameters().get(RpcConstants.PROCESS_IN_IOTHREAD)).isEqualTo("true");
                });
    }

    @Test
    void customH2cServerConfig() {
        this.contextRunner.withPropertyValues(
                        "sofa.boot.rpc.h2cPort=12201",
                        "sofa.boot.rpc.h2cThreadPoolCoreSize=30",
                        "sofa.boot.rpc.h2cThreadPoolMaxSize=300",
                        "sofa.boot.rpc.h2cThreadPoolQueueSize=100",
                        "sofa.boot.rpc.h2cAcceptsSize=50000").
                run(context -> {
                    ServerConfigContainer serverConfigContainer = context.getBean(ServerConfigContainer.class);
                    ServerConfig serverConfig = serverConfigContainer.getServerConfig("h2c");
                    assertThat(serverConfig.getPort()).isEqualTo(12201);
                    assertThat(serverConfig.getCoreThreads()).isEqualTo(30);
                    assertThat(serverConfig.getMaxThreads()).isEqualTo(300);
                    assertThat(serverConfig.getQueues()).isEqualTo(100);
                    assertThat(serverConfig.getAccepts()).isEqualTo(50000);
                });
    }

    @Test
    void customRestServerConfig() {
        this.contextRunner.withPropertyValues(
                        "sofa.boot.rpc.restHostname=localhost",
                        "sofa.boot.rpc.restPort=12201",
                        "sofa.boot.rpc.restIoThreadSize=5",
                        "sofa.boot.rpc.restContextPath=/",
                        "sofa.boot.rpc.restAllowedOrigins=true",
                        "sofa.boot.rpc.restThreadPoolMaxSize=300",
                        "sofa.boot.rpc.restMaxRequestSize=10485761",
                        "sofa.boot.rpc.restTelnet=false",
                        "sofa.boot.rpc.restDaemon=false").
                run(context -> {
                    ServerConfigContainer serverConfigContainer = context.getBean(ServerConfigContainer.class);
                    ServerConfig serverConfig = serverConfigContainer.getServerConfig("rest");
                    assertThat(serverConfig.getBoundHost()).isEqualTo("localhost");
                    assertThat(serverConfig.getPort()).isEqualTo(12201);
                    assertThat(serverConfig.getIoThreads()).isEqualTo(5);
                    assertThat(serverConfig.getContextPath()).isEqualTo("/");
                    assertThat(serverConfig.getParameters().get(RpcConstants.ALLOWED_ORIGINS)).isEqualTo("true");
                    assertThat(serverConfig.getMaxThreads()).isEqualTo(300);
                    assertThat(serverConfig.getPayload()).isEqualTo(10485761);
                    assertThat(serverConfig.isTelnet()).isEqualTo(false);
                    assertThat(serverConfig.isDaemon()).isEqualTo(false);
                });
    }

    @Test
    void customDubboServerConfig() {
        this.contextRunner.withPropertyValues(
                        "sofa.boot.rpc.dubboPort=12201",
                        "sofa.boot.rpc.dubboIoThreadSize=1",
                        "sofa.boot.rpc.dubboThreadPoolMaxSize=300",
                        "sofa.boot.rpc.dubboAcceptsSize=50000").
                run(context -> {
                    ServerConfigContainer serverConfigContainer = context.getBean(ServerConfigContainer.class);
                    ServerConfig serverConfig = serverConfigContainer.getServerConfig("dubbo");
                    assertThat(serverConfig.getPort()).isEqualTo(12201);
                    assertThat(serverConfig.getIoThreads()).isEqualTo(1);
                    assertThat(serverConfig.getMaxThreads()).isEqualTo(300);
                    assertThat(serverConfig.getAccepts()).isEqualTo(50000);
                });
    }

    @Test
    void customHttpServerConfig() {
        this.contextRunner.withPropertyValues(
                        "sofa.boot.rpc.httpPort=12201",
                        "sofa.boot.rpc.httpThreadPoolCoreSize=30",
                        "sofa.boot.rpc.httpThreadPoolMaxSize=300",
                        "sofa.boot.rpc.httpThreadPoolQueueSize=100",
                        "sofa.boot.rpc.httpAcceptsSize=50000").
                run(context -> {
                    ServerConfigContainer serverConfigContainer = context.getBean(ServerConfigContainer.class);
                    ServerConfig serverConfig = serverConfigContainer.getServerConfig("http");
                    assertThat(serverConfig.getPort()).isEqualTo(12201);
                    assertThat(serverConfig.getCoreThreads()).isEqualTo(30);
                    assertThat(serverConfig.getMaxThreads()).isEqualTo(300);
                    assertThat(serverConfig.getQueues()).isEqualTo(100);
                    assertThat(serverConfig.getAccepts()).isEqualTo(50000);
                });
    }

    @Test
    void customTripleServerConfig() {
        this.contextRunner.withPropertyValues(
                        "sofa.boot.rpc.triplePort=12201",
                        "sofa.boot.rpc.tripleThreadPoolCoreSize=30",
                        "sofa.boot.rpc.tripleThreadPoolMaxSize=300",
                        "sofa.boot.rpc.tripleThreadPoolQueueSize=100",
                        "sofa.boot.rpc.tripleAcceptsSize=50000").
                run(context -> {
                    ServerConfigContainer serverConfigContainer = context.getBean(ServerConfigContainer.class);
                    ServerConfig serverConfig = serverConfigContainer.getServerConfig("tri");
                    assertThat(serverConfig.getPort()).isEqualTo(12201);
                    assertThat(serverConfig.getCoreThreads()).isEqualTo(30);
                    assertThat(serverConfig.getMaxThreads()).isEqualTo(300);
                    assertThat(serverConfig.getQueues()).isEqualTo(100);
                    assertThat(serverConfig.getAccepts()).isEqualTo(50000);
                });
    }

    @Test
    void customRegistryConfigContainer() {
        this.contextRunner.withPropertyValues(
                        "sofa.boot.rpc.registryAddress=local://127.0.0.1:2883",
                        "sofa.boot.rpc.registries.mesh=local://127.0.0.1:2884",
                        "sofa.boot.rpc.enableMesh=all").
                run(context -> {
                    RegistryConfigContainer registryConfigContainer = context.getBean(RegistryConfigContainer.class);
                    RegistryConfig defaultConfig = registryConfigContainer.getRegistryConfig("default");
                    assertThat(defaultConfig.getFile()).isEqualTo("127.0.0.1:2883");

                    RegistryConfig config = registryConfigContainer.getRegistryConfig("mesh");
                    assertThat(config.getFile()).isEqualTo("127.0.0.1:2884");

                    assertThat(registryConfigContainer.isMeshEnabled("local")).isTrue();
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

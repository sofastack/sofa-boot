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
package com.alipay.sofa.rpc.boot.test;

import com.alipay.sofa.healthcheck.startup.ReadinessCheckCallback;
import com.alipay.sofa.rpc.boot.config.ConsulConfigurator;
import com.alipay.sofa.rpc.boot.config.FaultToleranceConfigurator;
import com.alipay.sofa.rpc.boot.config.LocalFileConfigurator;
import com.alipay.sofa.rpc.boot.config.MeshConfigurator;
import com.alipay.sofa.rpc.boot.config.NacosConfigurator;
import com.alipay.sofa.rpc.boot.config.RegistryConfigureProcessor;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties;
import com.alipay.sofa.rpc.boot.config.SofaRegistryConfigurator;
import com.alipay.sofa.rpc.boot.config.ZookeeperConfigurator;
import com.alipay.sofa.rpc.boot.container.ConsumerConfigContainer;
import com.alipay.sofa.rpc.boot.container.ProviderConfigContainer;
import com.alipay.sofa.rpc.boot.container.RegistryConfigContainer;
import com.alipay.sofa.rpc.boot.container.ServerConfigContainer;
import com.alipay.sofa.rpc.boot.context.ApplicationContextClosedListener;
import com.alipay.sofa.rpc.boot.context.ApplicationContextRefreshedListener;
import com.alipay.sofa.rpc.boot.context.SofaBootRpcStartListener;
import com.alipay.sofa.rpc.boot.health.RpcAfterHealthCheckCallback;
import com.alipay.sofa.rpc.boot.runtime.adapter.helper.ConsumerConfigHelper;
import com.alipay.sofa.rpc.boot.runtime.adapter.helper.ProviderConfigHelper;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ConsumerConfigProcessor;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ProcessorContainer;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ProviderConfigProcessor;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ProviderRegisterProcessor;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ConsumerMockProcessor;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.DynamicConfigProcessor;
import com.alipay.sofa.rpc.boot.swagger.BoltSwaggerServiceApplicationListener;
import com.alipay.sofa.rpc.boot.swagger.SwaggerServiceApplicationListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
@Configuration(proxyBeanMethods = false)
@Import({ RuntimeTestConfiguration.class, HealthcheckTestConfiguration.class })
@EnableConfigurationProperties(SofaBootRpcProperties.class)
public class SofaRpcTestAutoConfiguration {
    @Bean
    public ProviderConfigContainer providerConfigContainer() {
        return new ProviderConfigContainer();
    }

    @Bean
    public FaultToleranceConfigurator faultToleranceConfigurator() {
        return new FaultToleranceConfigurator();
    }

    @Bean
    public ServerConfigContainer serverConfigContainer(SofaBootRpcProperties sofaBootRpcProperties) {
        return new ServerConfigContainer(sofaBootRpcProperties);
    }

    @Bean
    public RegistryConfigContainer registryConfigContainer() {
        return new RegistryConfigContainer();
    }

    @Bean
    public ConsumerConfigHelper consumerConfigHelper(SofaBootRpcProperties sofaBootRpcProperties,
                                                     @Lazy RegistryConfigContainer registryConfigContainer,
                                                     @Value("${"
                                                            + SofaBootRpcConfigConstants.APP_NAME
                                                            + "}") String appName) {
        return new ConsumerConfigHelper(sofaBootRpcProperties, registryConfigContainer, appName);
    }

    @Bean
    public ProviderConfigHelper providerConfigHelper() {
        return new ProviderConfigHelper();
    }

    @Bean
    public ZookeeperConfigurator zookeeperConfigurator() {
        return new ZookeeperConfigurator();
    }

    @Bean
    public LocalFileConfigurator localFileConfigurator() {
        return new LocalFileConfigurator();
    }

    @Bean
    public MeshConfigurator meshConfigurator() {
        return new MeshConfigurator();
    }

    @Bean
    public RegistryConfigureProcessor nacosConfigurator() {
        return new NacosConfigurator();
    }

    @Bean
    public RegistryConfigureProcessor sofaRegistryConfigurator() {
        return new SofaRegistryConfigurator();
    }

    @Bean(name = "registryConfigMap")
    public Map<String, RegistryConfigureProcessor> configureProcessorMap(List<RegistryConfigureProcessor> processorList) {
        Map<String, RegistryConfigureProcessor> map = new HashMap<String, RegistryConfigureProcessor>();
        for (RegistryConfigureProcessor processor : processorList) {
            map.put(processor.registryType(), processor);
        }
        return map;
    }

    @Bean
    public RegistryConfigureProcessor consulConfigurator() {
        return new ConsulConfigurator();
    }

    @Bean
    public ConsumerConfigContainer consumerConfigContainer() {
        return new ConsumerConfigContainer();
    }

    @Bean
    public ApplicationContextClosedListener applicationContextClosedListener(ProviderConfigContainer providerConfigContainer,
                                                                             ServerConfigContainer serverConfigContainer) {
        return new ApplicationContextClosedListener(providerConfigContainer, serverConfigContainer);
    }

    @Bean
    @ConditionalOnMissingClass({ "com.alipay.sofa.healthcheck.startup.ReadinessCheckCallback" })
    public ApplicationContextRefreshedListener applicationContextRefreshedListener() {
        return new ApplicationContextRefreshedListener();
    }

    @Bean
    public SofaBootRpcStartListener sofaBootRpcStartListener() {
        return new SofaBootRpcStartListener();
    }

    @Bean
    @ConditionalOnProperty(name = "com.alipay.sofa.rpc.rest-swagger", havingValue = "true")
    public ApplicationListener swaggerServiceApplicationListener() {
        return new SwaggerServiceApplicationListener();
    }

    @Bean
    @ConditionalOnProperty(name = "com.alipay.sofa.rpc.enable-swagger", havingValue = "true")
    public ApplicationListener boltSwaggerServiceApplicationListener() {
        return new BoltSwaggerServiceApplicationListener();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(ReadinessCheckCallback.class)
    public static class RpcAfterHealthCheckCallbackConfiguration {
        @Bean
        public RpcAfterHealthCheckCallback rpcAfterHealthCheckCallback() {
            return new RpcAfterHealthCheckCallback();
        }
    }

    @Bean
    public ProcessorContainer processorContainer(List<ProviderConfigProcessor> providerConfigProcessors,
                                                 List<ConsumerConfigProcessor> consumerConfigProcessors) {
        return new ProcessorContainer(providerConfigProcessors, consumerConfigProcessors);
    }

    @Bean
    @ConditionalOnProperty(name = "com.alipay.sofa.rpc.mock-url")
    public ConsumerMockProcessor consumerMockProcessor() {
        return new ConsumerMockProcessor();
    }

    @Bean
    @ConditionalOnProperty(name = "com.alipay.sofa.rpc.dynamic-config")
    public DynamicConfigProcessor dynamicConfigProcessor() {
        return new DynamicConfigProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProviderRegisterProcessor providerRegisterProcessor() {
        return new ProviderRegisterProcessor();
    }
}
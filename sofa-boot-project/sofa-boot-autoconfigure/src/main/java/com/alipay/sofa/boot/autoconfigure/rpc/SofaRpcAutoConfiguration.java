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

import com.alipay.sofa.healthcheck.startup.ReadinessCheckCallback;
import com.alipay.sofa.rpc.boot.config.ConsulConfigurator;
import com.alipay.sofa.rpc.boot.config.FaultToleranceConfigurator;
import com.alipay.sofa.rpc.boot.config.LocalFileConfigurator;
import com.alipay.sofa.rpc.boot.config.MeshConfigurator;
import com.alipay.sofa.rpc.boot.config.MulticastConfigurator;
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
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ConsumerMockProcessor;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.DynamicConfigProcessor;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ProcessorContainer;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ProviderConfigProcessor;
import com.alipay.sofa.rpc.boot.swagger.BoltSwaggerServiceApplicationListener;
import com.alipay.sofa.rpc.boot.swagger.SwaggerServiceApplicationListener;
import com.alipay.sofa.rpc.config.JAXRSProviderManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SofaBootRpcProperties.class)
@ConditionalOnClass(SofaBootRpcProperties.class)
public class SofaRpcAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ProviderConfigContainer providerConfigContainer() {
        return new ProviderConfigContainer();
    }

    @Bean
    @ConditionalOnMissingBean
    public FaultToleranceConfigurator faultToleranceConfigurator() {
        return new FaultToleranceConfigurator();
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerConfigContainer serverConfigContainer(SofaBootRpcProperties sofaBootRpcProperties) {
        return new ServerConfigContainer(sofaBootRpcProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RegistryConfigContainer registryConfigContainer() {
        return new RegistryConfigContainer();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConsumerConfigHelper consumerConfigHelper(SofaBootRpcProperties sofaBootRpcProperties,
                                                     @Lazy RegistryConfigContainer registryConfigContainer,
                                                     @Value("${"
                                                            + SofaBootRpcConfigConstants.APP_NAME
                                                            + "}") String appName) {
        return new ConsumerConfigHelper(sofaBootRpcProperties, registryConfigContainer, appName);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProviderConfigHelper providerConfigHelper() {
        return new ProviderConfigHelper();
    }

    @Bean
    @ConditionalOnMissingBean
    public ZookeeperConfigurator zookeeperConfigurator() {
        return new ZookeeperConfigurator();
    }

    @Bean
    @ConditionalOnMissingBean
    public MulticastConfigurator multicastConfigurator() {
        return new MulticastConfigurator();
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalFileConfigurator localFileConfigurator() {
        return new LocalFileConfigurator();
    }

    @Bean
    @ConditionalOnMissingBean
    public MeshConfigurator meshConfigurator() {
        return new MeshConfigurator();
    }

    @Bean
    @ConditionalOnMissingBean
    public NacosConfigurator nacosConfigurator() {
        return new NacosConfigurator();
    }

    @Bean
    @ConditionalOnMissingBean
    public SofaRegistryConfigurator sofaRegistryConfigurator() {
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
    @ConditionalOnMissingBean
    public ConsulConfigurator consulConfigurator() {
        return new ConsulConfigurator();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConsumerConfigContainer consumerConfigContainer() {
        return new ConsumerConfigContainer();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextClosedListener applicationContextClosedListener(ProviderConfigContainer providerConfigContainer,
                                                                             ServerConfigContainer serverConfigContainer) {
        return new ApplicationContextClosedListener(providerConfigContainer, serverConfigContainer);
    }

    @Bean
    @ConditionalOnMissingClass({ "com.alipay.sofa.healthcheck.startup.ReadinessCheckCallback" })
    @ConditionalOnClass(SofaBootRpcProperties.class)
    public ApplicationContextRefreshedListener applicationContextRefreshedListener() {
        return new ApplicationContextRefreshedListener();
    }

    @Bean
    @ConditionalOnMissingBean
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
    @ConditionalOnClass({ SofaBootRpcProperties.class, ReadinessCheckCallback.class, Health.class })
    public static class RpcAfterHealthCheckCallbackConfiguration {
        @Bean
        public RpcAfterHealthCheckCallback rpcAfterHealthCheckCallback() {
            return new RpcAfterHealthCheckCallback();
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public List<ContainerRequestFilter> containerRequestFilters(List<ContainerRequestFilter> containerRequestFilters) {

        for (ContainerRequestFilter filter : containerRequestFilters) {
            JAXRSProviderManager.registerCustomProviderInstance(filter);
        }
        return containerRequestFilters;
    }

    @Bean
    @ConditionalOnMissingBean
    public List<ContainerResponseFilter> containerResponseFilters(List<ContainerResponseFilter> containerResponseFilters) {

        for (ContainerResponseFilter filter : containerResponseFilters) {
            JAXRSProviderManager.registerCustomProviderInstance(filter);
        }
        return containerResponseFilters;
    }

    @Bean
    @ConditionalOnMissingBean
    public List<ClientRequestFilter> clientRequestFilters(List<ClientRequestFilter> clientRequestFilters) {

        for (ClientRequestFilter filter : clientRequestFilters) {
            JAXRSProviderManager.registerCustomProviderInstance(filter);
        }
        return clientRequestFilters;
    }

    @Bean
    @ConditionalOnMissingBean
    public List<ClientResponseFilter> clientResponseFilters(List<ClientResponseFilter> clientResponseFilters) {

        for (ClientResponseFilter filter : clientResponseFilters) {
            JAXRSProviderManager.registerCustomProviderInstance(filter);
        }
        return clientResponseFilters;
    }

    @Bean
    @ConditionalOnMissingBean
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
}

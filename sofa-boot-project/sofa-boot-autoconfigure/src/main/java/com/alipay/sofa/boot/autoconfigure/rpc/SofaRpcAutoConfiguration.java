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

import com.alipay.sofa.boot.actuator.health.ReadinessCheckCallback;
import com.alipay.sofa.boot.autoconfigure.rpc.SofaRpcAutoConfiguration.RegistryConfigurationImportSelector;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.rpc.boot.config.FaultToleranceConfigurator;
import com.alipay.sofa.rpc.boot.config.RegistryConfigureProcessor;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcConfigConstants;
import com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties;
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
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ProviderRegisterProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Sofa Rpc.
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 * @author yuanxuan
 */
@AutoConfiguration(after = SofaRuntimeAutoConfiguration.class)
@EnableConfigurationProperties(SofaBootRpcProperties.class)
@ConditionalOnClass(SofaBootRpcProperties.class)
@Import({ RegistryConfigurationImportSelector.class, SwaggerConfiguration.class,
         RestFilterConfiguration.class })
public class SofaRpcAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ProviderConfigContainer providerConfigContainer() {
        return new ProviderConfigContainer();
    }

    @Bean
    @ConditionalOnMissingBean
    public FaultToleranceConfigurator faultToleranceConfigurator(SofaBootRpcProperties properties,
                                                                 Environment environment) {
        return new FaultToleranceConfigurator(properties,
            environment.getProperty(SofaBootRpcConfigConstants.APP_NAME));
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerConfigContainer serverConfigContainer(SofaBootRpcProperties sofaBootRpcProperties) {
        return new ServerConfigContainer(sofaBootRpcProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RegistryConfigContainer registryConfigContainer(SofaBootRpcProperties sofaBootRpcProperties,
                                                           @Qualifier("registryConfigMap") Map<String, RegistryConfigureProcessor> registryConfigMap) {
        return new RegistryConfigContainer(sofaBootRpcProperties, registryConfigMap);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConsumerConfigHelper consumerConfigHelper(SofaBootRpcProperties sofaBootRpcProperties,
                                                     @Lazy RegistryConfigContainer registryConfigContainer,
                                                     Environment environment) {
        String appName = environment.getProperty(SofaBootRpcConfigConstants.APP_NAME);
        return new ConsumerConfigHelper(sofaBootRpcProperties, registryConfigContainer, appName);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProviderConfigHelper providerConfigHelper(ServerConfigContainer serverConfigContainer,
                                                     RegistryConfigContainer registryConfigContainer,
                                                     Environment environment) {
        String appName = environment.getProperty(SofaBootRpcConfigConstants.APP_NAME);
        return new ProviderConfigHelper(serverConfigContainer, registryConfigContainer, appName);
    }

    @Bean(name = "registryConfigMap")
    public Map<String, RegistryConfigureProcessor> configureProcessorMap(List<RegistryConfigureProcessor> processorList) {
        Map<String, RegistryConfigureProcessor> map = new HashMap<>();
        for (RegistryConfigureProcessor processor : processorList) {
            map.put(processor.registryType(), processor);
        }
        return map;
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
    @ConditionalOnMissingClass({ "com.alipay.sofa.boot.actuator.health.ReadinessCheckCallback" })
    @ConditionalOnClass(SofaBootRpcProperties.class)
    public ApplicationContextRefreshedListener applicationContextRefreshedListener() {
        return new ApplicationContextRefreshedListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public SofaBootRpcStartListener sofaBootRpcStartListener(SofaBootRpcProperties sofaBootRpcProperties,
                                                             ProviderConfigContainer providerConfigContainer,
                                                             FaultToleranceConfigurator faultToleranceConfigurator,
                                                             ServerConfigContainer serverConfigContainer,
                                                             RegistryConfigContainer registryConfigContainer) {
        return new SofaBootRpcStartListener(sofaBootRpcProperties, providerConfigContainer,
            faultToleranceConfigurator, serverConfigContainer, registryConfigContainer);
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
    public ProcessorContainer processorContainer(List<ProviderConfigProcessor> providerConfigProcessors,
                                                 List<ConsumerConfigProcessor> consumerConfigProcessors) {
        return new ProcessorContainer(providerConfigProcessors, consumerConfigProcessors);
    }

    @Bean
    @ConditionalOnProperty(name = "com.alipay.sofa.rpc.mock-url")
    public ConsumerMockProcessor consumerMockProcessor(Environment environment) {
        return new ConsumerMockProcessor(environment.getProperty("com.alipay.sofa.rpc.mock-url"));
    }

    @Bean
    @ConditionalOnProperty(name = "com.alipay.sofa.rpc.dynamic-config")
    public DynamicConfigProcessor dynamicConfigProcessor(Environment environment) {
        return new DynamicConfigProcessor(
            environment.getProperty("com.alipay.sofa.rpc.dynamic-config"));
    }

    @Bean
    @ConditionalOnMissingBean
    public ProviderRegisterProcessor providerRegisterProcessor() {
        return new ProviderRegisterProcessor();
    }

    static class RegistryConfigurationImportSelector implements ImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return RegistryConfigurations.registryConfigurationClass();
        }
    }

}

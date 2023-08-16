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
import com.alipay.sofa.boot.autoconfigure.rpc.SofaRpcAutoConfiguration.RegistryConfigurationImportSelector;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.rpc.boot.config.FaultToleranceConfigurator;
import com.alipay.sofa.rpc.boot.config.RegistryConfigureProcessor;
import com.alipay.sofa.rpc.boot.container.ConsumerConfigContainer;
import com.alipay.sofa.rpc.boot.container.ProviderConfigContainer;
import com.alipay.sofa.rpc.boot.container.RegistryConfigContainer;
import com.alipay.sofa.rpc.boot.container.ServerConfigContainer;
import com.alipay.sofa.rpc.boot.context.RpcStartApplicationListener;
import com.alipay.sofa.rpc.boot.context.RpcStopApplicationListener;
import com.alipay.sofa.rpc.boot.context.SofaBootRpcStartListener;
import com.alipay.sofa.rpc.boot.runtime.adapter.helper.ConsumerConfigHelper;
import com.alipay.sofa.rpc.boot.runtime.adapter.helper.ProviderConfigHelper;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ConsumerConfigProcessor;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ConsumerMockProcessor;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.DynamicConfigProcessor;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ProcessorContainer;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ProviderConfigProcessor;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ProviderRegisterProcessor;
import com.alipay.sofa.rpc.common.SofaOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for sofa Rpc.
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 * @author yuanxuan
 * @author huzijie
 */
@AutoConfiguration(after = SofaRuntimeAutoConfiguration.class)
@ConditionalOnClass(ProviderConfigContainer.class)
@EnableConfigurationProperties(SofaBootRpcProperties.class)
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
    @ConditionalOnSwitch(value = "rpcFaultToleranceConfigurator")
    public FaultToleranceConfigurator faultToleranceConfigurator(SofaBootRpcProperties sofaBootRpcProperties,
                                                                 Environment environment) {
        FaultToleranceConfigurator faultToleranceConfigurator = new FaultToleranceConfigurator();
        faultToleranceConfigurator.setAppName(environment
            .getProperty(SofaBootConstants.APP_NAME_KEY));
        faultToleranceConfigurator.setRegulationEffectiveStr(sofaBootRpcProperties
            .getAftRegulationEffective());
        faultToleranceConfigurator.setDegradeEffectiveStr(sofaBootRpcProperties
            .getAftDegradeEffective());
        faultToleranceConfigurator.setTimeWindowStr(sofaBootRpcProperties.getAftTimeWindow());
        faultToleranceConfigurator.setLeastWindowCountStr(sofaBootRpcProperties
            .getAftLeastWindowCount());
        faultToleranceConfigurator.setLeastWindowExceptionRateMultipleStr(sofaBootRpcProperties
            .getAftLeastWindowExceptionRateMultiple());
        faultToleranceConfigurator.setWeightDegradeRateStr(sofaBootRpcProperties
            .getAftWeightDegradeRate());
        faultToleranceConfigurator.setWeightRecoverRateStr(sofaBootRpcProperties
            .getAftWeightRecoverRate());
        faultToleranceConfigurator.setDegradeLeastWeightStr(sofaBootRpcProperties
            .getAftDegradeLeastWeight());
        faultToleranceConfigurator.setDegradeMaxIpCountStr(sofaBootRpcProperties
            .getAftDegradeMaxIpCount());
        return faultToleranceConfigurator;
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerConfigContainer serverConfigContainer(SofaBootRpcProperties sofaBootRpcProperties) {
        ServerConfigContainer serverConfigContainer = new ServerConfigContainer();

        serverConfigContainer.setEnabledIpRange(sofaBootRpcProperties.getEnabledIpRange());
        serverConfigContainer.setBindNetworkInterface(sofaBootRpcProperties
            .getBindNetworkInterface());
        serverConfigContainer.setBoundHostStr(sofaBootRpcProperties.getBoundHost());
        serverConfigContainer.setVirtualHostStr(sofaBootRpcProperties.getVirtualHost());
        serverConfigContainer.setVirtualPortStr(sofaBootRpcProperties.getVirtualPort());

        // init h2c property
        serverConfigContainer.setH2cPortStr(sofaBootRpcProperties.getH2cPort());
        serverConfigContainer.setH2cThreadPoolCoreSizeStr(sofaBootRpcProperties
            .getH2cThreadPoolCoreSize());
        serverConfigContainer.setH2cThreadPoolMaxSizeStr(sofaBootRpcProperties
            .getH2cThreadPoolMaxSize());
        serverConfigContainer.setH2cAcceptsSizeStr(sofaBootRpcProperties.getH2cAcceptsSize());
        serverConfigContainer.setH2cThreadPoolQueueSizeStr(sofaBootRpcProperties
            .getH2cThreadPoolQueueSize());

        // init bolt property
        serverConfigContainer.setBoltPortStr(sofaBootRpcProperties.getBoltPort());
        serverConfigContainer.setBoltThreadPoolCoreSizeStr(sofaBootRpcProperties
            .getBoltThreadPoolCoreSize());
        serverConfigContainer.setBoltThreadPoolMaxSizeStr(sofaBootRpcProperties
            .getBoltThreadPoolMaxSize());
        serverConfigContainer.setBoltAcceptsSizeStr(sofaBootRpcProperties.getBoltAcceptsSize());
        serverConfigContainer.setBoltThreadPoolQueueSizeStr(sofaBootRpcProperties
            .getBoltThreadPoolQueueSize());
        serverConfigContainer.setBoltProcessInIoThread(sofaBootRpcProperties
            .getBoltProcessInIoThread());

        // init rest property
        serverConfigContainer.setRestHostName(sofaBootRpcProperties.getRestHostname());
        serverConfigContainer.setRestPortStr(sofaBootRpcProperties.getRestPort());
        serverConfigContainer.setRestIoThreadSizeStr(sofaBootRpcProperties.getRestIoThreadSize());
        serverConfigContainer.setRestContextPath(sofaBootRpcProperties.getRestContextPath());
        serverConfigContainer.setRestThreadPoolMaxSizeStr(sofaBootRpcProperties
            .getRestThreadPoolMaxSize());
        serverConfigContainer.setRestMaxRequestSizeStr(sofaBootRpcProperties
            .getRestMaxRequestSize());
        serverConfigContainer.setRestTelnetStr(sofaBootRpcProperties.getRestTelnet());
        serverConfigContainer.setRestDaemonStr(sofaBootRpcProperties.getRestDaemon());
        serverConfigContainer.setRestAllowedOrigins(sofaBootRpcProperties.getRestAllowedOrigins());

        // init dubbo property
        serverConfigContainer.setDubboPortStr(sofaBootRpcProperties.getDubboPort());
        serverConfigContainer.setDubboIoThreadSizeStr(sofaBootRpcProperties.getDubboIoThreadSize());
        serverConfigContainer.setDubboThreadPoolMaxSizeStr(sofaBootRpcProperties
            .getDubboThreadPoolMaxSize());
        serverConfigContainer.setDubboAcceptsSizeStr(sofaBootRpcProperties.getDubboAcceptsSize());

        // init http property
        serverConfigContainer.setHttpPortStr(sofaBootRpcProperties.getHttpPort());
        serverConfigContainer.setHttpThreadPoolCoreSizeStr(sofaBootRpcProperties
            .getHttpThreadPoolCoreSize());
        serverConfigContainer.setHttpThreadPoolMaxSizeStr(sofaBootRpcProperties
            .getHttpThreadPoolMaxSize());
        serverConfigContainer.setHttpAcceptsSizeStr(sofaBootRpcProperties.getHttpAcceptsSize());
        serverConfigContainer.setHttpThreadPoolQueueSizeStr(sofaBootRpcProperties
            .getHttpThreadPoolQueueSize());

        // init triple property
        serverConfigContainer.setTriplePortStr(sofaBootRpcProperties.getTriplePort());
        serverConfigContainer.setTripleThreadPoolCoreSizeStr(sofaBootRpcProperties
            .getTripleThreadPoolCoreSize());
        serverConfigContainer.setTripleThreadPoolMaxSizeStr(sofaBootRpcProperties
            .getTripleThreadPoolMaxSize());
        serverConfigContainer.setTripleAcceptsSizeStr(sofaBootRpcProperties.getTripleAcceptsSize());
        serverConfigContainer.setTripleThreadPoolQueueSizeStr(sofaBootRpcProperties
            .getTripleThreadPoolQueueSize());

        return serverConfigContainer;
    }

    @Bean
    @ConditionalOnMissingBean
    public RegistryConfigContainer registryConfigContainer(SofaBootRpcProperties sofaBootRpcProperties,
                                                           Environment environment,
                                                           @Qualifier("registryConfigMap") Map<String, RegistryConfigureProcessor> registryConfigMap) {
        RegistryConfigContainer registryConfigContainer = new RegistryConfigContainer(
            registryConfigMap);

        registryConfigContainer.setDefaultRegistryAddress(sofaBootRpcProperties
            .getRegistryAddress());
        registryConfigContainer.setRegistries(sofaBootRpcProperties.getRegistries());
        registryConfigContainer.setMeshConfig(sofaBootRpcProperties.getEnableMesh());
        registryConfigContainer.setIgnoreRegistry(Boolean.parseBoolean(environment
            .getProperty(SofaOptions.CONFIG_RPC_REGISTER_REGISTRY_IGNORE)));
        return registryConfigContainer;
    }

    @Bean
    @ConditionalOnMissingBean
    public ConsumerConfigHelper consumerConfigHelper(SofaBootRpcProperties sofaBootRpcProperties,
                                                     RegistryConfigContainer registryConfigContainer,
                                                     Environment environment) {
        String appName = environment.getProperty(SofaBootConstants.APP_NAME_KEY);
        ConsumerConfigHelper configHelper = new ConsumerConfigHelper(registryConfigContainer,
            appName);
        configHelper.setReferenceLimit(sofaBootRpcProperties.getConsumerRepeatedReferenceLimit());
        configHelper.setHystrixEnable(sofaBootRpcProperties.getHystrixEnable());
        return configHelper;
    }

    @Bean
    @ConditionalOnMissingBean
    public ProviderConfigHelper providerConfigHelper(ServerConfigContainer serverConfigContainer,
                                                     RegistryConfigContainer registryConfigContainer,
                                                     Environment environment) {
        String appName = environment.getProperty(SofaBootConstants.APP_NAME_KEY);
        return new ProviderConfigHelper(serverConfigContainer, registryConfigContainer, appName);
    }

    @Bean(name = "registryConfigMap")
    @ConditionalOnMissingBean(name = "registryConfigMap")
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
    public RpcStopApplicationListener applicationContextClosedListener(ProviderConfigContainer providerConfigContainer,
                                                                       ServerConfigContainer serverConfigContainer) {
        return new RpcStopApplicationListener(providerConfigContainer, serverConfigContainer);
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcStartApplicationListener applicationContextRefreshedListener(SofaBootRpcProperties sofaBootRpcProperties) {
        RpcStartApplicationListener rpcStartApplicationListener = new RpcStartApplicationListener();
        rpcStartApplicationListener.setEnableAutoPublish(sofaBootRpcProperties
            .isEnableAutoPublish());
        return rpcStartApplicationListener;
    }

    @Bean
    @ConditionalOnMissingBean
    public SofaBootRpcStartListener sofaBootRpcStartListener(SofaBootRpcProperties sofaBootRpcProperties,
                                                             ProviderConfigContainer providerConfigContainer,
                                                             ObjectProvider<FaultToleranceConfigurator> faultToleranceConfigurator,
                                                             ServerConfigContainer serverConfigContainer,
                                                             RegistryConfigContainer registryConfigContainer) {
        SofaBootRpcStartListener rpcStartListener = new SofaBootRpcStartListener(
            providerConfigContainer, faultToleranceConfigurator.getIfUnique(),
            serverConfigContainer, registryConfigContainer);
        rpcStartListener.setLookoutCollectDisable(sofaBootRpcProperties.getLookoutCollectDisable());
        return rpcStartListener;
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessorContainer processorContainer(List<ProviderConfigProcessor> providerConfigProcessors,
                                                 List<ConsumerConfigProcessor> consumerConfigProcessors) {
        return new ProcessorContainer(providerConfigProcessors, consumerConfigProcessors);
    }

    @Bean
    @ConditionalOnProperty(name = "sofa.boot.rpc.mock-url")
    public ConsumerMockProcessor consumerMockProcessor(Environment environment) {
        return new ConsumerMockProcessor(environment.getProperty("sofa.boot.rpc.mock-url"));
    }

    @Bean
    @ConditionalOnProperty(name = "sofa.boot.rpc.dynamic-config")
    public DynamicConfigProcessor dynamicConfigProcessor(Environment environment) {
        return new DynamicConfigProcessor(environment.getProperty("sofa.boot.rpc.dynamic-config"));
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

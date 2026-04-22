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
package com.alipay.sofa.boot.actuator.diagnostic;

import com.alipay.sofa.common.thread.ThreadPoolConfig;
import com.alipay.sofa.common.thread.ThreadPoolGovernor;
import com.alipay.sofa.common.thread.ThreadPoolMonitorWrapper;
import com.alipay.sofa.common.thread.ThreadPoolStatistics;
import com.alipay.sofa.rpc.bootstrap.ConsumerBootstrap;
import com.alipay.sofa.rpc.client.AbstractCluster;
import com.alipay.sofa.rpc.client.AddressHolder;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.context.RpcRuntimeContext;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.context.SpringContextComponent;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentNameFactory;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.endpoint.Access;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link SofaDiagnosticEndpoint}.
 *
 * @author xiaosiyuan
 * @version SofaDiagnosticEndpointTests.java, v 0.1 2026年04月02日 xiaosiyuan Exp $
 */
@ExtendWith(MockitoExtension.class)
public class SofaDiagnosticEndpointTests {

    @Mock
    private SofaRuntimeContext     sofaRuntimeContext;

    @Mock
    private ThreadPoolGovernor     threadPoolGovernor;

    @Mock
    private ComponentManager       componentManager;

    @Mock
    private SofaRuntimeManager     sofaRuntimeManager;

    @Mock
    private ApplicationContext     applicationContext;

    @Mock
    private ServiceComponent       serviceComponent;

    @Mock
    private ReferenceComponent     referenceComponent;

    @Mock
    private Service                service;

    @Mock
    private Reference              reference;

    @InjectMocks
    private SofaDiagnosticEndpoint sofaDiagnosticEndpoint;

    @Test
    void endpointShouldBeReadOnlyByDefault() {
        Endpoint endpoint = SofaDiagnosticEndpoint.class.getAnnotation(Endpoint.class);

        assertThat(endpoint).isNotNull();
        assertThat(endpoint.defaultAccess()).isEqualTo(Access.READ_ONLY);
    }

    @Test
    void summary() {
        ComponentInfo activated = component(ComponentStatus.ACTIVATED);
        ComponentInfo resolved = component(ComponentStatus.RESOLVED);
        ComponentInfo registered = component(ComponentStatus.REGISTERED);
        ComponentInfo unregistered = component(ComponentStatus.UNREGISTERED);
        ThreadPoolMonitorWrapper wrapper = threadPoolWrapper();

        when(sofaRuntimeContext.getComponentManager()).thenReturn(componentManager);
        when(componentManager.getComponents()).thenReturn(
            List.of(activated, resolved, registered, unregistered));
        when(threadPoolGovernor.getAllThreadPoolWrappers()).thenReturn(List.of(wrapper));

        SofaDiagnosticEndpoint.DiagnosticSummary summary = sofaDiagnosticEndpoint.summary();

        assertThat(summary.components().total()).isEqualTo(4);
        assertThat(summary.components().activated()).isEqualTo(1);
        assertThat(summary.components().resolved()).isEqualTo(1);
        assertThat(summary.components().registered()).isEqualTo(1);
        assertThat(summary.components().unregistered()).isEqualTo(1);

        assertThat(summary.threadPools()).hasSize(1);
        SofaDiagnosticEndpoint.ThreadPoolStats threadPoolStats = summary.threadPools().get(0);
        assertThat(threadPoolStats.threadPoolName()).isEqualTo("mockThreadPoolName");
        assertThat(threadPoolStats.spaceName()).isEqualTo("mockSpaceName");
        assertThat(threadPoolStats.coreSize()).isEqualTo(5);
        assertThat(threadPoolStats.maxSize()).isEqualTo(6);
        assertThat(threadPoolStats.queueRemainingCapacity()).isEqualTo(8);

        assertThat(summary.jvm().javaVersion()).isNotBlank();
        assertThat(summary.jvm().availableProcessors()).isPositive();
        assertThat(summary.jvm().pid()).isPositive();
        assertThat(summary.memory().heap().used()).isGreaterThanOrEqualTo(0);
        assertThat(summary.memory().nonHeap().used()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void readServices() {
        Binding publishedBinding = binding("bolt", "bolt://service", true);
        Binding referencedBinding = binding("jvm", "jvm://service", true);

        when(sofaRuntimeContext.getComponentManager()).thenReturn(componentManager);
        when(componentManager.getComponentInfosByType(ServiceComponent.SERVICE_COMPONENT_TYPE))
            .thenReturn(List.of(serviceComponent));
        when(componentManager.getComponentInfosByType(ReferenceComponent.REFERENCE_COMPONENT_TYPE))
            .thenReturn(List.of(referenceComponent));

        when(serviceComponent.getService()).thenReturn(service);
        when(serviceComponent.getState()).thenReturn(ComponentStatus.ACTIVATED);
        Mockito.doReturn(TestService.class).when(service).getInterfaceType();
        when(service.getUniqueId()).thenReturn("serviceUniqueId");
        when(service.getBindings()).thenReturn(Set.of(publishedBinding));

        when(referenceComponent.getReference()).thenReturn(reference);
        when(referenceComponent.getState()).thenReturn(ComponentStatus.RESOLVED);
        Mockito.doReturn(TestService.class).when(reference).getInterfaceType();
        when(reference.getUniqueId()).thenReturn("referenceUniqueId");
        when(reference.isJvmFirst()).thenReturn(true);
        when(reference.isRequired()).thenReturn(false);
        when(reference.getBindings()).thenReturn(Set.of(referencedBinding));

        SofaDiagnosticEndpoint.ServicesDescriptor descriptor = (SofaDiagnosticEndpoint.ServicesDescriptor) sofaDiagnosticEndpoint
            .read("services", "all");

        assertThat(descriptor).isNotNull();
        assertThat(descriptor.published()).hasSize(1);
        assertThat(descriptor.referenced()).hasSize(1);
        assertThat(descriptor.published().get(0).interfaceType()).isEqualTo(TestService.class.getName());
        assertThat(descriptor.published().get(0).uniqueId()).isEqualTo("serviceUniqueId");
        assertThat(descriptor.published().get(0).status()).isEqualTo("ACTIVATED");
        assertThat(descriptor.published().get(0).bindings()).singleElement()
            .satisfies(binding -> {
                assertThat(binding.bindingType()).isEqualTo("bolt");
                assertThat(binding.uri()).isEqualTo("bolt://service");
                assertThat(binding.healthy()).isTrue();
            });
        assertThat(descriptor.referenced().get(0).interfaceType()).isEqualTo(
            TestService.class.getName());
        assertThat(descriptor.referenced().get(0).uniqueId()).isEqualTo("referenceUniqueId");
        assertThat(descriptor.referenced().get(0).status()).isEqualTo("RESOLVED");
        assertThat(descriptor.referenced().get(0).jvmFirst()).isTrue();
        assertThat(descriptor.referenced().get(0).required()).isFalse();
    }

    @Test
    void readServiceDetail() {
        Binding publishedBinding = binding("bolt", "bolt://service", true);
        Binding referencedBinding = binding("jvm", "jvm://service", false);

        when(sofaRuntimeContext.getComponentManager()).thenReturn(componentManager);
        when(componentManager.getComponentInfosByType(ServiceComponent.SERVICE_COMPONENT_TYPE))
            .thenReturn(List.of(serviceComponent));
        when(componentManager.getComponentInfosByType(ReferenceComponent.REFERENCE_COMPONENT_TYPE))
            .thenReturn(List.of(referenceComponent));

        when(serviceComponent.getService()).thenReturn(service);
        when(serviceComponent.getState()).thenReturn(ComponentStatus.ACTIVATED);
        when(serviceComponent.isHealthy()).thenReturn(healthResult(true, "service healthy"));
        Mockito.doReturn(TestService.class).when(service).getInterfaceType();
        when(service.getUniqueId()).thenReturn("serviceUniqueId");
        when(service.getTarget()).thenReturn(new TestServiceImpl());
        when(service.getBindings()).thenReturn(Set.of(publishedBinding));

        when(referenceComponent.getReference()).thenReturn(reference);
        when(referenceComponent.getState()).thenReturn(ComponentStatus.RESOLVED);
        when(referenceComponent.isHealthy()).thenReturn(healthResult(false, "reference unhealthy"));
        Mockito.doReturn(TestService.class).when(reference).getInterfaceType();
        when(reference.getUniqueId()).thenReturn("referenceUniqueId");
        when(reference.isJvmFirst()).thenReturn(true);
        when(reference.isRequired()).thenReturn(true);
        when(reference.getBindings()).thenReturn(Set.of(referencedBinding));

        SofaDiagnosticEndpoint.ServiceDetailDescriptor descriptor = (SofaDiagnosticEndpoint.ServiceDetailDescriptor) sofaDiagnosticEndpoint
            .read("serviceDetail", TestService.class.getName());

        assertThat(descriptor).isNotNull();
        assertThat(descriptor.interfaceId()).isEqualTo(TestService.class.getName());
        assertThat(descriptor.publishedDetails()).singleElement().satisfies(detail -> {
            assertThat(detail.implementationClass()).isEqualTo(TestServiceImpl.class.getName());
            assertThat(detail.healthy()).isTrue();
            assertThat(detail.healthReport()).isEqualTo("service healthy");
        });
        assertThat(descriptor.referencedDetails()).singleElement().satisfies(detail -> {
            assertThat(detail.jvmFirst()).isTrue();
            assertThat(detail.required()).isTrue();
            assertThat(detail.healthy()).isFalse();
            assertThat(detail.healthReport()).isEqualTo("reference unhealthy");
        });
    }

    @Test
    void executeThrowsWhenCommandIsUnknown() {
        assertThatThrownBy(() -> sofaDiagnosticEndpoint.execute("unknown", null, null, null, null,
            null)).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unknown command: unknown")
            .hasMessageContaining("Supported commands: gc, thread-dump, heap-dump, clear-cache, refresh-component");
    }

    @Test
    void executeClearCacheUsesAllWhenTypeIsNull() {
        try (ConsumerBootstrapRegistration ignored = registerConsumerBootstrap(
            TestService.class.getName(), "serviceUniqueId", 2, false)) {
            SofaDiagnosticEndpoint.OperationResult result = sofaDiagnosticEndpoint.execute(
                "clear-cache", null, TestService.class.getName(), "serviceUniqueId", null, null);

            assertThat(result.success()).isTrue();
            assertThat(result.message()).isEqualTo("All caches cleared successfully");
            Map<String, Object> data = result.data();
            assertThat(data).containsOnlyKeys("rpcRouter");
            assertThat(((SofaDiagnosticEndpoint.OperationResult) data.get("rpcRouter")).success())
                .isTrue();
        }
    }

    @Test
    void executeClearCacheUsesAllWhenTypeIsEmpty() {
        SofaDiagnosticEndpoint.OperationResult result = sofaDiagnosticEndpoint.execute(
            "clear-cache", "", TestService.class.getName(), "serviceUniqueId", null, null);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo("Some caches failed to clear");
        Map<String, Object> data = result.data();
        assertThat(((SofaDiagnosticEndpoint.OperationResult) data.get("rpcRouter")).success())
            .isFalse();
    }

    @Test
    void executeClearCacheThrowsWhenTypeIsUnknown() {
        assertThatThrownBy(() -> sofaDiagnosticEndpoint.execute("clear-cache", "unknown", null,
            null, null, null)).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unknown cache type: unknown")
            .hasMessageContaining("Supported cache types: all, rpc-router");
    }

    @Test
    void executeClearCacheRpcRouterReturnsFailureWhenNoConsumerMatches() {
        SofaDiagnosticEndpoint.OperationResult result = sofaDiagnosticEndpoint
            .execute("clear-cache", "rpc-router", TestService.class.getName(), "serviceUniqueId",
                null, null);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo("No matching consumer references found");
        assertThat(result.data()).containsEntry("interfaceType", TestService.class.getName())
            .containsEntry("uniqueId", "serviceUniqueId");
    }

    @Test
    void executeClearCacheRpcRouterReturnsSuccessWhenConsumersMatch() {
        try (ConsumerBootstrapRegistration ignored = registerConsumerBootstrap(
            TestService.class.getName(), "serviceUniqueId", 3, false)) {
            SofaDiagnosticEndpoint.OperationResult result = sofaDiagnosticEndpoint.execute(
                "clear-cache", "rpc-router", TestService.class.getName(), "serviceUniqueId", null,
                null);

            assertThat(result.success()).isTrue();
            assertThat(result.message()).isEqualTo("RPC router cache cleared successfully");
            assertThat(result.data()).containsEntry("clearedCount", 1).containsEntry(
                "clearedProviderCount", 3);
            assertThat((List<?>) result.data().get("components")).singleElement().isEqualTo(
                TestService.class.getName() + ":serviceUniqueId");
        }
    }

    @Test
    void executeClearCacheRpcRouterReturnsPartialFailureWhenSomeConsumersFail() {
        try (ConsumerBootstrapRegistration successConsumer = registerConsumerBootstrap(
            TestService.class.getName(), "success", 1, false);
                ConsumerBootstrapRegistration failedConsumer = registerConsumerBootstrap(
                    TestService.class.getName(), "failed", 2, true)) {
            SofaDiagnosticEndpoint.OperationResult result = sofaDiagnosticEndpoint.execute(
                "clear-cache", "rpc-router", TestService.class.getName(), null, null, null);

            assertThat(result.success()).isFalse();
            assertThat(result.message()).isEqualTo("RPC router cache cleared partially");
            assertThat((List<?>) result.data().get("components")).singleElement().isEqualTo(
                TestService.class.getName() + ":success");
            assertThat((List<?>) result.data().get("failed")).singleElement().asString()
                .contains(TestService.class.getName() + ":failed")
                .contains("IllegalStateException");
        }
    }

    @Test
    void executeClearCacheRpcRouterReturnsFailureWhenAllConsumersFail() {
        try (ConsumerBootstrapRegistration ignored = registerConsumerBootstrap(
            TestService.class.getName(), "failed", 1, true)) {
            SofaDiagnosticEndpoint.OperationResult result = sofaDiagnosticEndpoint.execute(
                "clear-cache", "rpc-router", TestService.class.getName(), "failed", null, null);

            assertThat(result.success()).isFalse();
            assertThat(result.message()).isEqualTo("Failed to clear RPC router cache");
            assertThat((List<?>) result.data().get("components")).isEmpty();
            assertThat((List<?>) result.data().get("failed")).singleElement().asString()
                .contains(TestService.class.getName() + ":failed")
                .contains("IllegalStateException");
        }
    }

    @Test
    void executeRefreshComponentReturnsFailureWhenNoFactoryMatches() {
        when(sofaRuntimeContext.getComponentManager()).thenReturn(componentManager);
        when(sofaRuntimeContext.getSofaRuntimeManager()).thenReturn(sofaRuntimeManager);
        when(sofaRuntimeManager.getRootApplicationContext()).thenReturn(null);
        when(componentManager.getComponentInfosByType(SpringContextComponent.SPRING_COMPONENT_TYPE))
            .thenReturn(List.of());
        when(applicationContext.getBeanDefinitionNames()).thenReturn(new String[0]);

        SofaDiagnosticEndpoint.OperationResult result = sofaDiagnosticEndpoint.execute(
            "refresh-component", null, TestService.class.getName(), "serviceUniqueId", null, null);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo(
            "No matching Spring-managed service factories found for refresh");
        assertThat(result.data()).containsEntry("interfaceType", TestService.class.getName())
            .containsEntry("uniqueId", "serviceUniqueId");
    }

    @Test
    void executeRefreshComponentReturnsSuccessAndSkippedWhenFactoriesMatch() {
        ServiceFactoryBean refreshable = serviceFactoryBean(TestService.class, "refresh");
        ServiceFactoryBean registered = serviceFactoryBean(TestService.class, "registered");
        String refreshableName = componentRawName(TestService.class, "refresh");
        String registeredName = componentRawName(TestService.class, "registered");

        when(sofaRuntimeContext.getComponentManager()).thenReturn(componentManager);
        when(sofaRuntimeContext.getSofaRuntimeManager()).thenReturn(sofaRuntimeManager);
        when(sofaRuntimeManager.getRootApplicationContext()).thenReturn(null);
        when(componentManager.getComponentInfosByType(SpringContextComponent.SPRING_COMPONENT_TYPE))
            .thenReturn(List.of());
        when(applicationContext.getId()).thenReturn("app");
        when(applicationContext.getBeanDefinitionNames()).thenReturn(
            new String[] { "refreshBean", "registeredBean" });
        when(applicationContext.isTypeMatch("&refreshBean", ServiceFactoryBean.class)).thenReturn(
            true);
        when(applicationContext.isTypeMatch("&registeredBean", ServiceFactoryBean.class))
            .thenReturn(true);
        when(applicationContext.getBean("&refreshBean", ServiceFactoryBean.class)).thenReturn(
            refreshable);
        when(applicationContext.getBean("&registeredBean", ServiceFactoryBean.class)).thenReturn(
            registered);
        when(componentManager.isRegistered(Mockito.any(ComponentName.class))).thenAnswer(
            invocation -> {
                ComponentName componentName = invocation.getArgument(0);
                return componentName != null
                       && componentName.getRawName().equals(registeredName);
            });

        SofaDiagnosticEndpoint.OperationResult result = sofaDiagnosticEndpoint.execute(
            "refresh-component", null, TestService.class.getName(), null, null, null);

        assertThat(result.success()).isTrue();
        assertThat(result.message()).isEqualTo("Component refreshed successfully");
        assertThat(result.data()).containsEntry("refreshedCount", 1);
        assertThat((List<?>) result.data().get("components")).singleElement()
            .isEqualTo(refreshableName);
        assertThat((List<?>) result.data().get("skipped")).singleElement()
            .isEqualTo(registeredName);
    }

    @Test
    void executeRefreshComponentReturnsFailureWhenFactoryRefreshThrows() throws Exception {
        ServiceFactoryBean refreshable = serviceFactoryBean(TestService.class, "refresh");
        String refreshableName = componentRawName(TestService.class, "refresh");

        when(sofaRuntimeContext.getComponentManager()).thenReturn(componentManager);
        when(sofaRuntimeContext.getSofaRuntimeManager()).thenReturn(sofaRuntimeManager);
        when(sofaRuntimeManager.getRootApplicationContext()).thenReturn(null);
        when(componentManager.getComponentInfosByType(SpringContextComponent.SPRING_COMPONENT_TYPE))
            .thenReturn(List.of());
        when(applicationContext.getId()).thenReturn("app");
        when(applicationContext.getBeanDefinitionNames())
            .thenReturn(new String[] { "refreshBean" });
        when(applicationContext.isTypeMatch("&refreshBean", ServiceFactoryBean.class)).thenReturn(
            true);
        when(applicationContext.getBean("&refreshBean", ServiceFactoryBean.class)).thenReturn(
            refreshable);
        when(componentManager.isRegistered(Mockito.any(ComponentName.class))).thenReturn(false);
        Mockito.doThrow(new IllegalStateException("refresh failed")).when(refreshable)
            .afterPropertiesSet();

        SofaDiagnosticEndpoint.OperationResult result = sofaDiagnosticEndpoint.execute(
            "refresh-component", null, TestService.class.getName(), "refresh", null, null);

        assertThat(result.success()).isFalse();
        assertThat(result.message()).isEqualTo("Failed to refresh component");
        assertThat(result.data()).containsEntry("refreshedCount", 0)
            .containsEntry("failedComponent", refreshableName)
            .containsEntry("error", "refresh failed");
    }

    private ComponentInfo component(ComponentStatus componentStatus) {
        ComponentInfo componentInfo = Mockito.mock(ComponentInfo.class);
        when(componentInfo.getState()).thenReturn(componentStatus);
        return componentInfo;
    }

    @SuppressWarnings("rawtypes")
    private ConsumerBootstrapRegistration registerConsumerBootstrap(String interfaceId,
                                                                    String uniqueId,
                                                                    int providerCount,
                                                                    boolean updateFails) {
        ConsumerBootstrap consumerBootstrap = Mockito.mock(ConsumerBootstrap.class);
        ConsumerConfig consumerConfig = Mockito.mock(ConsumerConfig.class);
        AbstractCluster cluster = Mockito.mock(AbstractCluster.class);
        AddressHolder addressHolder = Mockito.mock(AddressHolder.class);
        when(consumerBootstrap.getConsumerConfig()).thenReturn(consumerConfig);
        when(consumerConfig.getInterfaceId()).thenReturn(interfaceId);
        when(consumerConfig.getUniqueId()).thenReturn(uniqueId);
        when(consumerBootstrap.getCluster()).thenReturn(cluster);
        when(cluster.getAddressHolder()).thenReturn(addressHolder);
        when(addressHolder.getAllProviderSize()).thenReturn(providerCount);
        if (updateFails) {
            Mockito.doThrow(new IllegalStateException("update failed")).when(cluster)
                .updateAllProviders(Mockito.anyList());
        }
        RpcRuntimeContext.cacheConsumerConfig(consumerBootstrap);
        return new ConsumerBootstrapRegistration(consumerBootstrap);
    }

    private ServiceFactoryBean serviceFactoryBean(Class<?> interfaceClass, String uniqueId) {
        ServiceFactoryBean serviceFactoryBean = Mockito.mock(ServiceFactoryBean.class);
        Mockito.doReturn(interfaceClass).when(serviceFactoryBean).getInterfaceClass();
        when(serviceFactoryBean.getUniqueId()).thenReturn(uniqueId);
        return serviceFactoryBean;
    }

    private String componentRawName(Class<?> interfaceClass, String uniqueId) {
        return ComponentNameFactory.createComponentName(ServiceComponent.SERVICE_COMPONENT_TYPE,
            interfaceClass, uniqueId).getRawName();
    }

    private ThreadPoolMonitorWrapper threadPoolWrapper() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 6, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(8));
        ThreadPoolConfig threadPoolConfig = Mockito.mock(ThreadPoolConfig.class);
        when(threadPoolConfig.getThreadPoolName()).thenReturn("mockThreadPoolName");
        when(threadPoolConfig.getSpaceName()).thenReturn("mockSpaceName");
        ThreadPoolStatistics statistics = new ThreadPoolStatistics(executor);
        return new ThreadPoolMonitorWrapper(executor, threadPoolConfig, statistics);
    }

    private Binding binding(String type, String uri, boolean healthy) {
        Binding binding = Mockito.mock(Binding.class);
        when(binding.getBindingType()).thenReturn(new BindingType(type));
        when(binding.getURI()).thenReturn(uri);
        when(binding.healthCheck()).thenReturn(healthResult(healthy, null));
        return binding;
    }

    private HealthResult healthResult(boolean healthy, String report) {
        HealthResult healthResult = new HealthResult("test");
        healthResult.setHealthy(healthy);
        healthResult.setHealthReport(report);
        return healthResult;
    }

    interface TestService {
    }

    static class TestServiceImpl implements TestService {
    }

    private static final class ConsumerBootstrapRegistration implements AutoCloseable {
        private final ConsumerBootstrap<?> consumerBootstrap;

        private ConsumerBootstrapRegistration(ConsumerBootstrap<?> consumerBootstrap) {
            this.consumerBootstrap = consumerBootstrap;
        }

        @Override
        public void close() {
            RpcRuntimeContext.invalidateConsumerConfig(consumerBootstrap);
        }
    }
}

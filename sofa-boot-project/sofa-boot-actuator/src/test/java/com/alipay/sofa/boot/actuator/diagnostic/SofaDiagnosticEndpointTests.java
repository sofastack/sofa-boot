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
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(summary.memory().getHeap().getUsed()).isGreaterThanOrEqualTo(0);
        assertThat(summary.memory().getNonHeap().getUsed()).isGreaterThanOrEqualTo(0);
        assertThat(summary.memory().getGarbageCollectors()).isNotEmpty();
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

    private ComponentInfo component(ComponentStatus componentStatus) {
        ComponentInfo componentInfo = Mockito.mock(ComponentInfo.class);
        when(componentInfo.getState()).thenReturn(componentStatus);
        return componentInfo;
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
}

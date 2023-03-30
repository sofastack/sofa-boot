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
package com.alipay.sofa.runtime.service.component;

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.sample.SampleService;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.DefaultImplementation;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import com.alipay.sofa.runtime.spi.service.DefaultDynamicServiceProxyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link ReferenceComponent}.
 *
 * @author huzijie
 * @version ReferenceComponentTests.java, v 0.1 2023年04月10日 7:29 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class ReferenceComponentTests {

    public static final Object            object         = new Object();

    private final Implementation          implementation = new DefaultImplementation();

    private ReferenceComponent            referenceComponent;

    private Reference                     reference;

    @Mock
    private BindingAdapterFactory         bindingAdapterFactory;

    @Mock
    private SofaRuntimeContext            sofaRuntimeContext;

    @Mock
    private ComponentManager              componentManager;

    @Mock
    private ServiceComponent              mockServiceComponent;

    @Mock
    private Binding                       mockBinding;

    @Mock
    private SofaRuntimeContext.Properties properties;

    @BeforeEach
    public void setUp() {
        reference = new ReferenceImpl("ABC", SampleService.class, InterfaceMode.api, true);
        referenceComponent = new ReferenceComponent(reference, implementation,
            bindingAdapterFactory, sofaRuntimeContext);
    }

    @Test
    void getReference() {
        assertThat(referenceComponent.getReference()).isEqualTo(reference);
    }

    @Test
    void getTypeShouldReturnReferenceComponentType() {
        assertThat(referenceComponent.getType()).isEqualTo(
            ReferenceComponent.REFERENCE_COMPONENT_TYPE);
    }

    @Test
    void getPropertiesShouldReturnEmptyMap() {
        assertThat(referenceComponent.getProperties()).hasSize(0);
    }

    @Test
    void isHealthyWhenWithException() {
        referenceComponent.exception(new RuntimeException("fail"));

        HealthResult healthResult = referenceComponent.isHealthy();
        assertThat(healthResult.isHealthy()).isFalse();
        assertThat(healthResult.getHealthReport()).isEqualTo(" [fail]");
    }

    @Test
    void isHealthyWhenBindingError() {
        reference.addBinding(mockBinding);
        HealthResult mockBindingResult = new HealthResult("mockBinding");
        mockBindingResult.setHealthy(false);
        when(mockBinding.healthCheck()).thenReturn(mockBindingResult);

        HealthResult healthResult = referenceComponent.isHealthy();
        assertThat(healthResult.isHealthy()).isFalse();
        assertThat(healthResult.getHealthReport()).isEqualTo("[mockBinding,failed]");
    }

    @Test
    void isHealthyWhenJvmBindingNotFound() {
        reference.addBinding(new JvmBinding());
        skipJvmReferenceHealthCheck(false);
        registerServiceComponent(false);

        HealthResult healthResult = referenceComponent.isHealthy();
        assertThat(healthResult.isHealthy()).isFalse();
        assertThat(healthResult.getHealthReport()).isEqualTo(
            "[jvm,can not find corresponding jvm service]");
    }

    @Test
    void isHealthyWhenJvmBindingNotFoundWithDetails() {
        reference.addBinding(new JvmBinding());
        skipJvmReferenceHealthCheck(false);
        registerServiceComponent(false);
        when(properties.isReferenceHealthCheckMoreDetailEnable()).thenReturn(true);
        Property sourceProperty = new Property();
        sourceProperty.setName(ComponentDefinitionInfo.SOURCE);
        ComponentDefinitionInfo componentDefinitionInfo = new ComponentDefinitionInfo();
        sourceProperty.setValue(componentDefinitionInfo);
        componentDefinitionInfo.setInterfaceMode(InterfaceMode.api);
        componentDefinitionInfo.putInfo(ComponentDefinitionInfo.BEAN_ID, "beanA");
        componentDefinitionInfo.putInfo(ComponentDefinitionInfo.BEAN_CLASS_NAME, "SampleClassImpl");
        componentDefinitionInfo.putInfo(ComponentDefinitionInfo.LOCATION, "fieldA");

        referenceComponent.getProperties().put(ComponentDefinitionInfo.SOURCE, sourceProperty);

        HealthResult healthResult = referenceComponent.isHealthy();
        assertThat(healthResult.isHealthy()).isFalse();
        assertThat(healthResult.getHealthReport())
            .isEqualTo(
                "[jvm,can not find corresponding jvm service.Which first declared through:api beanId:beanA,beanClassName:SampleClassImpl,location:fieldA]");
    }

    @Test
    void skipJvmHealthyUseInterfaceProperties() {
        reference.addBinding(new JvmBinding());
        skipJvmReferenceHealthCheck(false, "com.alipay.sofa.runtime.sample.SampleService:ABC");

        HealthResult healthResult = referenceComponent.isHealthy();
        assertThat(healthResult.isHealthy()).isTrue();
        assertThat(healthResult.getHealthReport()).isEqualTo("[jvm,passed]");
    }

    @Test
    void skipJvmHealthyUseRequired() {
        reference.addBinding(new JvmBinding());
        reference.setRequired(false);

        HealthResult healthResult = referenceComponent.isHealthy();
        assertThat(healthResult.isHealthy()).isTrue();
        assertThat(healthResult.getHealthReport()).isEqualTo("[jvm,passed]");
    }

    @Test
    void isHealthyWhenJvmBindingFound() {
        reference.addBinding(new JvmBinding());
        skipJvmReferenceHealthCheck(false);
        registerServiceComponent(true);

        HealthResult healthResult = referenceComponent.isHealthy();
        assertThat(healthResult.isHealthy()).isTrue();
        assertThat(healthResult.getHealthReport()).isEqualTo("[jvm,passed]");
    }

    @Test
    void activateWhenNoBinding() {
        referenceComponent.activate();
        assertThat(referenceComponent.getImplementation()).isEqualTo(implementation);
    }

    @Test
    void activateWhenBindingTypeNotFound() {
        reference.addBinding(new JvmBinding());

        assertThatThrownBy(() -> referenceComponent.activate()).isInstanceOf(ServiceRuntimeException.class)
                .hasMessageContaining("01-00100");
    }

    @Test
    void activateWhenOnlyJvmBinding() {
        reference.addBinding(new JvmBinding());
        when(bindingAdapterFactory.getBindingAdapter(any())).thenReturn(
            new MockBindingAdapter(false));

        referenceComponent.activate();
        assertThat(referenceComponent.getImplementation()).isNotEqualTo(implementation);
        assertThat(referenceComponent.getImplementation().getTarget()).isEqualTo(object);
    }

    @Test
    void activateWhenMultiBinding() {
        reference.addBinding(new JvmBinding());
        reference.addBinding(mockBinding);
        when(bindingAdapterFactory.getBindingAdapter(any())).thenReturn(
            new MockBindingAdapter(false));

        referenceComponent.activate();
        assertThat(referenceComponent.getImplementation()).isNotEqualTo(implementation);
        assertThat(referenceComponent.getImplementation().getTarget()).isEqualTo(object);
    }

    @Test
    void activateWhenInBindingError() {
        reference.addBinding(new JvmBinding());
        when(bindingAdapterFactory.getBindingAdapter(any())).thenReturn(new MockBindingAdapter(true));

        assertThatThrownBy(() -> referenceComponent.activate()).isInstanceOf(RuntimeException.class).hasMessageContaining("out error");
    }

    @Test
    void unregisterWhenBindingTypeNotFound() {
        reference.addBinding(new JvmBinding());

        assertThatThrownBy(() -> referenceComponent.unregister()).isInstanceOf(ServiceRuntimeException.class)
                .hasMessageContaining("01-00100");
    }

    @Test
    void unregisterWhenBindingSuccess() {
        reference.addBinding(new JvmBinding());
        when(bindingAdapterFactory.getBindingAdapter(any())).thenReturn(
            new MockBindingAdapter(false));

        referenceComponent.unregister();
        verify(bindingAdapterFactory, times(1)).getBindingAdapter(any());
    }

    @Test
    void getImplement() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = CompletableFuture.supplyAsync(() -> referenceComponent.getImplementation(), executor);

        assertThat(future.isDone()).isFalse();

        referenceComponent.activate();
        assertThat(future.get()).isEqualTo(implementation);
    }

    @Test
    void getImplementInterrupted() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = CompletableFuture.runAsync(() -> referenceComponent.getImplementation(), executor);

        assertThat(future.isDone()).isFalse();

        executor.shutdownNow();

        Throwable throwable = catchThrowable(future::get);
        assertThat(throwable).isInstanceOf(ExecutionException.class);
        Throwable cause = throwable.getCause();
        assertThat(cause).isInstanceOf(ServiceRuntimeException.class).hasMessageContaining("01-00101");
    }

    @Test
    void getImplementException() {
        referenceComponent.exception(new RuntimeException("ref fail"));
        assertThatThrownBy(() -> referenceComponent.getImplementation()).isInstanceOf(ServiceRuntimeException.class).hasMessageContaining("01-00102");
    }

    private void skipJvmReferenceHealthCheck(boolean skip, String... interfaces) {
        when(sofaRuntimeContext.getProperties()).thenReturn(properties);
        when(properties.isSkipJvmReferenceHealthCheck()).thenReturn(skip);
        when(properties.getSkipJvmReferenceHealthCheckList()).thenReturn(List.of(interfaces));
    }

    private void registerServiceComponent(boolean found) {
        when(sofaRuntimeContext.getComponentManager()).thenReturn(componentManager);
        if (!found) {
            when(componentManager.getComponentInfo(any())).thenReturn(null);
            when(sofaRuntimeContext.getServiceProxyManager()).thenReturn(
                new DefaultDynamicServiceProxyManager());
        } else {
            when(componentManager.getComponentInfo(any())).thenReturn(mockServiceComponent);
            when(mockServiceComponent.getImplementation()).thenReturn(
                new DefaultImplementation(object));
        }
    }

    static class MockBindingAdapter implements BindingAdapter {

        private final boolean exception;

        public MockBindingAdapter(boolean exception) {
            this.exception = exception;
        }

        @Override
        public void preOutBinding(Object contract, Binding binding, Object target,
                                  SofaRuntimeContext sofaRuntimeContext) {
            if (exception) {
                throw new RuntimeException("preOut error");
            }
        }

        @Override
        public Object outBinding(Object contract, Binding binding, Object target,
                                 SofaRuntimeContext sofaRuntimeContext) {
            if (exception) {
                throw new RuntimeException("out error");
            }
            return ReferenceComponentTests.object;
        }

        @Override
        public void preUnoutBinding(Object contract, Binding binding, Object target,
                                    SofaRuntimeContext sofaRuntimeContext) {
            if (exception) {
                throw new RuntimeException("un preOut error");
            }
        }

        @Override
        public void postUnoutBinding(Object contract, Binding binding, Object target,
                                     SofaRuntimeContext sofaRuntimeContext) {
            if (exception) {
                throw new RuntimeException("un out error");
            }
        }

        @Override
        public Object inBinding(Object contract, Binding binding,
                                SofaRuntimeContext sofaRuntimeContext) {
            if (exception) {
                throw new RuntimeException("out error");
            }
            return ReferenceComponentTests.object;
        }

        @Override
        public void unInBinding(Object contract, Binding binding,
                                SofaRuntimeContext sofaRuntimeContext) {

        }

        @Override
        public BindingType getBindingType() {
            return null;
        }

        @Override
        public Class getBindingClass() {
            return null;
        }
    }

}

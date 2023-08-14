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
import com.alipay.sofa.runtime.sample.SampleService;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link ServiceComponent}.
 *
 * @author huzijie
 * @version ServiceComponentTests.java, v 0.1 2023年04月10日 7:29 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class ServiceComponentTests {

    private Service                       service;

    private ServiceComponent              serviceComponent;

    @Mock
    private Implementation                implementation;

    @Mock
    private BindingAdapterFactory         bindingAdapterFactory;

    @Mock
    private SofaRuntimeContext            sofaRuntimeContext;

    private SofaRuntimeContext.Properties properties;

    @BeforeEach
    public void setUp() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        properties = new SofaRuntimeContext.Properties();
        when(sofaRuntimeContext.getProperties()).thenReturn(properties);
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory,
            sofaRuntimeContext);
    }

    @Test
    void getService() {
        assertThat(serviceComponent.getService()).isEqualTo(service);
    }

    @Test
    void getTypeShouldReturnServiceComponentType() {
        assertThat(serviceComponent.getType()).isEqualTo(ServiceComponent.SERVICE_COMPONENT_TYPE);
    }

    @Test
    void getPropertiesShouldReturnEmptyMap() {
        assertThat(serviceComponent.getProperties()).hasSize(0);
    }

    @Test
    void resolveWhenTargetIsNull() {
        service = new ServiceImpl("", SampleService.class, null);
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory, sofaRuntimeContext);

        assertThatThrownBy(() -> serviceComponent.resolve()).isInstanceOf(ServiceRuntimeException.class)
                .hasMessageContaining("01-00000");
    }

    @Test
    void resolveWhenBindingTypeNotFound() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        service.addBinding(new JvmBinding());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory, sofaRuntimeContext);

        assertThatThrownBy(() -> serviceComponent.resolve()).isInstanceOf(ServiceRuntimeException.class)
                .hasMessageContaining("01-00001");
    }

    @Test
    void resolveWhenBindingFail() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        service.addBinding(new JvmBinding());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory, sofaRuntimeContext);

        when(bindingAdapterFactory.getBindingAdapter(any())).thenReturn(new MockBindingAdapter(true));

        assertThatThrownBy(() -> serviceComponent.resolve()).isInstanceOf(ServiceRuntimeException.class)
                .hasMessageContaining("01-00003");
    }

    @Test
    void resolveSuccess() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        service.addBinding(new JvmBinding());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory,
            sofaRuntimeContext);

        when(bindingAdapterFactory.getBindingAdapter(any())).thenReturn(
            new MockBindingAdapter(false));
        serviceComponent.resolve();
        verify(bindingAdapterFactory, times(1)).getBindingAdapter(any());
    }

    @Test
    void registerWithInterfaceCheck() {
        service = new ServiceImpl("", Runnable.class, new SampleServiceImpl());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory, sofaRuntimeContext);

        SofaRuntimeContext.Properties properties = mock(SofaRuntimeContext.Properties.class);
        when(sofaRuntimeContext.getProperties()).thenReturn(properties);
        when(properties.isServiceInterfaceTypeCheck()).thenReturn(true);

        assertThatThrownBy(() -> serviceComponent.register()).isInstanceOf(ServiceRuntimeException.class)
                        .hasMessageContaining("01-00104");

        when(properties.isServiceInterfaceTypeCheck()).thenReturn(false);
        serviceComponent.register();
    }

    @Test
    void activateWhenTargetIsNull() {
        service = new ServiceImpl("", SampleService.class, null);
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory, sofaRuntimeContext);

        assertThatThrownBy(() -> serviceComponent.activate()).isInstanceOf(ServiceRuntimeException.class)
                .hasMessageContaining("01-00000");
    }

    @Test
    void activateWhenBindingTypeNotFound() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        service.addBinding(new JvmBinding());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory, sofaRuntimeContext);

        assertThatThrownBy(() -> serviceComponent.activate()).isInstanceOf(ServiceRuntimeException.class)
                .hasMessageContaining("01-00001");
    }

    @Test
    void activateWhenBindingFail() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        service.addBinding(new JvmBinding());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory, sofaRuntimeContext);

        when(bindingAdapterFactory.getBindingAdapter(any())).thenReturn(new MockBindingAdapter(true));

        assertThatThrownBy(() -> serviceComponent.activate()).isInstanceOf(ServiceRuntimeException.class)
                .hasMessageContaining("01-00005");
    }

    @Test
    void activateSuccess() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        service.addBinding(new JvmBinding());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory,
            sofaRuntimeContext);

        when(bindingAdapterFactory.getBindingAdapter(any())).thenReturn(
            new MockBindingAdapter(false));
        serviceComponent.activate();
        verify(bindingAdapterFactory, times(1)).getBindingAdapter(any());
    }

    @Test
    void deActivateWhenTargetIsNull() {
        service = new ServiceImpl("", SampleService.class, null);
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory, sofaRuntimeContext);

        assertThatThrownBy(() -> serviceComponent.deactivate()).isInstanceOf(ServiceRuntimeException.class)
                .hasMessageContaining("01-00000");
    }

    @Test
    void deActivateWhenBindingTypeNotFound() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        service.addBinding(new JvmBinding());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory, sofaRuntimeContext);

        assertThatThrownBy(() -> serviceComponent.deactivate()).isInstanceOf(ServiceRuntimeException.class)
                .hasMessageContaining("01-00001");
    }

    @Test
    void deActivateWhenBindingFail() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        service.addBinding(new JvmBinding());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory, sofaRuntimeContext);

        when(bindingAdapterFactory.getBindingAdapter(any())).thenReturn(new MockBindingAdapter(true));

        assertThatThrownBy(() -> serviceComponent.deactivate()).isInstanceOf(ServiceRuntimeException.class)
                .hasMessageContaining("01-00007");
    }

    @Test
    void deActivateSuccess() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        service.addBinding(new JvmBinding());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory,
            sofaRuntimeContext);

        when(bindingAdapterFactory.getBindingAdapter(any())).thenReturn(
            new MockBindingAdapter(false));
        serviceComponent.deactivate();
        verify(bindingAdapterFactory, times(1)).getBindingAdapter(any());
    }

    @Test
    void unRegisterWhenTargetIsNull() {
        service = new ServiceImpl("", SampleService.class, null);
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory, sofaRuntimeContext);

        assertThatThrownBy(() -> serviceComponent.unregister()).isInstanceOf(ServiceRuntimeException.class)
                .hasMessageContaining("01-00000");
    }

    @Test
    void unRegisterWhenBindingTypeNotFound() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        service.addBinding(new JvmBinding());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory, sofaRuntimeContext);

        assertThatThrownBy(() -> serviceComponent.unregister()).isInstanceOf(ServiceRuntimeException.class)
                .hasMessageContaining("01-00001");
    }

    @Test
    void unRegisterWhenBindingFail() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        service.addBinding(new JvmBinding());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory, sofaRuntimeContext);

        when(bindingAdapterFactory.getBindingAdapter(any())).thenReturn(new MockBindingAdapter(true));

        assertThatThrownBy(() -> serviceComponent.unregister()).isInstanceOf(ServiceRuntimeException.class)
                .hasMessageContaining("01-00009");
    }

    @Test
    void unRegisterSuccess() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        service.addBinding(new JvmBinding());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory,
            sofaRuntimeContext);

        when(bindingAdapterFactory.getBindingAdapter(any())).thenReturn(
            new MockBindingAdapter(false));
        serviceComponent.unregister();
        verify(bindingAdapterFactory, times(1)).getBindingAdapter(any());
    }

    @Test
    void dumpBindings() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        service.addBinding(new JvmBinding());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory,
            sofaRuntimeContext);

        assertThat(serviceComponent.dump()).isEqualTo(
            "service:com.alipay.sofa.runtime.sample.SampleService\n" + "|------>[binding]-[jvm]");
    }

    @Test
    void healthSuccessWhenNoException() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        service.addBinding(new JvmBinding());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory,
            sofaRuntimeContext);

        HealthResult healthResult = serviceComponent.isHealthy();
        assertThat(healthResult.isHealthy()).isTrue();
        assertThat(healthResult.getHealthReport()).isEqualTo("[jvm,passed]");
    }

    @Test
    void healthSuccessWhenHaveException() {
        service = new ServiceImpl("", SampleService.class, new SampleServiceImpl());
        service.addBinding(new JvmBinding());
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory,
            sofaRuntimeContext);
        serviceComponent.exception(new RuntimeException("error"));

        HealthResult healthResult = serviceComponent.isHealthy();
        assertThat(healthResult.isHealthy()).isFalse();
        assertThat(healthResult.getHealthReport()).isEqualTo("[jvm,passed] [error]");
    }

    @Test
    void canBeDuplicate() {
        properties.setServiceCanBeDuplicate(true);
        serviceComponent = new ServiceComponent(implementation, service, bindingAdapterFactory,
            sofaRuntimeContext);
        assertThat(serviceComponent.canBeDuplicate()).isTrue();
    }

    static class SampleServiceImpl implements SampleService {

        @Override
        public String service() {
            return "service";
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
            return new Object();
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
            return null;
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

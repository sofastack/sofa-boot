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
package com.alipay.sofa.runtime.service.binding;

import com.alipay.sofa.runtime.filter.JvmFilter;
import com.alipay.sofa.runtime.filter.JvmFilterContext;
import com.alipay.sofa.runtime.filter.JvmFilterHolder;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.sample.SampleService;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.DefaultDynamicServiceProxyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.boot.test.context.FilteredClassLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link JvmServiceInvoker}.
 *
 * @author huzijie
 * @version JvmServiceInvokerTests.java, v 0.1 2023年04月10日 7:30 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class JvmServiceInvokerTests {

    private final ClassLoader   mockClassLoader = new FilteredClassLoader("");

    private final ReferenceImpl reference       = new ReferenceImpl("", SampleService.class,
                                                    InterfaceMode.api, true);

    @Mock
    private JvmBinding          jvmBinding;

    @Mock
    private ServiceComponent    serviceComponent;

    @Mock
    private Implementation      implementation;

    @Mock
    private SofaRuntimeContext  sofaRuntimeContext;

    @Mock
    private ComponentManager    componentManager;

    private JvmFilterHolder     jvmFilterHolder = new JvmFilterHolder();

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void useJvmFilter() {
        SampleService sampleService = createSampleServiceProxy(mockClassLoader, true);
        registerService();
        registerJvmFilter();
        jvmFilterHolder.addJvmFilter(new TestJvmFilter(false));

        assertThat(sampleService.service()).isNull();

        jvmFilterHolder.clearJvmFilters();
        jvmFilterHolder.addJvmFilter(new TestJvmFilter(true));

        assertThat(sampleService.service()).isEqualTo("hello");
    }

    @Test
    public void callSuccessWhenRegisterService() {
        SampleService sampleService = createSampleServiceProxy(mockClassLoader, false);
        registerService();

        assertThat(sampleService.service()).isEqualTo("hello");
    }

    @Test
    public void callSuccessWhenHasBackRegisterService() {
        SampleService sampleService = createSampleServiceProxy(mockClassLoader, false);
        registerNoService();
        when(jvmBinding.getBackupProxy()).thenReturn(new RealSampleServiceImpl("back"));

        assertThat(sampleService.service()).isEqualTo("back");
    }

    @Test
    public void callSuccessWhenHasProxyRegisterService() {
        SampleService sampleService = createSampleServiceProxy(mockClassLoader, false);
        registerProxyService();
        when(jvmBinding.hasBackupProxy()).thenReturn(true);
        when(jvmBinding.getBackupProxy()).thenReturn(new RealSampleServiceImpl("back"));

        assertThat(sampleService.service()).isEqualTo("back");
    }

    @Test
    public void callFailWhenNoRegisterService() {
        SampleService sampleService = createSampleServiceProxy(mockClassLoader, false);
        registerNoService();

        assertThatThrownBy(sampleService::service).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("01-00400");
    }

    private SampleService createSampleServiceProxy(ClassLoader classLoader, boolean jvmFilterEnable) {
        when(sofaRuntimeContext.getComponentManager()).thenReturn(componentManager);
        when(sofaRuntimeContext.getAppClassLoader()).thenReturn(classLoader);
        SofaRuntimeContext.Properties properties = mock(SofaRuntimeContext.Properties.class);
        when(sofaRuntimeContext.getProperties()).thenReturn(properties);
        when(properties.isJvmFilterEnable()).thenReturn(jvmFilterEnable);
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(SampleService.class);
        JvmServiceInvoker jvmServiceInvoker = new JvmServiceInvoker(reference, jvmBinding,
            sofaRuntimeContext);
        proxyFactory.addAdvice(jvmServiceInvoker);
        return (SampleService) proxyFactory.getProxy();
    }

    private void registerJvmFilter() {
        when(sofaRuntimeContext.getJvmFilterHolder()).thenReturn(jvmFilterHolder);
    }

    private void registerService() {
        RealSampleServiceImpl realSampleService = new RealSampleServiceImpl();
        when(componentManager.getComponentInfo(any())).thenReturn(serviceComponent);
        when(serviceComponent.getImplementation()).thenReturn(implementation);
        when(implementation.getTarget()).thenReturn(realSampleService);
    }

    private void registerProxyService() {
        RealSampleServiceImpl realSampleService = new RealSampleServiceImpl("prxoy");
        when(componentManager.getComponentInfo(any())).thenReturn(serviceComponent);
        when(serviceComponent.getImplementation()).thenReturn(implementation);
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(SampleService.class);
        proxyFactory.setTarget(realSampleService);
        when(implementation.getTarget()).thenReturn(proxyFactory.getProxy());
    }

    private void registerNoService() {
        when(sofaRuntimeContext.getServiceProxyManager()).thenReturn(
            new DefaultDynamicServiceProxyManager());
    }

    static class RealSampleServiceImpl implements SampleService {

        private final String name;

        public RealSampleServiceImpl() {
            this("hello");
        }

        public RealSampleServiceImpl(String name) {
            this.name = name;
        }

        @Override
        public String service() {
            return name;
        }
    }

    static class TestJvmFilter implements JvmFilter {

        private final boolean skipBefore;

        public TestJvmFilter(boolean skipBefore) {
            this.skipBefore = skipBefore;
        }

        @Override
        public boolean before(JvmFilterContext context) {
            return skipBefore;
        }

        @Override
        public boolean after(JvmFilterContext context) {
            return false;
        }

        @Override
        public int getOrder() {
            return 0;
        }
    }
}

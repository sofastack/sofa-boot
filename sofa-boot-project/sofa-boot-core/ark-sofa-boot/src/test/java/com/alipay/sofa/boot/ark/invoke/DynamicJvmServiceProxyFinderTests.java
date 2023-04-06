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
package com.alipay.sofa.boot.ark.invoke;

import com.alipay.sofa.ark.api.ArkClient;
import com.alipay.sofa.ark.spi.model.BizState;
import com.alipay.sofa.ark.spi.replay.ReplayContext;
import com.alipay.sofa.boot.ark.MockBiz;
import com.alipay.sofa.boot.ark.MockBizManagerService;
import com.alipay.sofa.boot.ark.SofaRuntimeContainer;
import com.alipay.sofa.boot.ark.sample.SampleService;
import com.alipay.sofa.boot.ark.sample.SampleServiceImpl;
import com.alipay.sofa.runtime.impl.StandardSofaRuntimeManager;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.binding.JvmBindingParam;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import com.alipay.sofa.runtime.spi.component.DefaultImplementation;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.FilteredClassLoader;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DynamicJvmServiceProxyFinder}.
 *
 * @author huzijie
 * @version DynamicJvmServiceProxyFinderTests.java, v 0.1 2023年04月06日 2:19 PM huzijie Exp $
 */
public class DynamicJvmServiceProxyFinderTests {

    private final DynamicJvmServiceProxyFinder dynamicJvmServiceProxyFinder = DynamicJvmServiceProxyFinder
                                                                                .getInstance();

    private final MockBizManagerService        mockBizManagerService        = new MockBizManagerService();

    private final MockBiz                      mockBiz                      = new MockBiz();

    private final ClassLoader                  appClassLoader               = ClassLoader
                                                                                .getSystemClassLoader();

    private final ClassLoader                  mockClassloader              = new FilteredClassLoader(
                                                                                "test");

    private final ReferenceImpl                contract                     = new ReferenceImpl(
                                                                                "test",
                                                                                SampleService.class,
                                                                                InterfaceMode.api,
                                                                                true);

    private final SofaRuntimeManager           sofaRuntimeManager           = new StandardSofaRuntimeManager(
                                                                                "test",
                                                                                appClassLoader,
                                                                                null);

    private final SampleServiceImpl            sampleServiceImpl            = new SampleServiceImpl();

    @BeforeEach
    public void init() {
        ArkClient.setBizManagerService(mockBizManagerService);
        mockBizManagerService.registerBiz(mockBiz);
        dynamicJvmServiceProxyFinder.setBizManagerService(mockBizManagerService);
    }

    @AfterEach
    public void clearBiz() {
        if (ReplayContext.get() != null) {
            ReplayContext.unset();
        }
        dynamicJvmServiceProxyFinder.setHasFinishStartup(false);
        mockBizManagerService.clear();
        SofaRuntimeContainer.clear();
    }

    @AfterAll
    public static void clear() {
        DynamicJvmServiceProxyFinder.getInstance().setBizManagerService(null);
    }

    @Test
    public void findServiceComponentWhenNoSofaRuntimeManager() {
        assertThat(dynamicJvmServiceProxyFinder.findServiceComponent(appClassLoader, contract))
            .isNull();
    }

    @Test
    public void findServiceComponentWhenClassLoaderEqual() {
        registerSofaRuntimeContainer();
        assertThat(dynamicJvmServiceProxyFinder.findServiceComponent(appClassLoader, contract))
            .isNull();
    }

    @Test
    public void findServiceComponentWhenNoBizClassloaderMatch() {
        registerSofaRuntimeContainer();
        mockBizManagerService.clear();
        assertThat(dynamicJvmServiceProxyFinder.findServiceComponent(mockClassloader, contract))
            .isNull();
    }

    @Test
    public void findServiceComponentWhenNoStatMatch() {
        registerSofaRuntimeContainer();
        dynamicJvmServiceProxyFinder.setHasFinishStartup(true);
        assertThat(dynamicJvmServiceProxyFinder.findServiceComponent(mockClassloader, contract))
            .isNull();
    }

    @Test
    public void findServiceComponentWhenVersionNoMatch() {
        registerSofaRuntimeContainer();
        dynamicJvmServiceProxyFinder.setHasFinishStartup(true);
        mockBiz.setBizState(BizState.ACTIVATED);
        ReplayContext.set("version1");
        mockBiz.setBizVersion("version2");
        assertThat(dynamicJvmServiceProxyFinder.findServiceComponent(mockClassloader, contract))
            .isNull();

        mockBiz.setBizVersion(null);
    }

    @Test
    public void findServiceComponentWhenSpecificVersionNoMatch() {
        registerSofaRuntimeContainer();
        dynamicJvmServiceProxyFinder.setHasFinishStartup(true);
        mockBiz.setBizState(BizState.RESOLVED);
        assertThat(dynamicJvmServiceProxyFinder.findServiceComponent(mockClassloader, contract))
            .isNull();
    }

    @Test
    public void findNoServiceComponent() {
        registerSofaRuntimeContainer();
        dynamicJvmServiceProxyFinder.setHasFinishStartup(true);
        mockBiz.setBizState(BizState.ACTIVATED);
        assertThat(dynamicJvmServiceProxyFinder.findServiceComponent(mockClassloader, contract))
            .isNull();
    }

    @Test
    public void findServiceComponent() {
        ServiceComponent serviceComponent = initServiceComponentFind();
        assertThat(dynamicJvmServiceProxyFinder.findServiceComponent(mockClassloader, contract))
            .isEqualTo(serviceComponent);
    }

    @Test
    public void findServiceComponentByCache() {
        registerSofaRuntimeContainer().setJvmServiceCache(true);
        registerSofaRuntimeContainer(mockClassloader).setJvmServiceCache(true);
        dynamicJvmServiceProxyFinder.setHasFinishStartup(true);
        ServiceComponent serviceComponent = initServiceComponent();
        sofaRuntimeManager.getComponentManager().register(serviceComponent);
        mockBiz.setBizVersion("test");

        // register cache
        dynamicJvmServiceProxyFinder.afterBizStartup(mockBiz);

        ReplayContext.set("test");
        assertThat(dynamicJvmServiceProxyFinder.findServiceComponent(mockClassloader, contract))
            .isEqualTo(serviceComponent);

        ReplayContext.set(null);
        assertThat(dynamicJvmServiceProxyFinder.findServiceComponent(mockClassloader, contract))
            .isEqualTo(serviceComponent);

        dynamicJvmServiceProxyFinder.afterBizUninstall(mockBiz);
        assertThat(dynamicJvmServiceProxyFinder.findServiceComponent(mockClassloader, contract))
            .isNull();
    }

    @Test
    public void findServiceProxy() {
        // find ServiceComponent null
        assertThat(dynamicJvmServiceProxyFinder.findServiceProxy(mockClassloader, contract))
            .isNull();

        ServiceComponent serviceComponent = initServiceComponentFind();
        assertThat(dynamicJvmServiceProxyFinder.findServiceComponent(mockClassloader, contract))
            .isEqualTo(serviceComponent);

        dynamicJvmServiceProxyFinder.setBizManagerService(null);
        assertThat(dynamicJvmServiceProxyFinder.findServiceProxy(mockClassloader, contract))
            .isNull();

        dynamicJvmServiceProxyFinder.setBizManagerService(mockBizManagerService);
        assertThat(dynamicJvmServiceProxyFinder.findServiceProxy(mockClassloader, contract))
            .isNotNull();
    }

    @Test
    public void createInvokerNoJvmBinding() {
        ServiceComponent serviceComponent = initServiceComponent();
        DynamicJvmServiceInvoker dynamicJvmServiceInvoker = dynamicJvmServiceProxyFinder
            .createDynamicJvmServiceInvoker(mockClassloader, contract, serviceComponent,
                sofaRuntimeManager, mockBiz);
        assertThat(dynamicJvmServiceInvoker.isSerialize()).isTrue();
    }

    @Test
    public void createInvokerJvmBindingNoSerialize() {
        ServiceComponent serviceComponent = initServiceComponent();
        ReferenceImpl reference = new ReferenceImpl("test", SampleService.class, InterfaceMode.api,
            true);
        JvmBinding jvmBinding = new JvmBinding();
        JvmBindingParam jvmBindingParam = new JvmBindingParam();
        jvmBindingParam.setSerialize(false);
        jvmBinding.setJvmBindingParam(jvmBindingParam);
        serviceComponent.getService().addBinding(jvmBinding);
        reference.addBinding(jvmBinding);
        DynamicJvmServiceInvoker dynamicJvmServiceInvoker = dynamicJvmServiceProxyFinder
            .createDynamicJvmServiceInvoker(mockClassloader, reference, serviceComponent,
                sofaRuntimeManager, mockBiz);
        assertThat(dynamicJvmServiceInvoker.isSerialize()).isFalse();
    }

    @Test
    public void createInvokerWithSerializeProperties() {
        registerSofaRuntimeContainer(mockClassloader).setJvmInvokeSerialize(false);
        ServiceComponent serviceComponent = initServiceComponent();
        DynamicJvmServiceInvoker dynamicJvmServiceInvoker = dynamicJvmServiceProxyFinder
            .createDynamicJvmServiceInvoker(mockClassloader, contract, serviceComponent,
                sofaRuntimeManager, mockBiz);
        assertThat(dynamicJvmServiceInvoker.isSerialize()).isFalse();
    }

    private SofaRuntimeContainer registerSofaRuntimeContainer() {
        return new SofaRuntimeContainer(sofaRuntimeManager);
    }

    private SofaRuntimeContainer registerSofaRuntimeContainer(ClassLoader classLoader) {
        return new SofaRuntimeContainer(sofaRuntimeManager, classLoader);
    }

    private ServiceComponent initServiceComponentFind() {
        ServiceComponent serviceComponent = initServiceComponent();
        sofaRuntimeManager.getComponentManager().register(serviceComponent);
        registerSofaRuntimeContainer();
        dynamicJvmServiceProxyFinder.setHasFinishStartup(true);
        mockBiz.setBizState(BizState.ACTIVATED);
        return serviceComponent;
    }

    private ServiceComponent initServiceComponent() {
        SofaRuntimeContext sofaRuntimeContext = new SofaRuntimeContext(sofaRuntimeManager);
        Implementation implementation = new DefaultImplementation();
        implementation.setTarget(sampleServiceImpl);
        Service service = new ServiceImpl("test", SampleService.class, sampleServiceImpl);
        return new ServiceComponent(implementation, service, null, sofaRuntimeContext);
    }
}

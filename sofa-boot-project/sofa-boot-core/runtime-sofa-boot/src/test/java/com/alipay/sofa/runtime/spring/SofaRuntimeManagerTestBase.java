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
package com.alipay.sofa.runtime.spring;

import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.impl.ClientFactoryImpl;
import com.alipay.sofa.runtime.impl.StandardSofaRuntimeManager;
import com.alipay.sofa.runtime.service.client.ReferenceClientImpl;
import com.alipay.sofa.runtime.service.client.ServiceClientImpl;
import com.alipay.sofa.runtime.service.impl.BindingAdapterFactoryImpl;
import com.alipay.sofa.runtime.service.impl.BindingConverterFactoryImpl;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.HashSet;

/**
 * @author huzijie
 * @version SofaRuntimeManagerTestBase.java, v 0.1 2023年02月22日 11:15 AM huzijie Exp $
 */
public abstract class SofaRuntimeManagerTestBase {

    protected ClientFactoryInternal   clientFactoryInternal;

    protected BindingConverterFactory bindingConverterFactory;

    protected BindingAdapterFactory   bindingAdapterFactory;

    protected SofaRuntimeManager      sofaRuntimeManager;

    protected SofaRuntimeContext      sofaRuntimeContext;

    protected ComponentManager        componentManager;

    @BeforeEach
    public void init() {
        clientFactoryInternal = new ClientFactoryImpl();
        bindingConverterFactory = new BindingConverterFactoryImpl();
        bindingConverterFactory.addBindingConverters(new HashSet<>(SpringFactoriesLoader
            .loadFactories(BindingConverter.class, null)));
        bindingAdapterFactory = new BindingAdapterFactoryImpl();
        bindingAdapterFactory.addBindingAdapters(new HashSet<>(SpringFactoriesLoader.loadFactories(
            BindingAdapter.class, null)));
        sofaRuntimeManager = new StandardSofaRuntimeManager("testApp", Thread.currentThread()
            .getContextClassLoader(), clientFactoryInternal);
        clientFactoryInternal.registerClient(ReferenceClient.class, new ReferenceClientImpl(
            sofaRuntimeManager.getSofaRuntimeContext(), bindingConverterFactory,
            bindingAdapterFactory));
        clientFactoryInternal.registerClient(ServiceClient.class, new ServiceClientImpl(
            sofaRuntimeManager.getSofaRuntimeContext(), bindingConverterFactory,
            bindingAdapterFactory));
        sofaRuntimeContext = sofaRuntimeManager.getSofaRuntimeContext();
        componentManager = sofaRuntimeManager.getComponentManager();
    }
}

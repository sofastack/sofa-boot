package com.alipay.sofa.runtime.spring;

import com.alipay.sofa.boot.util.ServiceLoaderUtils;
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

/**
 * @author huzijie
 * @version SofaRuntimeManagerTestBase.java, v 0.1 2023年02月22日 11:15 AM huzijie Exp $
 */
public abstract class SofaRuntimeManagerTestBase {

    protected ClientFactoryInternal clientFactoryInternal;

    protected BindingConverterFactory bindingConverterFactory;

    protected BindingAdapterFactory bindingAdapterFactory;

    protected SofaRuntimeManager sofaRuntimeManager;

    protected SofaRuntimeContext sofaRuntimeContext;

    protected ComponentManager componentManager;

    @BeforeEach
    public void init() {
        clientFactoryInternal = new ClientFactoryImpl();
        bindingConverterFactory = new BindingConverterFactoryImpl();
        bindingConverterFactory.addBindingConverters(ServiceLoaderUtils
                .getClassesByServiceLoader(BindingConverter.class));
        bindingAdapterFactory = new BindingAdapterFactoryImpl();
        bindingAdapterFactory.addBindingAdapters(ServiceLoaderUtils
                .getClassesByServiceLoader(BindingAdapter.class));
        sofaRuntimeManager = new StandardSofaRuntimeManager(
                "testApp", Thread.currentThread()
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

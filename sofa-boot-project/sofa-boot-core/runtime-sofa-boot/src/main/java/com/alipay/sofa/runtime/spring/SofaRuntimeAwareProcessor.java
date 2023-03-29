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

import com.alipay.sofa.runtime.SofaRuntimeUtils;
import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.aware.ExtensionClientAware;
import com.alipay.sofa.runtime.ext.client.ExtensionClientImpl;
import com.alipay.sofa.runtime.filter.JvmFilter;
import com.alipay.sofa.runtime.filter.JvmFilterHolder;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.spring.RuntimeShutdownAware;
import com.alipay.sofa.runtime.spi.spring.SofaRuntimeContextAware;
import com.alipay.sofa.runtime.spring.singleton.SingletonSofaPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;

/**
 * @author yuanxuan
 * @version : SofaRuntimeAwareProcessor.java, v 0.1 2023年03月28日 15:52 yuanxuan Exp $
 */
@SingletonSofaPostProcessor
public class SofaRuntimeAwareProcessor implements BeanPostProcessor, PriorityOrdered {

    private final SofaRuntimeManager    sofaRuntimeManager;

    private final SofaRuntimeContext    sofaRuntimeContext;

    private final ClientFactoryInternal clientFactory;

    private final ExtensionClientImpl   extensionClient;

    public SofaRuntimeAwareProcessor(SofaRuntimeManager sofaRuntimeManager) {
        this.sofaRuntimeManager = sofaRuntimeManager;
        this.sofaRuntimeContext = sofaRuntimeManager.getSofaRuntimeContext();
        this.clientFactory = sofaRuntimeManager.getClientFactoryInternal();
        this.extensionClient = new ExtensionClientImpl(sofaRuntimeContext);
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
                                                                               throws BeansException {
        if (bean instanceof SofaRuntimeContextAware) {
            ((SofaRuntimeContextAware) bean).setSofaRuntimeContext(sofaRuntimeContext);
        }
        if (bean instanceof ClientFactoryAware) {
            ((ClientFactoryAware) bean).setClientFactory(clientFactory);
        }
        if (bean instanceof ExtensionClientAware) {
            ((ExtensionClientAware) bean).setExtensionClient(extensionClient);
        }
        if (!SofaRuntimeUtils.isArkEnvironment() || SofaRuntimeUtils.onMasterBiz()) {
            if (bean instanceof JvmFilter) {
                JvmFilterHolder.addJvmFilter((JvmFilter) bean);
            }
        }
        if (bean instanceof RuntimeShutdownAware) {
            sofaRuntimeManager.registerShutdownAware((RuntimeShutdownAware) bean);
        }
        return bean;
    }
}

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
package com.alipay.sofa.runtime.test.beans.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.spring.RuntimeShutdownAware;
import com.alipay.sofa.runtime.spi.spring.SofaRuntimeContextAware;
import com.alipay.sofa.runtime.test.beans.facade.SampleService;

/**
 * @author ruoshan
 * @since 2.6.0
 */
public class HighOrderBeanPostProcessor implements BeanPostProcessor, PriorityOrdered,
                                       SofaRuntimeContextAware, ClientFactoryAware,
                                       RuntimeShutdownAware {

    private SofaRuntimeContext sofaRuntimeContext;

    private ClientFactory      clientFactory;

    @SofaReference
    private SampleService      sampleService;

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public void setSofaRuntimeContext(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    public SofaRuntimeContext getSofaRuntimeContext() {
        return sofaRuntimeContext;
    }

    public ClientFactory getClientFactory() {
        return clientFactory;
    }

    public SampleService getSampleService() {
        return sampleService;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
                                                                               throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
                                                                              throws BeansException {
        return bean;
    }

    @Override
    public void shutdown() {
    }

}
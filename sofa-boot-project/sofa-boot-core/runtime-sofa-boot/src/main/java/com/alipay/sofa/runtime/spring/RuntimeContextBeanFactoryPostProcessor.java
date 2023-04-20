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

import com.alipay.sofa.boot.context.processor.SingletonSofaPostProcessor;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

/**
 * Implementation of {@link BeanFactoryPostProcessor} to register BeanPostProcessors.
 *
 * @author huzijie
 * @version RuntimeContextBeanFactoryPostProcessor.java, v 0.1 2023年04月19日 4:16 PM huzijie Exp $
 */
@SingletonSofaPostProcessor
public class RuntimeContextBeanFactoryPostProcessor implements BeanFactoryPostProcessor,
                                                   ApplicationContextAware, InitializingBean,
                                                   Ordered {

    protected BindingAdapterFactory                    bindingAdapterFactory;

    protected BindingConverterFactory                  bindingConverterFactory;

    protected SofaRuntimeManager                       sofaRuntimeManager;

    protected ApplicationContext                       applicationContext;

    protected SofaRuntimeAwareProcessor                sofaRuntimeAwareProcessor;

    protected ClientFactoryAnnotationBeanPostProcessor clientFactoryAnnotationBeanPostProcessor;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                                                                                   throws BeansException {
        // make sure those BeanPostProcessor work on other BeanPostProcessor
        beanFactory.addBeanPostProcessor(sofaRuntimeAwareProcessor);
        beanFactory.addBeanPostProcessor(clientFactoryAnnotationBeanPostProcessor);

        // none singleton bean
        ReferenceAnnotationBeanPostProcessor referenceAnnotationBeanPostProcessor = new ReferenceAnnotationBeanPostProcessor(
            sofaRuntimeManager.getSofaRuntimeContext(), bindingAdapterFactory,
            bindingConverterFactory);
        referenceAnnotationBeanPostProcessor.setApplicationContext(applicationContext);
        beanFactory.addBeanPostProcessor(referenceAnnotationBeanPostProcessor);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.bindingAdapterFactory = applicationContext.getBean("bindingAdapterFactory",
            BindingAdapterFactory.class);
        this.bindingConverterFactory = applicationContext.getBean("bindingConverterFactory",
            BindingConverterFactory.class);
        this.sofaRuntimeManager = applicationContext.getBean("sofaRuntimeManager",
            SofaRuntimeManager.class);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.sofaRuntimeAwareProcessor = new SofaRuntimeAwareProcessor(sofaRuntimeManager);
        this.clientFactoryAnnotationBeanPostProcessor = new ClientFactoryAnnotationBeanPostProcessor(
            sofaRuntimeManager.getClientFactoryInternal());
    }
}

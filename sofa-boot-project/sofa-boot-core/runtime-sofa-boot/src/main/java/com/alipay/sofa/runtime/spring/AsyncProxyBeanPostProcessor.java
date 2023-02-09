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

import com.alipay.sofa.runtime.async.AsyncInitMethodManager;
import com.alipay.sofa.runtime.async.AsyncInitializeBeanMethodInvoker;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.StringUtils;

import static com.alipay.sofa.runtime.async.AsyncInitMethodManager.ASYNC_INIT_METHOD_NAME;

/**
 * Implementation of {@link BeanPostProcessor} to async init methods for specific beans.
 *
 * @author qilong.zql
 * @author xuanbei
 * @since 2.6.0
 */
public class AsyncProxyBeanPostProcessor implements BeanPostProcessor, InitializingBean,
                                        BeanFactoryAware, Ordered {

    private final AsyncInitMethodManager    asyncInitMethodManager;

    private ConfigurableListableBeanFactory beanFactory;

    public AsyncProxyBeanPostProcessor(AsyncInitMethodManager asyncInitMethodManager) {
        this.asyncInitMethodManager = asyncInitMethodManager;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
                                                                               throws BeansException {
        String methodName = asyncInitMethodManager.findAsyncInitMethod(beanFactory, beanName);
        if (!StringUtils.hasText(methodName)) {
            return bean;
        }

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetClass(bean.getClass());
        proxyFactory.setProxyTargetClass(true);
        AsyncInitializeBeanMethodInvoker asyncInitializeBeanMethodInvoker = new AsyncInitializeBeanMethodInvoker(
            asyncInitMethodManager, bean, beanName, methodName);
        proxyFactory.addAdvice(asyncInitializeBeanMethodInvoker);
        return proxyFactory.getProxy();
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            String asyncInitMethodName = (String) beanDefinition
                .getAttribute(ASYNC_INIT_METHOD_NAME);
            if (StringUtils.hasText(asyncInitMethodName)) {
                asyncInitMethodManager.registerAsyncInitBean(beanFactory, beanName,
                    asyncInitMethodName);
            }
        }
    }

}

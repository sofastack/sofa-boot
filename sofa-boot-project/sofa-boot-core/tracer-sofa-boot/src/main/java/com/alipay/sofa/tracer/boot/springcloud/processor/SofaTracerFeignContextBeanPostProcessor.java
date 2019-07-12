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
package com.alipay.sofa.tracer.boot.springcloud.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.openfeign.FeignContext;

import com.alipay.sofa.tracer.plugins.springcloud.instruments.feign.SofaTracerFeignContext;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/3/13 6:08 PM
 * @since:
 **/
public class SofaTracerFeignContextBeanPostProcessor implements BeanPostProcessor {

    private BeanFactory beanFactory;

    public SofaTracerFeignContextBeanPostProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
                                                                               throws BeansException {
        if (bean instanceof FeignContext && !(bean instanceof SofaTracerFeignContext)) {
            return new SofaTracerFeignContext((FeignContext) bean, beanFactory);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
                                                                              throws BeansException {
        return bean;
    }
}

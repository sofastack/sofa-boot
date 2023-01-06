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
package com.alipay.sofa.boot.tracer.springmessage;

import com.alipay.sofa.tracer.plugins.message.interceptor.SofaTracerChannelInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.integration.channel.AbstractMessageChannel;

/**
 * @author guolei.sgl (guolei.sgl@antfin.com) 2019/12/4 11:07 AM
 * @since 3.9.1
 **/
public class SpringMessageTracerBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {

    private final String appName;

    public SpringMessageTracerBeanPostProcessor(String appName) {
        this.appName = appName;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
                                                                               throws BeansException {
        if (bean instanceof AbstractMessageChannel) {
            ((AbstractMessageChannel) bean).addInterceptor(SofaTracerChannelInterceptor.create(appName));
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}

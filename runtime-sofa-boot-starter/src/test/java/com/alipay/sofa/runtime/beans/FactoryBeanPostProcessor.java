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
package com.alipay.sofa.runtime.beans;

import com.alipay.sofa.runtime.spring.configuration.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.runtime.spring.factory.ReferenceFactoryBean;
import com.alipay.sofa.runtime.util.StateMessage;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;

/**
 * @author qilong.zql
 * @since 2.4.1
 */
public class FactoryBeanPostProcessor extends AbstractAutoProxyCreator {
    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName,
                                                    TargetSource customTargetSource)
                                                                                    throws BeansException {
        return new Object[0];
    }

    @Override
    protected Object wrapIfNecessary(final Object bean, String beanName, Object cacheKey) {
        if (bean instanceof ReferenceFactoryBean) {
            StateMessage.setFactoryMessage("aop");
        }
        if (bean instanceof SofaRuntimeAutoConfiguration) {
            StateMessage.setConfigMessage("config");
        }
        return bean;
    }
}
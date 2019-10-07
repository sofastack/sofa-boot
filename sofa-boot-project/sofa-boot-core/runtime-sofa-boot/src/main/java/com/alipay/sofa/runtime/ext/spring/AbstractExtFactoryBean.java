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
package com.alipay.sofa.runtime.ext.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.Ordered;

/**
 * Common bean for extension and extension point
 *
 * @author yangyanzhao@alipay.com
 * @since 2.6.0
 */
public class AbstractExtFactoryBean extends CommonContextBean implements BeanFactoryAware,
                                                             FactoryBean, Ordered {
    /* Spring bean context for looking up spring's bean */
    protected BeanFactory      beanFactory;

    protected String           targetBeanName;

    protected Object           target;

    public final static String LINK_SYMBOL = "$";

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;

    }

    /**
     * no real bean exist
     * @return null
     * @throws Exception any exception
     */
    public Object getObject() throws Exception {
        return null;
    }

    /**
     * no real bean exist
     * @return String.class
     */
    public Class<?> getObjectType() {
        return String.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * Export the given object as an OSGi service. Normally used when the
     * exported service is a nested bean or an object not managed by the Spring
     * container.
     *
     * @param target The target to set.
     */
    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTargetBeanName(String name) {
        this.targetBeanName = name;
    }
}

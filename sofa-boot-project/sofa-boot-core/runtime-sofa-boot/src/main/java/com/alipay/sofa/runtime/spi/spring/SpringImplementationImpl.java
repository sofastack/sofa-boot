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
package com.alipay.sofa.runtime.spi.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import com.alipay.sofa.runtime.spi.component.DefaultImplementation;

/**
 * Spring Component Implement
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */

public class SpringImplementationImpl extends DefaultImplementation {

    protected ApplicationContext applicationContext;
    protected String             beanName;
    protected Object             target;

    public SpringImplementationImpl(String beanName, ApplicationContext applicationContext) {
        Assert.hasText(beanName, "beanName must not be empty");
        Assert.notNull(applicationContext, "applicationContext must not be null");

        this.beanName = beanName;
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getTarget() {
        return applicationContext.getBean(this.beanName);
    }

    @Override
    public Class<?> getTargetClass() {
        return applicationContext.getBean(this.beanName).getClass();
    }

    @Override
    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public String getName() {
        return beanName;
    }

}

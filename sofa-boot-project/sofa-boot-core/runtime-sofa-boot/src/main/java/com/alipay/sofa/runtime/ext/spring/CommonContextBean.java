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
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;

/**
 *
 * @author linfengqi
 * @since 2.6.0
 */
public class CommonContextBean implements ApplicationContextAware, BeanNameAware, InitializingBean {

    protected String                          beanName;
    protected ClassLoaderWrapper              beanClassLoaderWrapper;
    protected ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    protected SofaRuntimeContext              sofaRuntimeContext;
    protected ApplicationContext              applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        // ignore
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        configurableListableBeanFactory = (ConfigurableListableBeanFactory) applicationContext
            .getAutowireCapableBeanFactory();
    }

    @Deprecated
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        throw new UnsupportedOperationException("Not support setBeanClassLoader for security");
    }

    @Deprecated
    public ClassLoader getBeanClassLoader() {
        throw new UnsupportedOperationException("Not support getBeanClassLoader for security");
    }

    public ClassLoaderWrapper getBeanClassLoaderWrapper() {
        return beanClassLoaderWrapper;
    }

    public void setBeanClassLoaderWrapper(ClassLoaderWrapper beanClassLoaderWrapper) {
        this.beanClassLoaderWrapper = beanClassLoaderWrapper;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}

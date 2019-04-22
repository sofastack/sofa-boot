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

import com.alipay.sofa.runtime.constants.SofaRuntimeFrameworkConstants;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author linfengqi
 * @since 2.6.0
 */
public class CommonContextBean implements ApplicationContextAware, BeanNameAware, InitializingBean {

    protected String                          beanName;
    protected ClassLoader                     beanClassLoader;
    protected ConfigurableListableBeanFactory configurableListableBeanFactory;
    protected SofaRuntimeContext              sofaRuntimeContext;
    protected ApplicationContext              applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        sofaRuntimeContext = applicationContext.getBean(
            SofaRuntimeFrameworkConstants.SOFA_RUNTIME_CONTEXT_BEAN_ID, SofaRuntimeContext.class);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        configurableListableBeanFactory = (ConfigurableListableBeanFactory) applicationContext
            .getAutowireCapableBeanFactory();
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    public ClassLoader getBeanClassLoader() {
        return beanClassLoader;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}

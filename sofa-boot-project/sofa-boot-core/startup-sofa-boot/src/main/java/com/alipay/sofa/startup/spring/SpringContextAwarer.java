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
package com.alipay.sofa.startup.spring;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;

/**
 * Support class to aware present bean factory
 *
 * @author: Zhijie
 * @since: 2020/7/10
 */
public class SpringContextAwarer implements BeanFactoryAware, BeanPostProcessor {
    protected BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public long getIsleContextInstallCost() {
        return -1L;
    }

    public String getModuleName() {
        return SofaBootConstants.ROOT_APPLICATION_CONTEXT;
    }

    public ServletWebServerFactory getServletWebServerFactory() {
        try {
            return beanFactory.getBean(ServletWebServerFactory.class);
        } catch (NoSuchBeanDefinitionException beanDefinitionException) {
            //Not web server project
            return null;
        }
    }
}

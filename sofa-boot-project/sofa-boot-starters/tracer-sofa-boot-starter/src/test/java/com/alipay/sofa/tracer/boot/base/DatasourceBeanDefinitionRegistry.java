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
package com.alipay.sofa.tracer.boot.base;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import com.zaxxer.hikari.HikariDataSource;

/**
 * @author qilong.zql
 * @since 2.2.1
 */
public class DatasourceBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
                                                                                  throws BeansException {
        BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder
            .genericBeanDefinition(HikariDataSource.class);
        AbstractBeanDefinition beanDefinition = definitionBuilder.getRawBeanDefinition();
        beanDefinition.setDestroyMethodName("close");
        beanDefinition.setPrimary(false);

        definitionBuilder.addPropertyValue("driverClassName", "org.h2.Driver");
        definitionBuilder
            .addPropertyValue(
                "jdbcUrl",
                "jdbc:mysql://1.1.1.1:3306/xxx?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull");
        definitionBuilder.addPropertyValue("username", "sofa");
        definitionBuilder.addPropertyValue("password", "123456");

        registry.registerBeanDefinition("manualDataSource",
            definitionBuilder.getRawBeanDefinition());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                                                                                   throws BeansException {
        // ignore
    }
}
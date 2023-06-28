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
package com.alipay.sofa.boot.context.processor;

/**
 * Interface to judge bean or class match
 * {@link org.springframework.beans.factory.config.BeanPostProcessor} or
 * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor}
 * should be share or should use singleton when shared.
 *
 * @author huzijie
 * @since 4.0.0
 */
public interface SofaPostProcessorShareFilter {

    /**
     * judge the bean for clazz type should be shared
     * @param clazz type for bean
     * @return if the bean should be shared, return true, otherwise false
     */
    default boolean skipShareByClass(Class<?> clazz) {
        return false;
    }

    /**
     * judge the bean for beanName should be shared
     * @param beanName beanName for bean
     * @return if the bean should be shared, return true, otherwise false
     */
    default boolean skipShareByBeanName(String beanName) {return false;}

    /**
     * judge the bean for clazz type should share singleton
     * @param clazz type for bean
     * @return if the bean should share singleton, return true, otherwise false
     */
    default boolean useSingletonByClass(Class<?> clazz) {
        return false;
    }

    /**
     * judge the bean for clazz type should share singleton
     * @param beanName beanName for bean
     * @return if the bean should share singleton, return true, otherwise false
     */
    default boolean useSingletonByBeanName(String beanName) {return false;}
}

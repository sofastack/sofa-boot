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
package com.alipay.sofa.runtime.spring.bean;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.StringUtils;

import com.alipay.sofa.runtime.spring.parser.AbstractContractDefinitionParser;

/**
 * @author qilong.zql
 * @since 3.1.0
 */
public class SofaBeanNameGenerator {
    private static final String SERVICE_BEAN_NAME_PREFIX   = "ServiceFactoryBean#";
    private static final String REFERENCE_BEAN_NAME_PREFIX = "ReferenceFactoryBean#";

    public static String generateSofaServiceBeanName(BeanDefinition definition) {
        String interfaceName = (String) definition.getPropertyValues().get(
            AbstractContractDefinitionParser.INTERFACE_PROPERTY);
        Class clazz = (Class) definition.getPropertyValues().get(
            AbstractContractDefinitionParser.INTERFACE_CLASS_PROPERTY);
        if (clazz != null) {
            interfaceName = clazz.getCanonicalName();
        }
        String uniqueId = (String) definition.getPropertyValues().get(
            AbstractContractDefinitionParser.UNIQUE_ID_PROPERTY);
        return generateSofaServiceBeanName(interfaceName, uniqueId);
    }

    public static String generateSofaServiceBeanName(Class<?> interfaceType, String uniqueId) {
        return generateSofaServiceBeanName(interfaceType.getCanonicalName(), uniqueId);
    }

    public static String generateSofaServiceBeanName(String interfaceName, String uniqueId) {
        if (StringUtils.isEmpty(uniqueId)) {
            return SERVICE_BEAN_NAME_PREFIX + interfaceName;
        }
        return SERVICE_BEAN_NAME_PREFIX + interfaceName + ":" + uniqueId;
    }

    public static String generateSofaReferenceBeanName(Class<?> interfaceType, String uniqueId) {
        if (StringUtils.isEmpty(uniqueId)) {
            return REFERENCE_BEAN_NAME_PREFIX + interfaceType.getCanonicalName();
        }
        return REFERENCE_BEAN_NAME_PREFIX + interfaceType.getCanonicalName() + ":" + uniqueId;
    }
}
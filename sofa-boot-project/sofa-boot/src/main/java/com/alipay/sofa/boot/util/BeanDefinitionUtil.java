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
package com.alipay.sofa.boot.util;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Utility methods that are useful for bean definition resolve.
 *
 * Created by TomorJM on 2019-10-09.
 */
public class BeanDefinitionUtil {

    /**
     * {@link AnnotatedGenericBeanDefinition}
     * {@link ScannedGenericBeanDefinition}
     * {@link GenericBeanDefinition}
     * {@link org.springframework.beans.factory.support.ChildBeanDefinition}
     * {@link org.springframework.beans.factory.support.RootBeanDefinition}
     *
     * @param beanDefinition resolve bean class type from bean definition
     * @return class for bean definition, could be null.
     */
    public static Class<?> resolveBeanClassType(BeanDefinition beanDefinition) {
        Class<?> clazz = null;

        if (beanDefinition instanceof AnnotatedBeanDefinition) {
            String className;
            if (isFromConfigurationSource(beanDefinition)) {
                MethodMetadata methodMetadata = ((AnnotatedBeanDefinition) beanDefinition)
                    .getFactoryMethodMetadata();
                className = methodMetadata.getReturnTypeName();
            } else {
                AnnotationMetadata annotationMetadata = ((AnnotatedBeanDefinition) beanDefinition)
                    .getMetadata();
                className = annotationMetadata.getClassName();
            }

            try {
                if (StringUtils.hasText(className)) {
                    clazz = ClassUtils.forName(className, null);
                }
            } catch (Throwable throwable) {
                // ignore
            }
        }

        if (clazz == null) {
            try {
                clazz = ((AbstractBeanDefinition) beanDefinition).getBeanClass();
            } catch (IllegalStateException ex) {
                try {
                    String className = beanDefinition.getBeanClassName();
                    if (StringUtils.hasText(className)) {
                        clazz = ClassUtils.forName(className, null);
                    }
                } catch (Throwable throwable) {
                    // ignore
                }
            }
        }

        if (clazz == null) {
            if (beanDefinition instanceof RootBeanDefinition) {
                clazz = ((RootBeanDefinition) beanDefinition).getTargetType();
            }
        }

        if (ClassUtils.isCglibProxyClass(clazz)) {
            return clazz.getSuperclass();
        } else {
            return clazz;
        }
    }

    /**
     * @param beanDefinition Check whether it is a bean definition created from a configuration class
     *                       as opposed to any other configuration source.
     * @return
     */
    public static boolean isFromConfigurationSource(BeanDefinition beanDefinition) {
        return beanDefinition
            .getClass()
            .getCanonicalName()
            .startsWith(
                "org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader");
    }
}

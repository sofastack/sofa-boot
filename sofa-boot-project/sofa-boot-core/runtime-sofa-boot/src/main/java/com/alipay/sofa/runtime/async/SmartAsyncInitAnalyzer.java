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
package com.alipay.sofa.runtime.async;

import com.alipay.sofa.boot.util.BeanDefinitionUtil;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.alipay.sofa.runtime.async.AsyncInitMethodManager.ASYNC_INIT_DISABLED_ATTRIBUTE;
import static com.alipay.sofa.runtime.async.AsyncInitMethodManager.ASYNC_INIT_METHOD_NAME;

/**
 * Analyzer for bean definitions that can safely move their init method to async startup.
 *
 * @author OpenAI
 */
public class SmartAsyncInitAnalyzer {

    private static final Set<String> STATEFUL_ANNOTATION_NAMES = Set
                                                                   .of("javax.ejb.Stateful",
                                                                       "jakarta.ejb.Stateful",
                                                                       "org.springframework.boot.context.properties.ConfigurationProperties",
                                                                       "org.springframework.context.annotation.Configuration");

    /**
     * Analyze bean definitions using conservative mode.
     * @param beanFactory bean factory to inspect
     * @return async init candidate bean names
     */
    public List<String> analyzeAsyncCandidates(ConfigurableListableBeanFactory beanFactory) {
        return analyzeAsyncCandidates(beanFactory, AsyncInitAutoMode.CONSERVATIVE);
    }

    /**
     * Analyze bean definitions using the given auto mode.
     * @param beanFactory bean factory to inspect
     * @param autoMode auto detection mode
     * @return async init candidate bean names
     */
    public List<String> analyzeAsyncCandidates(ConfigurableListableBeanFactory beanFactory,
                                               AsyncInitAutoMode autoMode) {
        List<String> candidates = new ArrayList<>();
        if (autoMode == null || autoMode == AsyncInitAutoMode.OFF) {
            return candidates;
        }
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            if (isAsyncCandidate(beanFactory, beanName, beanDefinition, autoMode)) {
                candidates.add(beanName);
            }
        }
        return candidates;
    }

    /**
     * Check whether a bean definition is safe to initialize asynchronously.
     * @param beanFactory bean factory to inspect
     * @param beanName bean name
     * @param beanDefinition bean definition
     * @param autoMode auto detection mode
     * @return true if the bean can be automatically marked async
     */
    public boolean isAsyncCandidate(ConfigurableListableBeanFactory beanFactory, String beanName,
                                    BeanDefinition beanDefinition, AsyncInitAutoMode autoMode) {
        if (autoMode == null || autoMode == AsyncInitAutoMode.OFF) {
            return false;
        }
        if (!StringUtils.hasText(beanDefinition.getInitMethodName())) {
            return false;
        }
        if (StringUtils.hasText((String) beanDefinition.getAttribute(ASYNC_INIT_METHOD_NAME))) {
            return false;
        }
        if (Boolean.TRUE.equals(beanDefinition.getAttribute(ASYNC_INIT_DISABLED_ATTRIBUTE))) {
            return false;
        }
        if (beanDefinition.isAbstract() || beanDefinition.isLazyInit()
            || !beanDefinition.isSingleton()) {
            return false;
        }
        return isStatelessService(beanDefinition, autoMode)
               && !hasMandatoryDependencies(beanFactory, beanName, beanDefinition);
    }

    private boolean isStatelessService(BeanDefinition beanDefinition, AsyncInitAutoMode autoMode) {
        Class<?> beanClass = BeanDefinitionUtil.resolveBeanClassType(beanDefinition);
        if (beanClass == null || beanClass.isInterface() || beanClass.isAnnotation()) {
            return false;
        }
        if (org.springframework.beans.factory.FactoryBean.class.isAssignableFrom(beanClass)) {
            return false;
        }
        if (hasStatefulAnnotations(beanClass)) {
            return false;
        }
        if (autoMode == AsyncInitAutoMode.CONSERVATIVE) {
            return !hasInstanceFields(beanClass);
        }
        return !hasMutableStateFields(beanClass);
    }

    private boolean hasStatefulAnnotations(Class<?> beanClass) {
        for (Annotation annotation : beanClass.getAnnotations()) {
            if (STATEFUL_ANNOTATION_NAMES.contains(annotation.annotationType().getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasMandatoryDependencies(ConfigurableListableBeanFactory beanFactory,
                                             String beanName, BeanDefinition beanDefinition) {
        if (beanDefinition.getDependsOn() != null && beanDefinition.getDependsOn().length > 0) {
            return true;
        }
        if (StringUtils.hasText(beanDefinition.getFactoryBeanName())) {
            return true;
        }
        ConstructorArgumentValues constructorArgumentValues = beanDefinition
            .getConstructorArgumentValues();
        if (!constructorArgumentValues.isEmpty()) {
            return true;
        }
        for (PropertyValue propertyValue : beanDefinition.getPropertyValues()
            .getPropertyValueList()) {
            if (isBeanReference(propertyValue.getValue())) {
                return true;
            }
        }
        if (beanFactory.getDependenciesForBean(beanName).length > 0) {
            return true;
        }
        Class<?> beanClass = BeanDefinitionUtil.resolveBeanClassType(beanDefinition);
        return beanClass != null && hasMandatoryInjectionPoints(beanClass);
    }

    private boolean isBeanReference(Object value) {
        if (value instanceof RuntimeBeanReference || value instanceof BeanDefinitionHolder
            || value instanceof BeanDefinition) {
            return true;
        }
        if (value instanceof ManagedList<?> list) {
            return list.stream().anyMatch(this::isBeanReference);
        }
        if (value instanceof ManagedSet<?> set) {
            return set.stream().anyMatch(this::isBeanReference);
        }
        if (value instanceof ManagedMap<?, ?> map) {
            return map.entrySet().stream()
                .anyMatch(entry -> isBeanReference(entry.getKey()) || isBeanReference(entry.getValue()));
        }
        return false;
    }

    private boolean hasMandatoryInjectionPoints(Class<?> beanClass) {
        for (Constructor<?> constructor : beanClass.getDeclaredConstructors()) {
            if (constructor.getParameterCount() > 0 && isMandatoryInjectionPoint(constructor)) {
                return true;
            }
        }
        for (Field field : beanClass.getDeclaredFields()) {
            if (isMandatoryInjectionPoint(field)) {
                return true;
            }
        }
        for (Method method : beanClass.getDeclaredMethods()) {
            if (method.getParameterCount() > 0 && isMandatoryInjectionPoint(method)) {
                return true;
            }
        }
        return hasSingleMandatoryConstructor(beanClass);
    }

    private boolean hasSingleMandatoryConstructor(Class<?> beanClass) {
        Constructor<?>[] constructors = beanClass.getDeclaredConstructors();
        if (constructors.length != 1) {
            return false;
        }
        Constructor<?> constructor = constructors[0];
        return constructor.getParameterCount() > 0 && !hasNoArgConstructor(beanClass);
    }

    private boolean hasNoArgConstructor(Class<?> beanClass) {
        for (Constructor<?> constructor : beanClass.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isMandatoryInjectionPoint(Member member) {
        for (Annotation annotation : getAnnotations(member)) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            String annotationName = annotationType.getName();
            if (Autowired.class.getName().equals(annotationName)) {
                return isAutowiredRequired(annotation);
            }
            if ("javax.annotation.Resource".equals(annotationName)
                || "jakarta.annotation.Resource".equals(annotationName)
                || "javax.inject.Inject".equals(annotationName)
                || "jakarta.inject.Inject".equals(annotationName)) {
                return true;
            }
        }
        return false;
    }

    private Annotation[] getAnnotations(Member member) {
        if (member instanceof Field field) {
            return field.getAnnotations();
        }
        if (member instanceof Method method) {
            return method.getAnnotations();
        }
        return ((Constructor<?>) member).getAnnotations();
    }

    private boolean isAutowiredRequired(Annotation annotation) {
        try {
            Method requiredMethod = annotation.annotationType().getMethod("required");
            return Boolean.TRUE.equals(requiredMethod.invoke(annotation));
        } catch (Throwable throwable) {
            return true;
        }
    }

    private boolean hasInstanceFields(Class<?> beanClass) {
        for (Field field : beanClass.getDeclaredFields()) {
            if (!field.isSynthetic() && !Modifier.isStatic(field.getModifiers())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasMutableStateFields(Class<?> beanClass) {
        for (Field field : beanClass.getDeclaredFields()) {
            if (field.isSynthetic() || Modifier.isStatic(field.getModifiers())
                || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            if (!isMandatoryInjectionPoint(field)) {
                return true;
            }
        }
        return false;
    }
}

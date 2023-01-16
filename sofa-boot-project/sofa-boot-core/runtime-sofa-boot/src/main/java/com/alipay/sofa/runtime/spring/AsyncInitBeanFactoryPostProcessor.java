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
package com.alipay.sofa.runtime.spring;

import com.alipay.sofa.boot.annotation.AnnotationWrapper;
import com.alipay.sofa.boot.annotation.DefaultPlaceHolderBinder;
import com.alipay.sofa.boot.context.processor.SingletonSofaPostProcessor;
import com.alipay.sofa.boot.log.ErrorCode;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.boot.util.BeanDefinitionUtil;
import com.alipay.sofa.runtime.api.annotation.SofaAsyncInit;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.alipay.sofa.runtime.async.AsyncInitMethodManager.ASYNC_INIT_METHOD_NAME;

/**
 * Implementation of {@link BeanFactoryPostProcessor} to register async init beans.
 *
 * @author huzijie
 * @version AsyncInitBeanFactoryPostProcessor.java, v 0.1 2022年03月25日 2:08 PM huzijie Exp $
 */
@SingletonSofaPostProcessor
public class AsyncInitBeanFactoryPostProcessor implements BeanFactoryPostProcessor,
                                              EnvironmentAware {

    private static final Logger              LOGGER = SofaBootLoggerFactory
                                                        .getLogger(AsyncInitBeanFactoryPostProcessor.class);

    private AnnotationWrapper<SofaAsyncInit> annotationWrapper;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Arrays.stream(beanFactory.getBeanDefinitionNames())
                .collect(Collectors.toMap(Function.identity(), beanFactory::getBeanDefinition))
                .forEach(this::scanAsyncInitBeanDefinition);
    }

    /**
     * {@link ScannedGenericBeanDefinition}
     * {@link AnnotatedGenericBeanDefinition}
     * {@link GenericBeanDefinition}
     * {@link org.springframework.beans.factory.support.ChildBeanDefinition}
     * {@link org.springframework.beans.factory.support.RootBeanDefinition}
     */
    private void scanAsyncInitBeanDefinition(String beanId, BeanDefinition beanDefinition) {
        if (BeanDefinitionUtil.isFromConfigurationSource(beanDefinition)) {
            scanAsyncInitBeanDefinitionOnMethod(beanId, (AnnotatedBeanDefinition) beanDefinition);
        } else {
            Class<?> beanClassType = BeanDefinitionUtil.resolveBeanClassType(beanDefinition);
            if (beanClassType == null) {
                return;
            }
            scanAsyncInitBeanDefinitionOnClass(beanClassType, beanDefinition);
        }
    }

    private void scanAsyncInitBeanDefinitionOnMethod(String beanId,
                                                     AnnotatedBeanDefinition beanDefinition) {
        Class<?> returnType;
        Class<?> declaringClass;
        List<Method> candidateMethods = new ArrayList<>();

        MethodMetadata methodMetadata = beanDefinition.getFactoryMethodMetadata();
        try {
            returnType = ClassUtils.forName(methodMetadata.getReturnTypeName(), null);
            declaringClass = ClassUtils.forName(methodMetadata.getDeclaringClassName(), null);
        } catch (Throwable throwable) {
            // it's impossible to catch throwable here
            LOGGER.error(ErrorCode.convert("01-02001", beanId), throwable);
            return;
        }
        if (methodMetadata instanceof StandardMethodMetadata) {
            candidateMethods.add(((StandardMethodMetadata) methodMetadata).getIntrospectedMethod());
        } else {
            for (Method m : declaringClass.getDeclaredMethods()) {
                // check methodName and return type
                if (!m.getName().equals(methodMetadata.getMethodName())
                    || !m.getReturnType().getTypeName().equals(methodMetadata.getReturnTypeName())) {
                    continue;
                }

                // check bean method
                if (!AnnotatedElementUtils.hasAnnotation(m, Bean.class)) {
                    continue;
                }

                Bean bean = m.getAnnotation(Bean.class);
                Set<String> beanNames = new HashSet<>();
                beanNames.add(m.getName());
                if (bean != null) {
                    beanNames.addAll(Arrays.asList(bean.name()));
                    beanNames.addAll(Arrays.asList(bean.value()));
                }

                // check bean name
                if (!beanNames.contains(beanId)) {
                    continue;
                }

                candidateMethods.add(m);
            }
        }

        if (candidateMethods.size() == 1) {
            SofaAsyncInit sofaAsyncInitAnnotation = candidateMethods.get(0).getAnnotation(
                SofaAsyncInit.class);
            if (sofaAsyncInitAnnotation == null) {
                sofaAsyncInitAnnotation = returnType.getAnnotation(SofaAsyncInit.class);
            }
            registerAsyncInitBean(sofaAsyncInitAnnotation, beanDefinition);
        } else if (candidateMethods.size() > 1) {
            for (Method m : candidateMethods) {
                if (AnnotatedElementUtils.hasAnnotation(m, SofaAsyncInit.class)
                    || AnnotatedElementUtils.hasAnnotation(returnType, SofaAsyncInit.class)) {
                    throw new FatalBeanException(ErrorCode.convert("01-02002",
                        declaringClass.getCanonicalName()));
                }
            }
        }
    }

    private void scanAsyncInitBeanDefinitionOnClass(Class<?> beanClass,
                                                    BeanDefinition beanDefinition) {
        // See issue: https://github.com/sofastack/sofa-boot/issues/835
        SofaAsyncInit sofaAsyncInitAnnotation = AnnotationUtils.findAnnotation(beanClass,
            SofaAsyncInit.class);
        registerAsyncInitBean(sofaAsyncInitAnnotation, beanDefinition);
    }

    @SuppressWarnings("unchecked")
    private void registerAsyncInitBean(SofaAsyncInit sofaAsyncInitAnnotation,
                                       BeanDefinition beanDefinition) {
        if (sofaAsyncInitAnnotation == null) {
            return;
        }

        sofaAsyncInitAnnotation = annotationWrapper.wrap(sofaAsyncInitAnnotation);

        String initMethodName = beanDefinition.getInitMethodName();
        if (sofaAsyncInitAnnotation.value() && StringUtils.hasText(initMethodName)) {
            beanDefinition.setAttribute(ASYNC_INIT_METHOD_NAME, initMethodName);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.annotationWrapper = AnnotationWrapper.create(SofaAsyncInit.class)
            .withEnvironment(environment).withBinder(DefaultPlaceHolderBinder.INSTANCE);
    }
}

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
package com.alipay.sofa.test.mock.injector.resolver;

import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.IsleDeploymentModel;
import com.alipay.sofa.boot.log.ErrorCode;
import com.alipay.sofa.test.mock.injector.definition.Definition;
import com.alipay.sofa.test.mock.injector.definition.QualifierDefinition;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A resolve used to find inject target bean and create {@link BeanInjectorStub}.
 *
 * @author pengym
 * @version BeanInjectorResolver.java, v 0.1 2023年08月07日 16:58 pengym
 */
public class BeanInjectorResolver {

    private static final String                   ISLE_MARKER_CLASS       = "com.alipay.sofa.boot.isle.ApplicationRuntimeModel";

    private static final boolean                  ISLE_MODEL_EXIST        = ClassUtils.isPresent(
                                                                              ISLE_MARKER_CLASS,
                                                                              null);

    private final ApplicationContext              rootApplicationContext;

    private final Map<String, ApplicationContext> isleApplicationContexts = new LinkedHashMap<>();

    public BeanInjectorResolver(ApplicationContext applicationContext) {
        this.rootApplicationContext = applicationContext;
        if (ISLE_MODEL_EXIST) {
            if (rootApplicationContext
                .containsBean(ApplicationRuntimeModel.APPLICATION_RUNTIME_MODEL_NAME)) {
                IsleDeploymentModel isleDeploymentModel = applicationContext.getBean(
                    ApplicationRuntimeModel.APPLICATION_RUNTIME_MODEL_NAME,
                    IsleDeploymentModel.class);
                isleApplicationContexts
                    .putAll(isleDeploymentModel.getModuleApplicationContextMap());
            }
        }
    }

    public BeanInjectorStub resolveStub(Definition definition) {
        // find target application context
        ApplicationContext applicationContext = getApplicationContext(definition);
        ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) applicationContext
            .getAutowireCapableBeanFactory();

        // find target beanName
        String beanName = getBeanName(beanFactory, definition);

        // find target bean instance
        if (!beanFactory.containsBean(beanName)) {
            throw new IllegalStateException(ErrorCode.convert("01-30005", beanName));
        }
        Object bean = resolveTargetObject(beanFactory.getBean(beanName));

        // inject target bean field
        return injectTargetBeanField(bean, beanName, definition);
    }

    private ApplicationContext getApplicationContext(Definition definition) {
        String module = definition.getModule();
        if (StringUtils.hasText(module)) {
            ApplicationContext applicationContext = isleApplicationContexts.get(module);
            if (applicationContext == null) {
                throw new IllegalStateException(ErrorCode.convert("01-30002", module, definition));
            }
            return applicationContext;
        } else {
            return rootApplicationContext;
        }
    }

    private BeanInjectorStub injectTargetBeanField(Object bean, String beanName,
                                                   Definition definition) {
        String fieldName = definition.getField();
        Field targetField = ReflectionUtils.findField(bean.getClass(), fieldName);

        if (targetField == null) {
            throw new IllegalStateException("Unable to inject target field to bean " + beanName
                                            + ", can not find field " + fieldName + " in "
                                            + bean.getClass());
        }

        BeanInjectorStub beanStubbedField = new BeanInjectorStub(definition, targetField, bean);
        beanStubbedField.inject();
        return beanStubbedField;
    }

    private Object resolveTargetObject(Object obj) {
        if (!AopUtils.isAopProxy(obj) && !AopUtils.isJdkDynamicProxy(obj)) {
            return obj;
        }

        // AopProxy or JdkDynamicProxy
        return AopProxyUtils.getSingletonTarget(obj);
    }

    private String getBeanName(ConfigurableListableBeanFactory beanFactory, Definition definition) {
        if (StringUtils.hasText(definition.getName())) {
            return definition.getName();
        }
        Set<String> existingBeans = getExistingBeans(beanFactory, definition.getType(),
            definition.getQualifier());
        if (existingBeans.isEmpty()) {
            throw new IllegalStateException(ErrorCode.convert("01-30003", definition.getType()));
        }
        if (existingBeans.size() == 1) {
            return existingBeans.iterator().next();
        }
        String primaryCandidate = determinePrimaryCandidate(beanFactory, existingBeans,
            definition.getType());
        if (primaryCandidate != null) {
            return primaryCandidate;
        }
        throw new IllegalStateException(ErrorCode.convert("01-30004", definition.getType(),
            existingBeans));
    }

    private Set<String> getExistingBeans(ConfigurableListableBeanFactory beanFactory,
                                         ResolvableType type, QualifierDefinition qualifier) {
        Set<String> candidates = new TreeSet<>();
        for (String candidate : getExistingBeans(beanFactory, type)) {
            if (qualifier == null || qualifier.matches(beanFactory, candidate)) {
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    private Set<String> getExistingBeans(ConfigurableListableBeanFactory beanFactory, ResolvableType resolvableType) {
        Set<String> beans = new LinkedHashSet<>(
                Arrays.asList(beanFactory.getBeanNamesForType(resolvableType, true, false)));
        Class<?> type = resolvableType.resolve(Object.class);
        String typeName = type.getName();
        for (String beanName : beanFactory.getBeanNamesForType(FactoryBean.class, true, false)) {
            beanName = BeanFactoryUtils.transformedBeanName(beanName);
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            Object attribute = beanDefinition.getAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE);
            if (resolvableType.equals(attribute) || type.equals(attribute) || typeName.equals(attribute)) {
                beans.add(beanName);
            }
        }
        beans.removeIf(this::isScopedTarget);
        return beans;
    }

    private String determinePrimaryCandidate(ConfigurableListableBeanFactory beanFactory,
                                             Collection<String> candidateBeanNames,
                                             ResolvableType type) {
        String primaryBeanName = null;
        for (String candidateBeanName : candidateBeanNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(candidateBeanName);
            if (beanDefinition.isPrimary()) {
                if (primaryBeanName != null) {
                    throw new NoUniqueBeanDefinitionException(type.resolve(),
                        candidateBeanNames.size(),
                        "more than one 'primary' bean found among candidates: "
                                + Collections.singletonList(candidateBeanNames));
                }
                primaryBeanName = candidateBeanName;
            }
        }
        return primaryBeanName;
    }

    private boolean isScopedTarget(String beanName) {
        try {
            return ScopedProxyUtils.isScopedTarget(beanName);
        } catch (Throwable ex) {
            return false;
        }
    }
}

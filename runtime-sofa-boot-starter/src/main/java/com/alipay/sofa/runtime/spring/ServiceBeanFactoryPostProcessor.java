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

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
import com.alipay.sofa.runtime.spring.parser.AbstractContractDefinitionParser;

/**
 * @author qilong.zql
 * @since 2.3.0
 */
public class ServiceBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Arrays.stream(beanFactory.getBeanDefinitionNames())
            .filter(
                beanId -> beanFactory.getBeanDefinition(beanId) instanceof AnnotatedBeanDefinition)
            .collect(Collectors.toMap(Function.identity(), beanFactory::getBeanDefinition))
            .entrySet().forEach(entry -> transformSofaBeanDefinition(entry, beanFactory));

    }

    protected void transformSofaBeanDefinition(Map.Entry<String, BeanDefinition> entry,
                                               ConfigurableListableBeanFactory beanFactory) {

        /**
         * {@link ScannedGenericBeanDefinition}
         */
        if (entry.getValue() instanceof ScannedGenericBeanDefinition
            || entry.getValue() instanceof AnnotatedGenericBeanDefinition) {
            generateSofaServiceDefinition(entry.getKey(),
                (AnnotatedBeanDefinition) entry.getValue(), beanFactory);
        } else {
        }
    }

    protected void generateSofaServiceDefinition(String beanId,
                                                 AnnotatedBeanDefinition beanDefinition,
                                                 ConfigurableListableBeanFactory beanFactory) {
        AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
        if (!annotationMetadata.hasAnnotation(SofaService.class.getCanonicalName())) {
            return;
        }
        SofaServiceAnnotationParser serviceParser = SofaServiceAnnotationParser.create(beanId,
            annotationMetadata.getClassName(),
            annotationMetadata.getAnnotationAttributes(SofaService.class.getCanonicalName()));
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        builder.getRawBeanDefinition().setBeanClass(ServiceFactoryBean.class);
        builder.getRawBeanDefinition().setScope(beanDefinition.getScope());
        builder.setLazyInit(beanDefinition.isLazyInit());
        builder.addPropertyValue(AbstractContractDefinitionParser.INTERFACE_PROPERTY,
            serviceParser.interfaceType());
        builder.addPropertyValue(AbstractContractDefinitionParser.UNIQUE_ID_PROPERTY,
            serviceParser.uniqueId());

    }

    protected boolean isProxyClass(Class clazz) {
        return Proxy.isProxyClass(clazz) || ClassUtils.isCglibProxyClass(clazz);
    }

    /**
     * {@link SofaService}
     */
    private static class SofaServiceAnnotationParser {
        private Map<String, Object> sofaServiceAttributes;
        private Class               underlyingClass;
        private String              beanId;

        private SofaServiceAnnotationParser(String beanId, Class underlyingClass,
                                            Map<String, Object> sofaServiceAttributes) {
            this.underlyingClass = underlyingClass;
            this.sofaServiceAttributes = sofaServiceAttributes;
            this.beanId = beanId;
        }

        static SofaServiceAnnotationParser create(String beanId, String underlyingClassName,
                                                  Map<String, Object> sofaServiceAttributes) {
            try {
                return new SofaServiceAnnotationParser(beanId, ClassUtils.forName(
                    underlyingClassName, null), sofaServiceAttributes);
            } catch (Throwable throwable) {
                throw new FatalBeanException("Parse the annotation of @SofaService failed.",
                    throwable);
            }
        }

        Class<?> interfaceType() {
            Class<?> interfaceType = (Class) sofaServiceAttributes.get("interfaceType");
            if (interfaceType.equals(void.class)) {
                Class<?> interfaces[] = underlyingClass.getInterfaces();
                if (interfaces == null || interfaces.length == 0) {
                    interfaceType = underlyingClass;
                } else if (interfaces.length == 1) {
                    interfaceType = interfaces[0];
                } else {
                    throw new FatalBeanException("Bean " + beanId + " has more than one interface.");
                }
            }
            return interfaceType;
        }

        String uniqueId() {
            return (String) sofaServiceAttributes.get("uniqueId");
        }

        SofaServiceBinding[] bindings() {
            return (SofaServiceBinding[]) sofaServiceAttributes.get("bindings");
        }
    }
}
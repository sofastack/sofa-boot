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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.alipay.sofa.runtime.annotation.PlaceHolderAnnotationInvocationHandler.AnnotationWrapperBuilder;
import com.alipay.sofa.runtime.annotation.PlaceHolderBinder;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.log.SofaLogger;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
import com.alipay.sofa.runtime.spring.parser.AbstractContractDefinitionParser;
import com.alipay.sofa.runtime.spring.parser.ServiceDefinitionParser;

/**
 * @author qilong.zql
 * @since 2.3.0
 */
public class ServiceBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    private final static String            BEAN_ID_PREFIX  = "com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean#api#";
    private final PlaceHolderBinder        binder          = new DefaultPlaceHolderBinder();
    private ConfigurableApplicationContext applicationContext;
    private SofaRuntimeContext             sofaRuntimeContext;
    private BindingAdapterFactory          bindingAdapterFactory;
    private BindingConverterFactory        bindingConverterFactory;
    private ConfigurableEnvironment        environment;
    private AtomicInteger                  beanIdGenerator = new AtomicInteger(0);

    public ServiceBeanFactoryPostProcessor(ConfigurableApplicationContext applicationContext,
                                           SofaRuntimeContext sofaRuntimeContext,
                                           BindingAdapterFactory bindingAdapterFactory,
                                           BindingConverterFactory bindingConverterFactory) {
        this.applicationContext = applicationContext;
        this.sofaRuntimeContext = sofaRuntimeContext;
        this.bindingAdapterFactory = bindingAdapterFactory;
        this.bindingConverterFactory = bindingConverterFactory;
        this.environment = applicationContext.getEnvironment();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Arrays.stream(beanFactory.getBeanDefinitionNames())
            .collect(Collectors.toMap(Function.identity(), beanFactory::getBeanDefinition))
            .forEach((key, value) -> transformSofaBeanDefinition(key, value, beanFactory));
    }

    /**
     * {@link ScannedGenericBeanDefinition}
     * {@link AnnotatedGenericBeanDefinition}
     * {@link GenericBeanDefinition}
     * {@link org.springframework.beans.factory.support.ChildBeanDefinition}
     * {@link org.springframework.beans.factory.support.RootBeanDefinition}
     * {@link org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader.ConfigurationClassBeanDefinition}
     */
    private void transformSofaBeanDefinition(String beanId, BeanDefinition beanDefinition,
                                             ConfigurableListableBeanFactory beanFactory) {
        if (isFromConfigurationSource(beanDefinition)) {
            generateSofaServiceDefinitionOnMethod(beanId, (AnnotatedBeanDefinition) beanDefinition,
                beanFactory);
        } else {
            Class<?> beanClassType = resolveBeanClassType(beanDefinition);
            if (beanClassType == null) {
                SofaLogger.warn("Bean class type cant be resolved from bean of {}", beanId);
                return;
            }
            generateSofaServiceDefinitionOnClass(beanId, beanClassType, beanDefinition, beanFactory);
        }
    }

    private void generateSofaServiceDefinitionOnMethod(String beanId,
                                                       AnnotatedBeanDefinition beanDefinition,
                                                       ConfigurableListableBeanFactory beanFactory) {
        MethodMetadata methodMetadata = beanDefinition.getFactoryMethodMetadata();
        if (methodMetadata == null
            || methodMetadata.getAnnotationAttributes(SofaService.class.getCanonicalName()) == null) {
            return;
        }

        Class returnType;
        Class declaringClass;
        Method method = null;

        try {
            returnType = ClassUtils.forName(methodMetadata.getReturnTypeName(), null);
            declaringClass = ClassUtils.forName(methodMetadata.getDeclaringClassName(), null);
            for (Method m : declaringClass.getDeclaredMethods()) {
                if (!m.getName().equals(beanId)) {
                    continue;
                }
                if (method != null) {
                    throw new IllegalStateException("multi @Bean-method with same name in "
                                                    + declaringClass.getCanonicalName());
                }
                method = m;
            }
        } catch (Throwable throwable) {
            SofaLogger.error(throwable, "Failed to resolve @SofaService on @Bean-method({}) in {}",
                beanId, methodMetadata.getDeclaringClassName());
            throw new FatalBeanException("Failed to resolve @SofaService on method", throwable);
        }

        if (method != null) {
            SofaService sofaServiceAnnotation = method.getAnnotation(SofaService.class);
            generateSofaServiceDefinition(beanId, sofaServiceAnnotation, returnType,
                beanDefinition, beanFactory);
        }
    }

    private void generateSofaServiceDefinitionOnClass(String beanId, Class<?> beanClass,
                                                      BeanDefinition beanDefinition,
                                                      ConfigurableListableBeanFactory beanFactory) {
        SofaService sofaServiceAnnotation = beanClass.getAnnotation(SofaService.class);
        if (sofaServiceAnnotation == null) {
            return;
        }
        generateSofaServiceDefinition(beanId, sofaServiceAnnotation, beanClass, beanDefinition,
            beanFactory);
    }

    @SuppressWarnings("unchecked")
    private void generateSofaServiceDefinition(String beanId, SofaService sofaServiceAnnotation,
                                               Class<?> beanClass, BeanDefinition beanDefinition,
                                               ConfigurableListableBeanFactory beanFactory) {
        if (sofaServiceAnnotation == null) {
            return;
        }
        AnnotationWrapperBuilder<SofaService> wrapperBuilder = AnnotationWrapperBuilder.wrap(
            sofaServiceAnnotation).withBinder(binder);
        sofaServiceAnnotation = wrapperBuilder.build();

        Class<?> interfaceType = sofaServiceAnnotation.interfaceType();
        if (interfaceType.equals(void.class)) {
            Class<?> interfaces[] = beanClass.getInterfaces();

            if (interfaces == null || interfaces.length == 0) {
                interfaceType = beanClass;
            } else if (interfaces.length == 1) {
                interfaceType = interfaces[0];
            } else {
                throw new FatalBeanException("Bean " + beanId + " has more than one interface.");
            }
        }

        String serviceBeanId = BEAN_ID_PREFIX + beanIdGenerator.getAndIncrement();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        builder.getRawBeanDefinition().setScope(beanDefinition.getScope());
        builder.setLazyInit(beanDefinition.isLazyInit());
        builder.getRawBeanDefinition().setBeanClass(ServiceFactoryBean.class);
        builder.addPropertyValue(AbstractContractDefinitionParser.INTERFACE_CLASS_PROPERTY,
            interfaceType);
        builder.addPropertyValue(AbstractContractDefinitionParser.UNIQUE_ID_PROPERTY,
            sofaServiceAnnotation.uniqueId());
        builder.addPropertyValue(AbstractContractDefinitionParser.BINDINGS,
            getSofaServiceBinding(sofaServiceAnnotation, sofaServiceAnnotation.bindings()));
        builder.addPropertyReference(ServiceDefinitionParser.REF, beanId);
        builder.addPropertyValue(ServiceDefinitionParser.BEAN_ID, serviceBeanId);
        builder.addPropertyValue(AbstractContractDefinitionParser.DEFINITION_BUILDING_TYPE, true);
        builder.addDependsOn(beanId);

        ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(serviceBeanId,
            builder.getBeanDefinition());
    }

    private List<Binding> getSofaServiceBinding(SofaService sofaServiceAnnotation,
                                                SofaServiceBinding[] sofaServiceBindings) {
        List<Binding> bindings = new ArrayList<>();
        for (SofaServiceBinding sofaServiceBinding : sofaServiceBindings) {
            if (JvmBinding.JVM_BINDING_TYPE.getType().equals(sofaServiceBinding.bindingType())) {
                bindings.add(new JvmBinding());
            } else {
                BindingConverter bindingConverter = bindingConverterFactory
                    .getBindingConverter(new BindingType(sofaServiceBinding.bindingType()));
                if (bindingConverter == null) {
                    throw new ServiceRuntimeException(
                        "Can not found binding converter for binding type "
                                + sofaServiceBinding.bindingType());
                }
                BindingConverterContext bindingConverterContext = new BindingConverterContext();
                bindingConverterContext.setInBinding(false);
                bindingConverterContext.setApplicationContext(applicationContext);
                bindingConverterContext.setAppName(sofaRuntimeContext.getAppName());
                bindingConverterContext.setAppClassLoader(sofaRuntimeContext.getAppClassLoader());
                Binding binding = bindingConverter.convert(sofaServiceAnnotation,
                    sofaServiceBinding, bindingConverterContext);
                bindings.add(binding);
            }
        }
        return bindings;
    }

    /**
     * {@link org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader.ConfigurationClassBeanDefinition}
     *
     * @param beanDefinition Check whether it is a bean definition created from a configuration class
     *                       as opposed to any other configuration source.
     * @return
     */
    private boolean isFromConfigurationSource(BeanDefinition beanDefinition) {
        return beanDefinition
            .getClass()
            .getCanonicalName()
            .startsWith(
                "org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader");
    }

    /**
     * {@link AnnotatedGenericBeanDefinition}
     * {@link ScannedGenericBeanDefinition}
     * {@link GenericBeanDefinition}
     * {@link org.springframework.beans.factory.support.ChildBeanDefinition}
     * {@link org.springframework.beans.factory.support.RootBeanDefinition}
     *
     * @param beanDefinition resolve bean class type from bean definition
     * @return
     */
    private Class<?> resolveBeanClassType(BeanDefinition beanDefinition) {
        Class<?> clazz = null;

        if (beanDefinition instanceof AnnotatedBeanDefinition) {
            AnnotationMetadata annotationMetadata = ((AnnotatedBeanDefinition) beanDefinition)
                .getMetadata();
            try {
                String className = annotationMetadata.getClassName();
                clazz = StringUtils.isEmpty(className) ? null : ClassUtils.forName(className, null);
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
                    clazz = StringUtils.isEmpty(className) ? null : ClassUtils.forName(className,
                        null);
                } catch (Throwable throwable) {
                    // ignore
                }
            }
        }

        if (ClassUtils.isCglibProxyClass(clazz)) {
            return clazz.getSuperclass();
        } else {
            return clazz;
        }
    }

    class DefaultPlaceHolderBinder implements PlaceHolderBinder {
        @Override
        public String bind(String text) {
            return environment.resolvePlaceholders(text);
        }
    }
}
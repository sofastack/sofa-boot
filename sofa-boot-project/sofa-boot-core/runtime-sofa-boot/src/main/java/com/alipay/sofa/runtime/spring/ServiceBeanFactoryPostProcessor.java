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
import com.alipay.sofa.boot.util.SmartAnnotationUtils;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import com.alipay.sofa.runtime.spring.bean.SofaBeanNameGenerator;
import com.alipay.sofa.runtime.spring.bean.SofaParameterNameDiscoverer;
import com.alipay.sofa.runtime.spring.factory.ReferenceFactoryBean;
import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
import com.alipay.sofa.runtime.spring.parser.AbstractContractDefinitionParser;
import com.alipay.sofa.runtime.spring.parser.ServiceDefinitionParser;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of {@link BeanFactoryPostProcessor} to resolve {@link SofaService}
 * and {@link SofaReference} annotation and register factory beans.
 *
 * @author qilong.zql
 * @since 3.1.0
 */
@SingletonSofaPostProcessor
public class ServiceBeanFactoryPostProcessor implements BeanFactoryPostProcessor,
                                            ApplicationContextAware, InitializingBean, Ordered {

    private static final Logger              LOGGER = SofaBootLoggerFactory
                                                        .getLogger(ServiceBeanFactoryPostProcessor.class);

    private ApplicationContext               applicationContext;

    private SofaRuntimeManager               sofaRuntimeManager;

    private BindingConverterFactory          bindingConverterFactory;

    private AnnotationWrapper<SofaService>   serviceAnnotationWrapper;

    private AnnotationWrapper<SofaReference> referenceAnnotationWrapper;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                                                                                   throws BeansException {
        if (beanFactory instanceof AbstractAutowireCapableBeanFactory) {
            ParameterNameDiscoverer parameterNameDiscoverer = ((AbstractAutowireCapableBeanFactory) beanFactory).getParameterNameDiscoverer();
            if (parameterNameDiscoverer == null) {
                parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
            }
            ((AbstractAutowireCapableBeanFactory) beanFactory)
                    .setParameterNameDiscoverer(new SofaParameterNameDiscoverer(parameterNameDiscoverer, referenceAnnotationWrapper));
        }

        Arrays.stream(beanFactory.getBeanDefinitionNames())
                .collect(Collectors.toMap(Function.identity(), beanFactory::getBeanDefinition))
                .forEach((key, value) -> transformSofaBeanDefinition(key, value, (BeanDefinitionRegistry) beanFactory));
    }

    /**
     * {@link ScannedGenericBeanDefinition}
     * {@link AnnotatedGenericBeanDefinition}
     * {@link GenericBeanDefinition}
     * {@link org.springframework.beans.factory.support.ChildBeanDefinition}
     * {@link org.springframework.beans.factory.support.RootBeanDefinition}
     */
    private void transformSofaBeanDefinition(String beanId, BeanDefinition beanDefinition,
                                             BeanDefinitionRegistry registry) {
        if (BeanDefinitionUtil.isFromConfigurationSource(beanDefinition)) {
            generateSofaServiceDefinitionOnMethod(beanId, (AnnotatedBeanDefinition) beanDefinition,
                registry);
        } else {
            Class<?> beanClassType = BeanDefinitionUtil.resolveBeanClassType(beanDefinition);
            if (beanClassType == null) {
                return;
            }
            generateSofaServiceDefinitionOnClass(beanId, beanClassType, beanDefinition, registry);
        }
    }

    private void generateSofaServiceDefinitionOnMethod(String beanId,
                                                       AnnotatedBeanDefinition beanDefinition,
                                                       BeanDefinitionRegistry registry) {
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
            Method method = candidateMethods.get(0);
            Collection<SofaService> sofaServiceList = SmartAnnotationUtils.getAnnotations(method, SofaService.class);
            // use method @SofaService annotations
            if (!sofaServiceList.isEmpty()) {
                sofaServiceList.forEach((annotation) -> generateSofaServiceDefinition(beanId, annotation, returnType, beanDefinition,
                                registry));
            } else {
                // use returnType class @SofaService annotations
                sofaServiceList = SmartAnnotationUtils.getAnnotations(returnType, SofaService.class);
                sofaServiceList.forEach((annotation) -> generateSofaServiceDefinition(beanId, annotation, returnType, beanDefinition,
                                registry));
            }
            generateSofaReferenceDefinition(beanId, candidateMethods.get(0), registry);
        } else if (candidateMethods.size() > 1) {
            for (Method m : candidateMethods) {
                if (AnnotatedElementUtils.hasAnnotation(m, SofaService.class)
                    || AnnotatedElementUtils.hasAnnotation(returnType, SofaService.class)) {
                    throw new FatalBeanException(
                        ErrorCode.convert("01-02002", declaringClass.getCanonicalName()));
                }

                if (Stream.of(m.getParameterAnnotations())
                        .flatMap(Stream::of).anyMatch(annotation -> annotation instanceof SofaReference)) {
                    throw new FatalBeanException(
                            ErrorCode.convert("01-02003", declaringClass.getCanonicalName()));
                }
            }
        }
    }

    private void generateSofaReferenceDefinition(String beanId, Method method,
                                                 BeanDefinitionRegistry registry) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; ++i) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof SofaReference) {
                    doGenerateSofaReferenceDefinition(registry.getBeanDefinition(beanId),
                        (SofaReference) annotation, parameterTypes[i], registry);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void doGenerateSofaReferenceDefinition(BeanDefinition beanDefinition,
                                                   SofaReference sofaReference,
                                                   Class<?> parameterType,
                                                   BeanDefinitionRegistry registry) {
        Assert.isTrue(
            JvmBinding.JVM_BINDING_TYPE.getType().equals(sofaReference.binding().bindingType()),
            "Only jvm type of @SofaReference on parameter is supported.");

        sofaReference = referenceAnnotationWrapper.wrap(sofaReference);
        Class<?> interfaceType = sofaReference.interfaceType();
        if (interfaceType.equals(void.class)) {
            interfaceType = parameterType;
        }
        String uniqueId = sofaReference.uniqueId();
        String referenceId = SofaBeanNameGenerator.generateSofaReferenceBeanName(interfaceType,
            uniqueId);

        // build sofa reference definition
        if (!registry.containsBeanDefinition(referenceId)) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
            builder.getRawBeanDefinition().setScope(beanDefinition.getScope());
            builder.getRawBeanDefinition().setLazyInit(beanDefinition.isLazyInit());
            builder.getRawBeanDefinition().setBeanClass(ReferenceFactoryBean.class);
            builder.addAutowiredProperty(AbstractContractDefinitionParser.SOFA_RUNTIME_CONTEXT);
            builder
                .addAutowiredProperty(AbstractContractDefinitionParser.BINDING_CONVERTER_FACTORY);
            builder.addAutowiredProperty(AbstractContractDefinitionParser.BINDING_ADAPTER_FACTORY);
            builder.addPropertyValue(AbstractContractDefinitionParser.UNIQUE_ID_PROPERTY, uniqueId);
            builder.addPropertyValue(AbstractContractDefinitionParser.INTERFACE_CLASS_PROPERTY,
                interfaceType);
            builder.addPropertyValue(AbstractContractDefinitionParser.BINDINGS,
                getSofaReferenceBinding(sofaReference, sofaReference.binding()));
            builder.addPropertyValue(AbstractContractDefinitionParser.DEFINITION_BUILDING_API_TYPE,
                true);
            builder.getBeanDefinition().setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE,
                interfaceType);
            registry.registerBeanDefinition(referenceId, builder.getBeanDefinition());
        }

        // add bean dependency relationship
        if (beanDefinition.getDependsOn() == null) {
            beanDefinition.setDependsOn(referenceId);
        } else {
            String[] added = ObjectUtils.addObjectToArray(beanDefinition.getDependsOn(),
                referenceId);
            beanDefinition.setDependsOn(added);
        }
    }

    private void generateSofaServiceDefinitionOnClass(String beanId, Class<?> beanClass,
                                                      BeanDefinition beanDefinition,
                                                      BeanDefinitionRegistry registry) {
        // See issue: https://github.com/sofastack/sofa-boot/issues/835
        SmartAnnotationUtils.getAnnotations(beanClass, SofaService.class)
                .forEach((annotation) -> generateSofaServiceDefinition(beanId, annotation, beanClass, beanDefinition,
                        registry));
    }

    @SuppressWarnings("unchecked")
    private void generateSofaServiceDefinition(String beanId, SofaService sofaServiceAnnotation,
                                               Class<?> beanClass, BeanDefinition beanDefinition,
                                               BeanDefinitionRegistry registry) {
        if (sofaServiceAnnotation == null) {
            return;
        }
        sofaServiceAnnotation = serviceAnnotationWrapper.wrap(sofaServiceAnnotation);

        Class<?> interfaceType = sofaServiceAnnotation.interfaceType();
        if (interfaceType.equals(void.class)) {
            Class<?> interfaces[] = beanClass.getInterfaces();

            if (beanClass.isInterface() || interfaces == null || interfaces.length == 0) {
                interfaceType = beanClass;
            } else if (interfaces.length == 1) {
                interfaceType = interfaces[0];
            } else {
                throw new FatalBeanException(ErrorCode.convert("01-02004", beanId));
            }
        }

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        String serviceId = SofaBeanNameGenerator.generateSofaServiceBeanName(interfaceType,
            sofaServiceAnnotation.uniqueId(), beanId);

        if (!registry.containsBeanDefinition(serviceId)) {
            builder.getRawBeanDefinition().setScope(beanDefinition.getScope());
            builder.setLazyInit(beanDefinition.isLazyInit());
            builder.getRawBeanDefinition().setBeanClass(ServiceFactoryBean.class);
            builder.addAutowiredProperty(AbstractContractDefinitionParser.SOFA_RUNTIME_CONTEXT);
            builder
                .addAutowiredProperty(AbstractContractDefinitionParser.BINDING_CONVERTER_FACTORY);
            builder.addAutowiredProperty(AbstractContractDefinitionParser.BINDING_ADAPTER_FACTORY);
            builder.addPropertyValue(AbstractContractDefinitionParser.INTERFACE_CLASS_PROPERTY,
                interfaceType);
            builder.addPropertyValue(AbstractContractDefinitionParser.UNIQUE_ID_PROPERTY,
                sofaServiceAnnotation.uniqueId());
            builder.addPropertyValue(AbstractContractDefinitionParser.BINDINGS,
                getSofaServiceBinding(sofaServiceAnnotation, sofaServiceAnnotation.bindings()));
            builder.addPropertyReference(ServiceDefinitionParser.REF, beanId);
            builder.addPropertyValue(ServiceDefinitionParser.BEAN_ID, beanId);
            builder.addPropertyValue(AbstractContractDefinitionParser.DEFINITION_BUILDING_API_TYPE,
                true);
            builder.addDependsOn(beanId);
            registry.registerBeanDefinition(serviceId, builder.getBeanDefinition());
        } else {
            LOGGER.warn("SofaService was already registered: {}", serviceId);
        }
    }

    private List<Binding> getSofaServiceBinding(SofaService sofaServiceAnnotation,
                                                SofaServiceBinding[] sofaServiceBindings) {
        List<Binding> bindings = new ArrayList<>();
        for (SofaServiceBinding sofaServiceBinding : sofaServiceBindings) {
            BindingConverter bindingConverter = bindingConverterFactory
                .getBindingConverter(new BindingType(sofaServiceBinding.bindingType()));
            if (bindingConverter == null) {
                throw new ServiceRuntimeException(ErrorCode.convert("01-00200",
                    sofaServiceBinding.bindingType()));
            }
            BindingConverterContext bindingConverterContext = new BindingConverterContext();
            bindingConverterContext.setInBinding(false);
            bindingConverterContext.setApplicationContext(applicationContext);
            bindingConverterContext.setAppName(sofaRuntimeManager.getAppName());
            bindingConverterContext.setAppClassLoader(sofaRuntimeManager.getAppClassLoader());
            Binding binding = bindingConverter.convert(sofaServiceAnnotation, sofaServiceBinding,
                bindingConverterContext);
            bindings.add(binding);
        }
        return bindings;
    }

    /**
     * get sofa reference binding annotated on parameter. At present, only jvm sofa reference is supported .
     * @param sofaReferenceAnnotation
     * @param sofaReferenceBinding
     * @return
     */
    private List<Binding> getSofaReferenceBinding(SofaReference sofaReferenceAnnotation,
                                                  SofaReferenceBinding sofaReferenceBinding) {
        if (!JvmBinding.XmlConstants.BINDING_TYPE.equals(sofaReferenceBinding.bindingType())) {
            throw new ServiceRuntimeException(ErrorCode.convert("01-02005"));
        }
        List<Binding> bindings = new ArrayList<>();
        BindingConverter bindingConverter = bindingConverterFactory
            .getBindingConverter(new BindingType(sofaReferenceBinding.bindingType()));
        if (bindingConverter == null) {
            throw new ServiceRuntimeException(ErrorCode.convert("01-00200",
                sofaReferenceBinding.bindingType()));
        }
        BindingConverterContext bindingConverterContext = new BindingConverterContext();
        bindingConverterContext.setInBinding(true);
        bindingConverterContext.setApplicationContext(applicationContext);
        bindingConverterContext.setAppName(sofaRuntimeManager.getAppName());
        bindingConverterContext.setAppClassLoader(sofaRuntimeManager.getAppClassLoader());
        Binding binding = bindingConverter.convert(sofaReferenceAnnotation, sofaReferenceBinding,
            bindingConverterContext);
        bindings.add(binding);
        return bindings;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.serviceAnnotationWrapper = AnnotationWrapper.create(SofaService.class)
            .withEnvironment(applicationContext.getEnvironment())
            .withBinder(DefaultPlaceHolderBinder.INSTANCE);
        this.referenceAnnotationWrapper = AnnotationWrapper.create(SofaReference.class)
            .withEnvironment(applicationContext.getEnvironment())
            .withBinder(DefaultPlaceHolderBinder.INSTANCE);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // avoid getByType trigger factory bean early init
        this.sofaRuntimeManager = applicationContext.getBean("sofaRuntimeManager",
            SofaRuntimeManager.class);
        this.bindingConverterFactory = applicationContext.getBean("bindingConverterFactory",
            BindingConverterFactory.class);
    }
}

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
package com.alipay.sofa.rpc.boot.ext;

import com.alipay.sofa.rpc.ext.Extensible;
import com.alipay.sofa.rpc.ext.Extension;
import com.alipay.sofa.rpc.ext.ExtensionLoader;
import com.alipay.sofa.rpc.ext.ExtensionLoaderFactory;
import com.alipay.sofa.rpc.log.Logger;
import com.alipay.sofa.rpc.log.LoggerFactory;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhaowang
 * @version : RpcExtensionBeanPostProcessor.java, v 0.1 2020年05月08日 2:27 下午 zhaowang Exp $
 */
public class RpcExtensionBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory
                                           .getLogger(RpcExtensionBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        Class<?> extensionClass = bean.getClass();
        Extension extension = AnnotationUtils.findAnnotation(extensionClass, Extension.class);
        if (extension != null) {
            List<Class<?>> candidateExtensibleClasses;
            candidateExtensibleClasses = ClassUtils.getAllInterfaces(extensionClass);
            candidateExtensibleClasses.addAll(ClassUtils.getAllSuperclasses(extensionClass));
            Set<Class<?>> extensibleClasses = candidateExtensibleClasses.stream()
                    .filter(RpcExtensionBeanPostProcessor::isExtensible).collect(Collectors.toSet());
            for (Class<?> extensibleClass : extensibleClasses) {
                ExtensionLoader<?> extensionLoader = ExtensionLoaderFactory
                        .getExtensionLoader(extensibleClass);
                extensionLoader.loadExtension(extensionClass);
            }
        }
        return bean;
    }

    private static boolean isExtensible(Class<?> clazz) {
        Extensible annotation = clazz.getAnnotation(Extensible.class);
        return annotation != null;

    }

}
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

import com.alipay.sofa.boot.annotation.AnnotationWrapper;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Implementation of {@link ParameterNameDiscoverer} to handle parameter with {@link SofaReference} annotation.
 *
 * @author qilong.zql
 * @since 3.1.0
 */
public class SofaParameterNameDiscoverer implements ParameterNameDiscoverer {

    private final ParameterNameDiscoverer          parameterNameDiscoverer;

    private final AnnotationWrapper<SofaReference> referenceAnnotationWrapper;

    public SofaParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer,
                                       AnnotationWrapper<SofaReference> referenceAnnotationWrapper) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
        this.referenceAnnotationWrapper = referenceAnnotationWrapper;
    }

    @Override
    public String[] getParameterNames(Method method) {
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] annotations = method.getParameterAnnotations();
        return transformParameterNames(parameterNames, parameterTypes, annotations);
    }

    @Override
    public String[] getParameterNames(Constructor<?> ctor) {
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(ctor);
        Class<?>[] parameterTypes = ctor.getParameterTypes();
        Annotation[][] annotations = ctor.getParameterAnnotations();
        return transformParameterNames(parameterNames, parameterTypes, annotations);
    }

    @SuppressWarnings("unchecked")
    protected String[] transformParameterNames(String[] parameterNames, Class<?>[] parameterType,
                                               Annotation[][] annotations) {
        if (parameterNames == null) {
            return null;
        }
        for (int i = 0; i < annotations.length; ++i) {
            for (Annotation annotation : annotations[i]) {
                if (annotation instanceof SofaReference) {
                    SofaReference delegate = referenceAnnotationWrapper
                        .wrap((SofaReference) annotation);
                    Class<?> interfaceType = delegate.interfaceType();
                    if (interfaceType.equals(void.class)) {
                        interfaceType = parameterType[i];
                    }
                    String uniqueId = delegate.uniqueId();
                    parameterNames[i] = SofaBeanNameGenerator.generateSofaReferenceBeanName(
                        interfaceType, uniqueId);
                }
            }
        }
        return parameterNames;
    }
}

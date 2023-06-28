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
package com.alipay.sofa.boot.annotation;

import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;

/**
 * Used to wrap annotation to aware spring environment.
 *
 * @author huzijie
 * @version AnnotationWrapper.java, v 0.1 2023年01月17日 4:05 PM huzijie Exp $
 * @since 4.0.0
 */
public class AnnotationWrapper<A extends Annotation> {

    private final Class<A>    clazz;

    private Annotation        delegate;

    private PlaceHolderBinder binder;

    private Environment       environment;

    private AnnotationWrapper(Class<A> clazz) {
        this.clazz = clazz;
    }

    public static <A extends Annotation> AnnotationWrapper<A> create(Class<A> clazz) {
        return new AnnotationWrapper<>(clazz);
    }

    public static <A extends Annotation> AnnotationWrapper<A> create(Annotation annotation) {
        return new AnnotationWrapper(annotation.getClass());
    }

    public AnnotationWrapper<A> withBinder(PlaceHolderBinder binder) {
        this.binder = binder;
        return this;
    }

    public AnnotationWrapper<A> withEnvironment(Environment environment) {
        this.environment = environment;
        return this;
    }

    public A wrap(A annotation) {
        Assert.notNull(annotation, "annotation must not be null.");
        Assert.isInstanceOf(clazz, annotation, "parameter must be annotation type.");
        this.delegate = annotation;
        return build();
    }

    @SuppressWarnings("unchecked")
    private A build() {
        ClassLoader cl = this.getClass().getClassLoader();
        Class<?>[] exposedInterface = { delegate.annotationType(), WrapperAnnotation.class };
        return (A) Proxy.newProxyInstance(cl, exposedInterface,
            new PlaceHolderAnnotationInvocationHandler(delegate, binder, environment));
    }
}

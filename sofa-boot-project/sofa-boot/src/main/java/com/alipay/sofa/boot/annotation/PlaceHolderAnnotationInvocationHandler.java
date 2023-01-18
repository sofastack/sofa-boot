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
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Implementation of {@link InvocationHandler} to bind value from environment when get annotation value.
 *
 * @author qilong.zql
 * @author huzijie
 * @since 2.5.2
 */
public class PlaceHolderAnnotationInvocationHandler implements InvocationHandler {

    private final Annotation        delegate;

    private final PlaceHolderBinder binder;

    private final Environment       environment;

    public PlaceHolderAnnotationInvocationHandler(Annotation delegate, PlaceHolderBinder binder,
                                                  Environment environment) {
        this.delegate = delegate;
        this.binder = binder;
        this.environment = environment;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret = method.invoke(delegate, args);
        if (ret != null && !ReflectionUtils.isObjectMethod(method) && isAttributeMethod(method)) {
            return resolvePlaceHolder(ret);
        }
        return ret;
    }

    private boolean isAttributeMethod(Method method) {
        return method != null && method.getParameterTypes().length == 0
               && method.getReturnType() != void.class;
    }

    public Object resolvePlaceHolder(Object origin) {
        if (origin.getClass().isArray()) {
            int length = Array.getLength(origin);
            Object ret = Array.newInstance(origin.getClass().getComponentType(), length);
            for (int i = 0; i < length; ++i) {
                Array.set(ret, i, resolvePlaceHolder(Array.get(origin, i)));
            }
            return ret;
        } else {
            return doResolvePlaceHolder(origin);
        }
    }

    private Object doResolvePlaceHolder(Object origin) {
        if (origin instanceof String) {
            return binder.bind(environment, (String) origin);
        } else if (origin instanceof Annotation && !(origin instanceof WrapperAnnotation)) {
            return AnnotationWrapper.create((Annotation) origin).withBinder(binder)
                .withEnvironment(environment).wrap((Annotation) origin);
        } else {
            return origin;
        }
    }

}

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
package com.alipay.sofa.tracer.boot.flexible.aop;

import com.alipay.sofa.tracer.plugin.flexible.annotations.Tracer;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.DynamicMethodMatcherPointcut;
import org.springframework.aop.support.annotation.AnnotationClassFilter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/8/9 3:12 PM
 * @since:
 **/
public class TracerAnnotationClassPointcut extends DynamicMethodMatcherPointcut {

    @Override
    public boolean matches(Method method, Class<?> aClass, Object... objects) {
        return true;
    }

    @Override
    public ClassFilter getClassFilter() {
        return (clazz) -> new AnnotationClassOrMethodFilter(Tracer.class).matches(clazz);
    }

    private final class AnnotationClassOrMethodFilter extends AnnotationClassFilter {

        private final AnnotationMethodsResolver methodsResolver;

        AnnotationClassOrMethodFilter(Class<? extends Annotation> annotationType) {
            super(annotationType, true);
            this.methodsResolver = new AnnotationMethodsResolver(annotationType);
        }

        @Override
        public boolean matches(Class<?> clazz) {
            return super.matches(clazz) || this.methodsResolver.hasAnnotatedMethods(clazz);
        }

    }

    private static class AnnotationMethodsResolver {

        private final Class<? extends Annotation> annotationType;

        AnnotationMethodsResolver(Class<? extends Annotation> annotationType) {
            this.annotationType = annotationType;
        }

        boolean hasAnnotatedMethods(Class<?> clazz) {
            final AtomicBoolean found = new AtomicBoolean(false);
            ReflectionUtils.doWithMethods(clazz, (method) ->{
                if (found.get()) {
                    return;
                }
                Annotation annotation = AnnotationUtils.findAnnotation(method,
                        AnnotationMethodsResolver.this.annotationType);
                if (annotation != null) {
                    found.set(true);
                }
            });
            return found.get();
        }
    }
}

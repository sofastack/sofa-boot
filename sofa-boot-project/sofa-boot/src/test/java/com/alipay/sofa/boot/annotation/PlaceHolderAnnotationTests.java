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

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AnnotationWrapper} and {@link PlaceHolderAnnotationInvocationHandler}.
 *
 * @author qilong.zql
 * @author huzijie
 * @version 3.2.0
 */
public class PlaceHolderAnnotationTests {

    @Test
    public void annotationPlaceHolder() {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("key", "testKey");
        environment.setProperty("subKey", "testSubKey");
        AnnotationWrapper<SampleAnnotation> annotationWrapper = AnnotationWrapper
            .create(SampleAnnotation.class).withEnvironment(environment)
            .withBinder(DefaultPlaceHolderBinder.INSTANCE);

        SampleAnnotation origin = SampleClass.class.getAnnotation(SampleAnnotation.class);
        SampleAnnotation delegate = annotationWrapper.wrap(origin);

        assertThat(delegate.id()).isEqualTo("testKey");
        assertThat(delegate.bindings()[0].id()).isEqualTo("testSubKey");
        assertThat(delegate.bindings()[1].id()).isEqualTo("${any}");
    }

    @SampleAnnotation(id = "${key}", bindings = { @SubSampleAnnotation(id = "${subKey}"),
            @SubSampleAnnotation(id = "${any}") })
    static class SampleClass {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    public @interface SampleAnnotation {
        String id() default "";

        /**
         * bindings of service
         *
         * @return bindings of service
         */
        SubSampleAnnotation[] bindings() default {};
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    public @interface SubSampleAnnotation {
        String id() default "";
    }
}

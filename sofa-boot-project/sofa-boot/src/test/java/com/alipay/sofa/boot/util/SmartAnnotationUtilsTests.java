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
package com.alipay.sofa.boot.util;

import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SmartAnnotationUtils}
 *
 * @author huzijie
 * @version SmartAnnotationUtilsTests.java).isEqualTo(v 0.1 2023年06月20日 6:13 PM huzijie Exp $
 */
public class SmartAnnotationUtilsTests {

    private List<String> getAnnotations(AnnotatedElement element) {
        return SmartAnnotationUtils.getAnnotations(element, SampleAnnotation.class)
                .stream().map(SampleAnnotation::id).collect(Collectors.toList());
    }

    private Method getMethod(Class<?> clazz, String methodName) {
        return ReflectionUtils.findMethod(clazz, methodName);
    }

    @Test
    public void testAnnotationOnClass() {
        assertThat(1).isEqualTo(getAnnotations(SampleService.class).size());
        assertThat("SampleClass").isEqualTo(getAnnotations(SampleService.class).get(0));

        assertThat(1).isEqualTo(getAnnotations(NormalClass.class).size());
        assertThat("NormalClass").isEqualTo(getAnnotations(NormalClass.class).get(0));

        assertThat(2).isEqualTo(getAnnotations(RepeatableClass.class).size());
        assertThat("RepeatableA").isEqualTo(getAnnotations(RepeatableClass.class).get(0));
        assertThat("RepeatableB").isEqualTo(getAnnotations(RepeatableClass.class).get(1));

        assertThat(2).isEqualTo(getAnnotations(RepeatAnnotationClass.class).size());
        assertThat("RepeatableA").isEqualTo(getAnnotations(RepeatAnnotationClass.class).get(0));
        assertThat("RepeatableB").isEqualTo(getAnnotations(RepeatAnnotationClass.class).get(1));

        assertThat(2).isEqualTo(getAnnotations(RepeatableChildClass.class).size());
        assertThat("RepeatableA").isEqualTo(getAnnotations(RepeatableChildClass.class).get(0));
        assertThat("RepeatableB").isEqualTo(getAnnotations(RepeatableChildClass.class).get(1));

        assertThat(2).isEqualTo(getAnnotations(ChildRepeatableClass.class).size());
        assertThat("RepeatableC").isEqualTo(getAnnotations(ChildRepeatableClass.class).get(0));
        assertThat("RepeatableD").isEqualTo(getAnnotations(ChildRepeatableClass.class).get(1));

        assertThat(0).isEqualTo(getAnnotations(NoneAnnotationClass.class).size());

        assertThat(1).isEqualTo(getAnnotations(ChildClass.class).size());
        assertThat("ChildClass").isEqualTo(getAnnotations(ChildClass.class).get(0));

        assertThat(1).isEqualTo(getAnnotations(NoAnnotationChildClass.class).size());
        assertThat("NormalClass").isEqualTo(getAnnotations(NoAnnotationChildClass.class).get(0));

        assertThat(1).isEqualTo(getAnnotations(ImplClass.class).size());
        assertThat("ImplClass").isEqualTo(getAnnotations(ImplClass.class).get(0));

        assertThat(1).isEqualTo(getAnnotations(NoAnnotationImplClass.class).size());
        assertThat("SampleClass").isEqualTo(getAnnotations(NoAnnotationImplClass.class).get(0));

        assertThat(1).isEqualTo(getAnnotations(ChildAndImplClass.class).size());
        assertThat("ImplClass").isEqualTo(getAnnotations(ChildAndImplClass.class).get(0));
    }

    @Test
    public void testAnnotationOnMethod() {
        assertThat(0).isEqualTo(
            getAnnotations(getMethod(ChildMethodClass.class, "noMethod")).size());

        assertThat(1).isEqualTo(
            getAnnotations(getMethod(ChildMethodClass.class, "parentMethod")).size());
        assertThat("overrideParentMethod").isEqualTo(
            getAnnotations(getMethod(ChildMethodClass.class, "parentMethod")).get(0));

        assertThat(2).isEqualTo(
            getAnnotations(getMethod(ChildMethodClass.class, "selfMethod")).size());
        assertThat("selfMethodA").isEqualTo(
            getAnnotations(getMethod(ChildMethodClass.class, "selfMethod")).get(0));
        assertThat("selfMethodB").isEqualTo(
            getAnnotations(getMethod(ChildMethodClass.class, "selfMethod")).get(1));

        assertThat(1).isEqualTo(
            getAnnotations(getMethod(ChildMethodClass.class, "parentSelfMethod")).size());
        assertThat("parentSelfMethod").isEqualTo(
            getAnnotations(getMethod(ChildMethodClass.class, "parentSelfMethod")).get(0));

        assertThat(1).isEqualTo(
            getAnnotations(getMethod(ParentMethodClass.class, "parentMethod")).size());
        assertThat("parentMethod").isEqualTo(
            getAnnotations(getMethod(ParentMethodClass.class, "parentMethod")).get(0));
    }

    @SampleAnnotation(id = "SampleClass")
    interface SampleService {

    }

    @SampleAnnotation(id = "NormalClass")
    static class NormalClass {

    }

    @SampleAnnotation(id = "RepeatableA")
    @SampleAnnotation(id = "RepeatableB")
    static class RepeatableClass {

    }

    @SampleAnnotations(value = { @SampleAnnotation(id = "RepeatableA"),
            @SampleAnnotation(id = "RepeatableB") })
    static class RepeatAnnotationClass {

    }

    @SampleAnnotation(id = "RepeatableA")
    @SampleAnnotation(id = "RepeatableB")
    static class RepeatableChildClass extends NormalClass {

    }

    @SampleAnnotation(id = "RepeatableC")
    @SampleAnnotation(id = "RepeatableD")
    static class ChildRepeatableClass extends RepeatableClass {

    }

    static class NoneAnnotationClass {

    }

    @SampleAnnotation(id = "ChildClass")
    static class ChildClass extends NormalClass {

    }

    static class NoAnnotationChildClass extends NormalClass {

    }

    @SampleAnnotation(id = "ImplClass")
    static class ImplClass implements SampleService {

    }

    static class NoAnnotationImplClass implements SampleService {

    }

    static class ChildAndImplClass extends ImplClass {

    }

    static class ParentMethodClass {

        @SampleAnnotation(id = "parentMethod")
        public void parentMethod() {

        }

        @SampleAnnotation(id = "parentSelfMethod")
        public void parentSelfMethod() {

        }
    }

    static class ChildMethodClass extends ParentMethodClass {

        public void noMethod() {

        }

        @Override
        @SampleAnnotation(id = "overrideParentMethod")
        public void parentMethod() {

        }

        @SampleAnnotation(id = "selfMethodA")
        @SampleAnnotation(id = "selfMethodB")
        public void selfMethod() {

        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Repeatable(SampleAnnotations.class)
    public @interface SampleAnnotation {

        String id() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    public @interface SampleAnnotations {

        SampleAnnotation[] value();
    }
}

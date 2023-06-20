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
package com.alipay.sofa.boot.test.util;

import com.alipay.sofa.boot.util.SmartAnnotationUtils;
import org.junit.Assert;
import org.junit.Test;
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

/**
 * @author huzijie
 * @version SmartAnnotationUtilsTest.java, v 0.1 2023年06月20日 6:13 PM huzijie Exp $
 */
public class SmartAnnotationUtilsTest {

    private List<String> getAnnotations(AnnotatedElement element) {
        return SmartAnnotationUtils.getAnnotations(element, SampleAnnotation.class)
                .stream().map(SampleAnnotation::id).collect(Collectors.toList());
    }

    private Method getMethod(Class<?> clazz, String methodName) {
        return ReflectionUtils.findMethod(clazz, methodName);
    }

    @Test
    public void testAnnotationOnClass() {
        Assert.assertEquals(1, getAnnotations(SampleService.class).size());
        Assert.assertEquals("SampleClass", getAnnotations(SampleService.class).get(0));

        Assert.assertEquals(1, getAnnotations(NormalClass.class).size());
        Assert.assertEquals("NormalClass", getAnnotations(NormalClass.class).get(0));

        Assert.assertEquals(2, getAnnotations(RepeatableClass.class).size());
        Assert.assertEquals("RepeatableA", getAnnotations(RepeatableClass.class).get(0));
        Assert.assertEquals("RepeatableB", getAnnotations(RepeatableClass.class).get(1));

        Assert.assertEquals(2, getAnnotations(RepeatAnnotationClass.class).size());
        Assert.assertEquals("RepeatableA", getAnnotations(RepeatAnnotationClass.class).get(0));
        Assert.assertEquals("RepeatableB", getAnnotations(RepeatAnnotationClass.class).get(1));

        Assert.assertEquals(2, getAnnotations(RepeatableChildClass.class).size());
        Assert.assertEquals("RepeatableA", getAnnotations(RepeatableChildClass.class).get(0));
        Assert.assertEquals("RepeatableB", getAnnotations(RepeatableChildClass.class).get(1));

        Assert.assertEquals(2, getAnnotations(ChildRepeatableClass.class).size());
        Assert.assertEquals("RepeatableC", getAnnotations(ChildRepeatableClass.class).get(0));
        Assert.assertEquals("RepeatableD", getAnnotations(ChildRepeatableClass.class).get(1));

        Assert.assertEquals(0, getAnnotations(NoneAnnotationClass.class).size());

        Assert.assertEquals(1, getAnnotations(ChildClass.class).size());
        Assert.assertEquals("ChildClass", getAnnotations(ChildClass.class).get(0));

        Assert.assertEquals(1, getAnnotations(NoAnnotationChildClass.class).size());
        Assert.assertEquals("NormalClass", getAnnotations(NoAnnotationChildClass.class).get(0));

        Assert.assertEquals(1, getAnnotations(ImplClass.class).size());
        Assert.assertEquals("ImplClass", getAnnotations(ImplClass.class).get(0));

        Assert.assertEquals(1, getAnnotations(NoAnnotationImplClass.class).size());
        Assert.assertEquals("SampleClass", getAnnotations(NoAnnotationImplClass.class).get(0));

        Assert.assertEquals(1, getAnnotations(ChildAndImplClass.class).size());
        Assert.assertEquals("ImplClass", getAnnotations(ChildAndImplClass.class).get(0));
    }

    @Test
    public void testAnnotationOnMethod() {
        Assert
            .assertEquals(0, getAnnotations(getMethod(ChildMethodClass.class, "noMethod")).size());

        Assert.assertEquals(1, getAnnotations(getMethod(ChildMethodClass.class, "parentMethod"))
            .size());
        Assert.assertEquals("overrideParentMethod",
            getAnnotations(getMethod(ChildMethodClass.class, "parentMethod")).get(0));

        Assert.assertEquals(2, getAnnotations(getMethod(ChildMethodClass.class, "selfMethod"))
            .size());
        Assert.assertEquals("selfMethodA",
            getAnnotations(getMethod(ChildMethodClass.class, "selfMethod")).get(0));
        Assert.assertEquals("selfMethodB",
            getAnnotations(getMethod(ChildMethodClass.class, "selfMethod")).get(1));

        Assert.assertEquals(1,
            getAnnotations(getMethod(ChildMethodClass.class, "parentSelfMethod")).size());
        Assert.assertEquals("parentSelfMethod",
            getAnnotations(getMethod(ChildMethodClass.class, "parentSelfMethod")).get(0));

        Assert.assertEquals(1, getAnnotations(getMethod(ParentMethodClass.class, "parentMethod"))
            .size());
        Assert.assertEquals("parentMethod",
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

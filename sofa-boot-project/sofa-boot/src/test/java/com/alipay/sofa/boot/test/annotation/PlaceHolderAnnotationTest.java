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
package com.alipay.sofa.boot.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.Assert;
import org.junit.Test;

import com.alipay.sofa.boot.annotation.PlaceHolderAnnotationInvocationHandler;
import com.alipay.sofa.boot.annotation.PlaceHolderBinder;

/**
 * @author qilong.zql
 * @version 3.2.0
 */
public class PlaceHolderAnnotationTest {

    @Test
    public void testAnnotationPlaceHolder() {
        PlaceHolderBinder placeHolderBinder = new PlaceHolderBinder() {
            @Override
            public String bind(String key) {
                if ("${key}".equals(key)) {
                    return "value";
                } else if ("${subKey}".equals(key)) {
                    return "subValue";
                } else {
                    return "invalid";
                }
            }
        };

        SampleAnnotation origin = SampleClass.class.getAnnotation(SampleAnnotation.class);
        SampleAnnotation delegate = (SampleAnnotation) PlaceHolderAnnotationInvocationHandler.AnnotationWrapperBuilder
            .wrap(origin).withBinder(placeHolderBinder).build();

        Assert.assertEquals("value", delegate.id());
        Assert.assertEquals("subValue", delegate.bindings()[0].id());
        Assert.assertEquals("invalid", delegate.bindings()[1].id());
    }

    @SampleAnnotation(id = "${key}", bindings = { @SubSampleAnnotation(id = "${subKey}"),
            @SubSampleAnnotation(id = "any") })
    class SampleClass {
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
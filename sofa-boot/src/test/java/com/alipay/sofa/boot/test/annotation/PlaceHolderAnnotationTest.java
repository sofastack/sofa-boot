/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.sofa.boot.test.annotation;

import com.alipay.sofa.boot.annotation.PlaceHolderAnnotationInvocationHandler;
import com.alipay.sofa.boot.annotation.PlaceHolderBinder;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
        SampleAnnotation delegate = (SampleAnnotation) PlaceHolderAnnotationInvocationHandler.AnnotationWrapperBuilder.wrap(origin).withBinder(placeHolderBinder).build();

        Assert.assertEquals("value", delegate.id());
        Assert.assertEquals("subValue", delegate.bindings()[0].id());
        Assert.assertEquals("invalid", delegate.bindings()[1].id());
    }

    @SampleAnnotation(id="${key}", bindings = {@SubSampleAnnotation(id="${subKey}"), @SubSampleAnnotation(id="any")})
    class SampleClass{}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    public @interface SampleAnnotation{
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
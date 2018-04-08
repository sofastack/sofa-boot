/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.sofa.test.annotation;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author qilong.zql
 * @since 2.3.0
 */
public class DelegateToRunnerTest {

    @Test
    public void testDelegateToRunnerTest() {
        DelegateToRunner annotation = AnnotationUtils.getAnnotation(SampleClass.class, DelegateToRunner.class);
        Assert.assertTrue(annotation.value().equals(JUnit4.class));
    }

    @DelegateToRunner(JUnit4.class)
    static class SampleClass {

    }

}
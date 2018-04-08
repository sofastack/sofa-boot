/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.sofa.boot.test;

import com.alipay.sofa.test.runner.SofaJUnit4Runner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author qilong.zql
 * @since 2.3.0
 */
@RunWith(SofaJUnit4Runner.class)
public class UnitTestCaseOnArk {

    public static final String testClassloader = "com.alipay.sofa.ark.container.test.TestClassLoader";
    @Test
    public void test() {
        ClassLoader currentClassLoader = this.getClass().getClassLoader();
        Assert.assertTrue(currentClassLoader.getClass().getCanonicalName().equals(testClassloader));
    }
}
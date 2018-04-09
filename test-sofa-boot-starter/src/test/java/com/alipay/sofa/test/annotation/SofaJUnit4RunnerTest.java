/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.sofa.test.annotation;

import com.alipay.sofa.test.runner.SofaJUnit4Runner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author qilong.zql
 * @since 2.3.0
 */
@RunWith(SofaJUnit4Runner.class)
public class SofaJUnit4RunnerTest {

    @Test
    public void test() {
        Assert.assertTrue(true);
    }
}
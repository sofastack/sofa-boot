/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.sofa.test.annotation;

import org.junit.runner.Runner;

import java.lang.annotation.*;

/**
 * @author qilong.zql
 * @since 2.3.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface DelegateToRunner {
    /**
     * @return a Runner class (must have a constructor that takes a single Class to run)
     */
    Class<? extends Runner> value();
}
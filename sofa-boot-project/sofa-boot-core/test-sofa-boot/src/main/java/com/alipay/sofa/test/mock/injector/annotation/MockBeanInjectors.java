package com.alipay.sofa.test.mock.injector.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huzijie
 * @version MockBeanInjectors.java, v 0.1 2023年08月14日 6:03 PM huzijie Exp $
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MockBeanInjectors {

    MockBeanInjector[] value();
}

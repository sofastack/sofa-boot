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
package com.alipay.sofa.test.mock.injector.annotation;

import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Similar to {@link MockBeanInjector}
 *
 * @author pengym
 * @version SofaSpyBeanFor.java, v 0.1 2023年08月07日 15:38 pengym
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(SpyBeanInjectors.class)
public @interface SpyBeanInjector {
    /**
     * target bean name
     * @return
     */
    String name() default "";

    @AliasFor("classes")
    Class<?>[] value() default {};

    @AliasFor("value")
    Class<?>[] classes() default {};

    /**
     * This parameter specifies the SOFA module to be searched when resolving the stub targets. If this parameter is not specified, all SOFA modules will be searched by default, which may result in increased processing time!
     */
    String module() default "";

    /**
     * This parameter specifies the target {@link Class} to be injected with a {@link Mock} instance during test execution. The targets can be regular Spring {@link Bean}s objects. Note that if multiple targets are matched, they will <b>all</b> be injected with the {@link Mock} instance.
     */
    String field() default "";

    /**
     * The reset mode to apply to the spied bean. The default is {@link MockReset#AFTER}
     * meaning that spies are automatically reset after each test method is invoked.
     * @return the reset mode
     */
    MockReset reset() default MockReset.AFTER;

    /**
     * Indicates that Mockito methods such as {@link Mockito#verify(Object) verify(mock)}
     * should use the {@code target} of AOP advised beans, rather than the proxy itself.
     * If set to {@code false} you may need to use the result of
     * {@link org.springframework.test.util.AopTestUtils#getUltimateTargetObject(Object)
     * AopTestUtils.getUltimateTargetObject(...)} when calling Mockito methods.
     * @return {@code true} if the target of AOP advised beans is used or {@code false} if
     * the proxy is used directly
     */
    boolean proxyTargetAware() default true;

}

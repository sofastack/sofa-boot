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
package com.alipay.sofa.testing.api.annotation;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.testing.api.annotation.SofaMockBeanFor.SofaMockBeans;
import org.mockito.Mock;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Example usage:
 * <pre>
 * {@code
 * @SpringTest
 * public class ExampleServiceTest {
 *      @SofaReference
 *      private ExampleService service;
 *
 *      @SofaMockBeanFor(target = ExampleService.class)
 *      private ExternalService mock;
 *
 *      @Test
 *      public void test_case_1() {
 *          // 1. mock external dependency
 *          given(mock.callSomeMethod(...))
 *              .willReturn(...);
 *
 *          // 2. perform testing
 *          service.doSomething();
 *
 *          // 3. behavioral-driven testing / standard unit-testing
 *          then(mock)
 *              .should(atLeastOnce())
 *              .callSomeMethod(...);
 *
 *          assertThat(...)...;
 *      }
 * }
 * }
 * </pre>
 *
 * @author pengym
 * @version SofaMockBeanFor.java, v 0.1 2023年08月07日 15:32 pengym
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(SofaMockBeans.class)
public @interface SofaMockBeanFor {
    /**
     * This parameter specifies the target {@link Class} to be injected with a {@link Mock} instance during test execution. The targets can be regular Spring {@link Bean}s or {@link SofaReference} objects. Note that if multiple targets are matched, they will <b>all</b> be injected with the {@link Mock} instance.
     */
    @AliasFor("target")
    Class<?> value() default void.class;

    /**
     * This parameter specifies the target {@link Class} to be injected with a {@link Mock} instance during test execution. The targets can be regular Spring {@link Bean}s or {@link SofaReference} objects. Note that if multiple targets are matched, they will <b>all</b> be injected with the {@link Mock} instance.
     */
    @AliasFor("value")
    Class<?> target() default void.class;

    /**
     * This optional parameter specifies the {@link SofaReference#uniqueId()} for the target {@link SofaReference} to be injected with a {@link Mock} instance during test execution.
     */
    String uniqueId() default "";

    /**
     * This optional parameter specifies the qualifier (i.e., {@link Bean#name()}) for the target {@link Bean} to be injected with a {@link Mock} instance during test execution.
     */
    String qualifier() default "";

    /**
     * This optional parameter specifies the field name in the targets that will be replaced by the {@link Mock} instance. If this parameter is not specified, the name of the {@link Mock} will be used by default.
     */
    String field() default "";

    /**
     * This parameter specifies the SOFA module to be searched when resolving the stub targets. If this parameter is not specified, all SOFA modules will be searched by default, which may result in increased processing time!
     */
    String module() default "";

    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface SofaMockBeans {
        SofaMockBeanFor[] value();
    }
}
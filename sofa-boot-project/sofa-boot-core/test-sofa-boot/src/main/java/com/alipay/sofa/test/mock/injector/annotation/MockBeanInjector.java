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
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(MockBeanInjectors.class)
public @interface MockBeanInjector {

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
     * Any extra interfaces that should also be declared on the mock. See
     * {@link MockSettings#extraInterfaces(Class...)} for details.
     * @return any extra interfaces
     */
    Class<?>[] extraInterfaces() default {};

    /**
     * The {@link Answers} type to use on the mock.
     * @return the answer type
     */
    Answers answer() default Answers.RETURNS_DEFAULTS;

    /**
     * If the generated mock is serializable. See {@link MockSettings#serializable()} for
     * details.
     * @return if the mock is serializable
     */
    boolean serializable() default false;

    /**
     * The reset mode to apply to the mock bean. The default is {@link MockReset#AFTER}
     * meaning that mocks are automatically reset after each test method is invoked.
     * @return the reset mode
     */
    MockReset reset() default MockReset.AFTER;
}

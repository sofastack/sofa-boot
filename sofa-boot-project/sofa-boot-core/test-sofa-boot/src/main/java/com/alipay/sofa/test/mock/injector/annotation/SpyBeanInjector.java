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

import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be used to create spies and inject spy to a target bean's field.
 * <p>
 * Injector target bean can be found by type or by {@link #name() bean name}. When registered by
 * type, any existing single bean of a matching type (including subclasses) in the context
 * will be found for injector. If no suitable bean could be found, {@link IllegalStateException} will be thrown.
 * <p>
 * Field in target bean will be found by {@link #field()}. If no field could be found, {@link IllegalStateException} will be thrown.
 * <p>
 *
 * Typical usage might be: <pre class="code">
 * &#064;RunWith(SpringRunner.class)
 * public class ExampleServiceTest {
 *
 *      &#064;Autowired
 *      private ExampleService service;
 *
 *      &#064;SpyBeanInjector(type = ExampleService.class, field = "fieldA")
 *      private FieldAClass spy;
 *
 *      &#064;Test
 *      public void testInjectExampleServiceFieldA() {
 *          // 1. spy external dependency
 *          given(spy.callSomeMethod(...))
 *              .willReturn(...);
 *
 *          // 2. perform testing
 *          service.doSomething();
 *
 *          // 3. behavioral-driven testing / standard unit-testing
 *          then(spy)
 *              .should(atLeastOnce())
 *              .callSomeMethod(...);
 *
 *          assertThat(...)...;
 *      }
 *
 *      #064;Configuration
 *      &#064;Import(ExampleService.class) // A &#064;Component injected with ExampleService
 *      static class Config {
 *      }
 * }
 * </pre>
 * If there is more than one bean of the requested type, qualifier metadata must be
 * specified at field level: <pre class="code">
 * &#064;RunWith(SpringRunner.class)
 * public class ExampleTests {
 *
 *     &#064;SpyBeanInjector(type = ExampleService.class, field = "fieldA")
 *     &#064;Qualifier("example")
 *     private ExampleService service;
 *
 *     ...
 * }
 * </pre>
 * @author pengym
 * @version SpyBeanInjector.java, v 0.1 2023年08月07日 15:38 pengym
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpyBeanInjector {

    /**
     * The name for field which should inject the spy.
     * <p> When can not find the target field, an {@link IllegalStateException} will be thrown.
     */
    String field();

    /**
     * The name of the bean to inject the spy to a field.
     * @return the name of the target bean
     */
    String name() default "";

    /**
     * The class type of the bean to inject the spy to a field. This is an alias of {@link #type()} which can be used for
     * brevity if no other attributes are defined. See {@link #type()} for details.
     * @return the class type of the target bean
     */
    @AliasFor("type")
    Class<?> value() default void.class;

    /**
     * The class type of the bean to inject the spy to a field
     * @return the class ype of the target bean
     */
    @AliasFor("value")
    Class<?> type() default void.class;

    /**
     * The application context id to find the target bean. If not specified, the root application context will be used.
     * <p> When can not find the target SOFA module for the specified module name, an {@link IllegalStateException} will be thrown.
     */
    String module() default "";

    /**
     * The reset mode to apply to the spy. The default is {@link MockReset#AFTER}
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

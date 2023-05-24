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
package com.alipay.sofa.runtime.api.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to create a SOFA service, which will also be created as a bean.
 * Sample usage:
 *
 * <pre>
 *
 * &#064;SofaServiceBean(uniqueId = &quot;aop&quot;)
 * public class SampleServiceImpl implements SampleService {
 *
 *     &#064;Override
 *     public String say() {
 *         return &quot;sampleService&quot;;
 *     }
 * }
 * </pre>
 *
 * @author xunfang 23/5/23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@SofaService
@Component
public @interface SofaServiceBean {
    @AliasFor(annotation = Component.class)
    String value() default "";

    @AliasFor(annotation = SofaService.class)
    Class<?> interfaceType() default void.class;

    @AliasFor(annotation = SofaService.class)
    String uniqueId() default "";

    @AliasFor(annotation = SofaService.class)
    SofaServiceBinding[] bindings() default { @SofaServiceBinding };
}

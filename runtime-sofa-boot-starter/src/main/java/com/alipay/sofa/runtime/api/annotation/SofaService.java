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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to create a SOFA service of a spring bean. Sample usage:
 *
 * <pre>
 *
 * &#064;SofaService(uniqueId = &quot;aop&quot;)
 * public class SampleServiceImpl implements SampleService {
 *
 *     &#064;Override
 *     public String say() {
 *         return &quot;sampleService&quot;;
 *     }
 * }
 * </pre>
 *
 * @author xuanbei 18/3/1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SofaService {

    /**
     * The interface type of the SOFA service to be create. Default to the only interface of the annotated Spring bean
     * when not specified. When the annotated Spring bean has more than one interface, this field must be specified.
     * When you want to create a SOFA service which's interface type is not a java interface but and concrete java
     * class, this field must be specified.
     *
     * @return return interface type
     */
    Class<?> interfaceType() default void.class;

    /**
     * The unique id of the SOFA service to be created. Default to an empty string when not specified.
     *
     * @return return service unique-id
     */
    String uniqueId() default "";

    /**
     * bindings of service
     *
     * @return bindings of service
     */
    SofaServiceBinding[] bindings() default { @SofaServiceBinding };
}

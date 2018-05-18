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
 * Annotation used to create a SOFA reference and set it to the annotated field of a Spring bean. Sample usage:
 *
 * <pre>
 *
 * public class ReferenceAnnotationSample {
 *
 *     &#064;SofaReference
 *     private SampleService sampleService;
 * }
 * </pre>
 *
 * @author xuanbei 18/3/2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface SofaReference {

    /**
     * The type of the SOFA reference to be created. Default to the type of field annotated when not specified.
     *
     * @return return interface type
     */
    Class<?> interfaceType() default void.class;

    /**
     * The unique id of the SOFA reference to be created. Default to an empty string when not specified.
     *
     * @return return reference unique-id
     */
    String uniqueId() default "";

    /**
     * invoke jvm service first
     *
     * @return is jvm first or not
     */
    boolean jvmFirst() default true;

    /**
     * binding of reference
     *
     * @return binding of reference
     */
    SofaReferenceBinding binding() default @SofaReferenceBinding;
}

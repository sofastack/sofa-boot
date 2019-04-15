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
 * @author bystander
 * @since 2.6.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SofaMethod {

    /**
     * method name
     *
     * @return method name
     * @since 2.6.4
     */
    String name() default "";

    /**
     * timeout
     *
     * @return timeout
     * @since 2.6.4
     */
    int timeout() default 3000;

    /**
     * retry times
     *
     * @return retry times
     * @since 2.6.4
     */
    int retries() default 0;

    /**
     * invoke type
     *
     * @return invoke type
     * @since 2.6.4
     */
    String invokeType() default "sync";

    /**
     * callback implementation class name
     *
     * @return callback implementation class name
     * @since 2.6.4
     */
    String callbackClass() default "";

    /**
     * callback spring beanName ref
     *
     * @return callback spring beanName ref
     * @since 2.6.4
     */
    String callbackRef() default "";
}
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

/**
 * Annotation to configure sofa reference binding.
 *
 * @author xuanbei 18/5/11
 */
public @interface SofaReferenceBinding {

    /**
     * Binding type, maybe jvm/bolt/rest.
     *
     * @return binding type
     */
    String bindingType() default "jvm";

    /**
     * Timeout in milliseconds.
     *
     * @return timeout
     */
    int timeout() default 0;

    /**
     * Retry times.
     *
     * @return retry times
     */
    int retries() default 0;

    /**
     * Address time out.
     *
     * @return address time out
     */
    int addressWaitTime() default 0;

    /**
     * Invoke type.
     *
     * @return invoke type
     */
    String invokeType() default "sync";

    /**
     * Filter beans.
     *
     * @return filter beans
     */
    String[] filters() default {};

    /**
     * Direct url.
     *
     * @return direct url
     */
    String directUrl() default "";

    /**
     *
     * @return callback handler
     * @deprecated this attribute is not intended for use and will be removed the next major version.
     * callback handler
     */
    @Deprecated
    String callbackHandler() default "";

    /**
     * Callback implementation class name.
     *
     * @return callback implementation class name
     * @since 2.5.0
     */
    String callbackClass() default "";

    /**
     * Callback spring beanName ref.
     *
     * @return callback spring beanName ref
     * @since 2.5.0
     */
    String callbackRef() default "";

    /**
     * Registry for this consumer.
     *
     * @return registry for this consumer
     */
    String registry() default "";

    /**
     * The number of long connections per ref.
     *
     * @return connection num
     */
    int connectionNum() default 1;

    /**
     * Delay init connection.
     *
     * @return is lazy
     */
    boolean lazy() default false;

    /**
     * Specify serialize type.
     *
     * @return serialize type
     */
    String serializeType() default "";

    /**
     * Specify load balance type.
     *
     * @return  load balance type
     */
    String loadBalancer() default "";

    /**
     * Parameters of consumer.
     *
     * @return parameters of consumer
     */
    SofaParameter[] parameters() default {};

    /**
     * serialization between biz, default is false.
     * only serialize of reference and service is false
     * then invocation between biz would skip serialization
     * Note that the serialize of {@link SofaServiceBinding} is true.
     *
     * @return whether serialize
     */
    boolean serialize() default false;

    /**
     * For each method config.
     *
     * @return method configs
     * @since 2.6.4
     */
    SofaMethod[] methodInfos() default {};

    /**
     * Mock mode of reference.
     * @return "local", "remote" or empty
     */
    String mockMode() default "";

    /**
     * Get mock from spring beans.
     * @return bean name
     */
    String mockBean() default "";
}

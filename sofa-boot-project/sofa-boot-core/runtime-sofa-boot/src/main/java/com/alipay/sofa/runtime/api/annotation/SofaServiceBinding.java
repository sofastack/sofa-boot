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
 * Annotation to configure sofa service binding.
 *
 * @author xuanbei 18/5/11
 */
public @interface SofaServiceBinding {
    /**
     * Binding type, maybe jvm/bolt/rest.
     *
     * @return binding type
     */
    String bindingType() default "jvm";

    /**
     * Normal weight, default is 100.
     *
     * @return normal weight
     */
    int weight() default 0;

    /**
     * When warmup, the weight.
     *
     * @return warmup weight
     */
    int warmUpWeight() default 0;

    /**
     * Warmup time, default is 0.
     *
     * @return warmup time
     */
    int warmUpTime() default 0;

    /**
     * Filter beans.
     *
     * @return filter beans
     */
    String[] filters() default {};

    /**
     * Custom thread pool for current service.
     *
     * @return custom thread pool
     */
    String userThreadPool() default "";

    /**
     * Registry for this service.
     *
     * @return registry for this service
     */
    String registry() default "";

    /**
     * Timeout in milliseconds.
     *
     * @return timeout
     */
    int timeout() default 0;

    /**
     * Specify serialize type.
     *
     * @return serialize type
     */
    String serializeType() default "";

    /**
     * Parameters of service.
     *
     * @return parameters of service
     */
    SofaParameter[] parameters() default {};

    /**
     * Serialization between biz, default is true.
     * only serialize of reference and service is false
     * then invocation between biz would skip serialization.
     *
     * Note that the serialize of {@link SofaReferenceBinding} is false
     *
     * @return whether serialize
     */
    boolean serialize() default true;

    /**
     * For each method config.
     *
     * @return method configs
     * @since 2.6.4
     */
    SofaMethod[] methodInfos() default {};
}

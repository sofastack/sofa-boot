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
 * @author xuanbei 18/5/11
 */
public @interface SofaServiceBinding {
    /**
     * binding type, maybe jvm/bolt/rest
     *
     * @return binding type
     */
    String bindingType() default "jvm";

    /**
     * normal weight, default is 100
     *
     * @return normal weight
     */
    int weight() default 0;

    /**
     * when warmup, the weight.
     *
     * @return warmup weight
     */
    int warmUpWeight() default 0;

    /**
     * warmup time, default is 0
     *
     * @return warmup time
     */
    int warmUpTime() default 0;

    /**
     * filter beans
     *
     * @return filter beans
     */
    String[] filters() default {};

    /**
     * custorm thread pool for current service
     *
     * @return custorm thread pool
     */
    String userThreadPool() default "";

    /**
     * registry for this service
     *
     * @return registry for this service
     */
    String registry() default "";

    /**
     * timeout
     *
     * @return timeout
     */
    int timeout() default 3000;
}

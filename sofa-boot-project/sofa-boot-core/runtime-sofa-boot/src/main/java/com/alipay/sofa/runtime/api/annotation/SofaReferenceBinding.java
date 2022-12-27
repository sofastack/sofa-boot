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
public @interface SofaReferenceBinding {
    /**
     * binding type, maybe jvm/bolt/rest
     *
     * @return binding type
     */
    String bindingType() default "jvm";

    /**
     * timeout
     *
     * @return timeout
     */
    int timeout() default 0;

    /**
     * retry times
     *
     * @return retry times
     */
    int retries() default 0;

    /**
     * address time out
     *
     * @return address time out
     */
    int addressWaitTime() default 0;

    /**
     * invoke type
     *
     * @return invoke type
     */
    String invokeType() default "sync";

    /**
     * filter beans
     *
     * @return filter beans
     */
    String[] filters() default {};

    /**
     * direct url
     *
     * @return direct url
     */
    String directUrl() default "";

    /**
     * @return callback handler
     * @deprecated this attribute is not intended for use and will be removed the next major version.
     * callback handler
     */
    @Deprecated
    String callbackHandler() default "";

    /**
     * callback implementation class name
     *
     * @return callback implementation class name
     * @since 2.5.0
     */
    String callbackClass() default "";

    /**
     * callback spring beanName ref
     *
     * @return callback spring beanName ref
     * @since 2.5.0
     */
    String callbackRef() default "";

    /**
     * registry for this consumer
     *
     * @return registry for this consumer
     */
    String registry() default "";

    /**
     * the number of long connections per ref
     *
     * @return
     */
    int connectionNum() default 1;

    /**
     * delay init connection
     *
     * @return
     */
    boolean lazy() default false;

    /**
     * specify serialize type
     *
     * @return
     */
    String serializeType() default "";

    /**
     * specify load balance type
     *
     * @return
     */
    String loadBalancer() default "";

    /**
     * parameters of consumer
     *
     * @return parameters of consumer
     */
    SofaParameter[] parameters() default {};

    /**
     * serialization between biz, default is false.
     * only serialize of reference and service is false
     * then invocation between biz would skip serialization
     * Note that the serialize of {@link SofaServiceBinding} is true
     *
     * @return
     */
    boolean serialize() default false;

    /**
     * for each method config
     *
     * @return method configs
     * @since 2.6.4
     */
    SofaMethod[] methodInfos() default {};

    /**
     * mock mode of reference
     * @return "local", "remote" or empty
     */
    String mockMode() default "";

    /**
     * get mock from spring beans
     * @return bean name
     */
    String mockBean() default "";
}

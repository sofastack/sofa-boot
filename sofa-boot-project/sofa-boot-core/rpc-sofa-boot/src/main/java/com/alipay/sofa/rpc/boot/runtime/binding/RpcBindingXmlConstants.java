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
package com.alipay.sofa.rpc.boot.runtime.binding;

/**
 * SOFABoot RPC 相关的 XML 元素和属性
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class RpcBindingXmlConstants {

    /**
     * key start
     */
    public static final String TAG_GLOBAL_ATTRS      = "global-attrs";
    public static final String TAG_FILTER            = "filter";
    public static final String TAG_ROUTE             = "route";
    public static final String TAG_METHOD            = "method";
    public static final String TAG_PARAMETER         = "parameter";

    public static final String TAG_TIMEOUT           = "timeout";
    public static final String TAG_ADDRESS_WAIT_TIME = "address-wait-time";
    public static final String TAG_CONNECT_TIMEOUT   = "connect.timeout";
    public static final String TAG_RETRIES           = "retries";
    public static final String TAG_TYPE              = "type";
    public static final String TAG_CALLBACK_CLASS    = "callback-class";
    public static final String TAG_CALLBACK_REF      = "callback-ref";
    public static final String TAG_WEIGHT            = "weight";
    public static final String TAG_WARMUP_TIME       = "warm-up-time";
    public static final String TAG_WARMUP_WEIGHT     = "warm-up-weight";
    public static final String TAG_THREAD_POOL_REF   = "thread-pool-ref";
    public static final String TAG_REGISTRY          = "registry";
    public static final String TAG_GENERIC_INTERFACE = "generic-interface";
    public static final String TAG_TARGET_URL        = "target-url";
    public static final String TAG_SERIALIZE_TYPE    = "serialize-type";
    public static final String TAG_LOAD_BALANCER     = "loadBalancer";
    public static final String TAG_LAZY              = "lazy";
    public static final String TAG_CHECK             = "check";
    public static final String TAG_PARAMETER_KEY     = "key";
    public static final String TAG_PARAMETER_VALUE   = "value";

    public static final String TAG_NAME              = "name";

    /** key end */

    /**
     * value start
     */
    public static final String TYPE_SYNC             = "sync";
    public static final String TYPE_FUTURE           = "future";
    public static final String TYPE_CALLBACK         = "callback";
    public static final String TYPE_ONEWAY           = "oneway";
    /** value end */

}
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

import com.alipay.sofa.runtime.api.binding.BindingType;

/**
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class RpcBindingType {

    public static final BindingType BOLT_BINDING_TYPE  = new BindingType("bolt");

    public static final BindingType REST_BINDING_TYPE  = new BindingType("rest");

    public static final BindingType DUBBO_BINDING_TYPE = new BindingType("dubbo");

    public static final BindingType H2C_BINDING_TYPE   = new BindingType("h2c");

}
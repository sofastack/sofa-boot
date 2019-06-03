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

import com.alipay.sofa.rpc.boot.runtime.param.RpcBindingParam;
import com.alipay.sofa.runtime.api.binding.BindingType;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class BoltBinding extends RpcBinding {

    public BoltBinding(RpcBindingParam bindingParam, ApplicationContext applicationContext, boolean inBinding) {
        super(bindingParam, applicationContext, inBinding);
    }

    @Override
    public BindingType getBindingType() {
        return RpcBindingType.BOLT_BINDING_TYPE;
    }

}
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
package com.alipay.sofa.rpc.boot.runtime.adapter;

import com.alipay.sofa.rpc.boot.container.SpringBridge;
import com.alipay.sofa.rpc.boot.runtime.binding.RpcBinding;
import com.alipay.sofa.rpc.boot.runtime.binding.RpcBindingType;
import com.alipay.sofa.rpc.log.LogCodes;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;

/**
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class DubboBindingAdapter extends RpcBindingAdapter {

    @Override
    public BindingType getBindingType() {
        return RpcBindingType.DUBBO_BINDING_TYPE;
    }

    @Override
    public Object outBinding(Object contract, RpcBinding binding, Object target, SofaRuntimeContext sofaRuntimeContext) {
        return Boolean.TRUE;
    }

    @Override
    public void postUnoutBinding(Object contract, RpcBinding binding, Object target,
                                 SofaRuntimeContext sofaRuntimeContext) {
        try {
            String key = SpringBridge.getProviderConfigContainer().createUniqueName((Contract) contract, binding);
            SpringBridge.getProviderConfigContainer().removeProviderConfig(key);
        } catch (Exception e) {
            throw new ServiceRuntimeException(
                LogCodes.getLog(LogCodes.ERROR_PROXY_POST_UNPUBLISH_FAIL), e);
        }
    }
}
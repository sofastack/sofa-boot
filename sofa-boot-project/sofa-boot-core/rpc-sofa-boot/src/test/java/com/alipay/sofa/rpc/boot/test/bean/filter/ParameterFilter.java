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
package com.alipay.sofa.rpc.boot.test.bean.filter;

import java.util.Map;

import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.core.exception.SofaRpcException;
import com.alipay.sofa.rpc.core.request.SofaRequest;
import com.alipay.sofa.rpc.core.response.SofaResponse;
import com.alipay.sofa.rpc.filter.Filter;
import com.alipay.sofa.rpc.filter.FilterInvoker;

/**
 * @author <a href="mailto:scienjus@gmail.com">ScienJus</a>
 */
public class ParameterFilter extends Filter {

    private Map<String, String> providerParameters;

    private Map<String, String> consumerParameters;

    @Override
    public SofaResponse invoke(FilterInvoker invoker, SofaRequest request) throws SofaRpcException {
        if (invoker.getConfig() instanceof ProviderConfig) {
            providerParameters = invoker.getConfig().getParameters();
        } else {
            consumerParameters = invoker.getConfig().getParameters();
        }
        return invoker.invoke(request);
    }

    public Map<String, String> getProviderParameters() {
        return providerParameters;
    }

    public Map<String, String> getConsumerParameters() {
        return consumerParameters;
    }
}

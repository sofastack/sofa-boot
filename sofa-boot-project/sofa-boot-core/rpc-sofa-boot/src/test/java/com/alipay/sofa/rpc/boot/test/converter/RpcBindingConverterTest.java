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
package com.alipay.sofa.rpc.boot.test.converter;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.alipay.sofa.rpc.boot.runtime.binding.RpcBindingMethodInfo;
import com.alipay.sofa.rpc.boot.runtime.converter.BoltBindingConverter;
import com.alipay.sofa.rpc.boot.runtime.converter.RpcBindingConverter;
import com.alipay.sofa.runtime.api.annotation.SofaMethod;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
public class RpcBindingConverterTest {

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "bolt", methodInfos = { @SofaMethod(name = "test", retries = 1, invokeType = "callback", callbackClass = "class", callbackRef = "ref", timeout = 2000) }))
    private String testAnnotation;

    @Test
    public void parseSofaMethods() {

        RpcBindingConverter rpcBindingConverter = new BoltBindingConverter();

        SofaReference reference = null;
        try {
            reference = RpcBindingConverterTest.class.getDeclaredField("testAnnotation")
                .getAnnotation(SofaReference.class);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        List<RpcBindingMethodInfo> result = rpcBindingConverter.parseSofaMethods(reference
            .binding().methodInfos());

        Assert.assertEquals(1, result.size());
        final RpcBindingMethodInfo rpcBindingMethodInfo = result.get(0);
        Assert.assertEquals("test", rpcBindingMethodInfo.getName());
        Assert.assertEquals(1, rpcBindingMethodInfo.getRetries().intValue());
        Assert.assertEquals("callback", rpcBindingMethodInfo.getType());
        Assert.assertEquals("class", rpcBindingMethodInfo.getCallbackClass());
        Assert.assertEquals("ref", rpcBindingMethodInfo.getCallbackRef());

        Assert.assertEquals(2000, rpcBindingMethodInfo.getTimeout().intValue());

    }
}
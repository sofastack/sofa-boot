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

import com.alipay.sofa.rpc.boot.runtime.binding.RpcBindingMethodInfo;
import com.alipay.sofa.rpc.boot.runtime.converter.BoltBindingConverter;
import com.alipay.sofa.rpc.boot.runtime.converter.RpcBindingConverter;
import com.alipay.sofa.runtime.api.annotation.SofaMethod;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.service.impl.BindingConverterFactoryImpl;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for rpc binding converter.
 *
 * @author qilong.zql
 * @since 3.2.0
 */
public class RpcBindingConverterTests {

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "bolt", methodInfos = { @SofaMethod(name = "test", retries = 1, invokeType = "callback", callbackClass = "class", callbackRef = "ref", timeout = 2000) }))
    private String testAnnotation;

    @Test
    public void parseSofaMethods() {

        RpcBindingConverter rpcBindingConverter = new BoltBindingConverter();

        SofaReference reference = null;
        try {
            reference = RpcBindingConverterTests.class.getDeclaredField("testAnnotation")
                .getAnnotation(SofaReference.class);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        List<RpcBindingMethodInfo> result = rpcBindingConverter.parseSofaMethods(reference
            .binding().methodInfos());

        assertThat(1).isEqualTo(result.size());
        final RpcBindingMethodInfo rpcBindingMethodInfo = result.get(0);
        assertThat("test").isEqualTo(rpcBindingMethodInfo.getName());
        assertThat(1).isEqualTo(rpcBindingMethodInfo.getRetries().intValue());
        assertThat("callback").isEqualTo(rpcBindingMethodInfo.getType());
        assertThat("class").isEqualTo(rpcBindingMethodInfo.getCallbackClass());
        assertThat("ref").isEqualTo(rpcBindingMethodInfo.getCallbackRef());

        assertThat(2000).isEqualTo(rpcBindingMethodInfo.getTimeout().intValue());

    }

    @Test
    public void checkOrder() {
        BindingConverterFactory factory = new BindingConverterFactoryImpl();
        factory.addBindingConverters(new HashSet<>(SpringFactoriesLoader.loadFactories(
            BindingConverter.class, null)));
        BindingConverter bindingConverter = factory.getBindingConverter(TestBindingConverter.TEST);
        BindingConverter bindingConverterByTagName = factory
            .getBindingConverterByTagName(TestBindingConverter.TARGET_NAME);

        assertThat(bindingConverter).isInstanceOf(TestBindingConverter2.class);
        assertThat(bindingConverterByTagName).isInstanceOf(TestBindingConverter2.class);
    }
}

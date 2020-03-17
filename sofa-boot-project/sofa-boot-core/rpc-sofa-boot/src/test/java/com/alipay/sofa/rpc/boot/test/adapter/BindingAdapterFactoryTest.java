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
package com.alipay.sofa.rpc.boot.test.adapter;

import com.alipay.sofa.rpc.boot.test.RuntimeTestConfiguration;
import com.alipay.sofa.runtime.service.impl.BindingAdapterFactoryImpl;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhaowang
 * @version : BindingAdatperFactoryTest.java, v 0.1 2020年02月05日 2:46 下午 zhaowang Exp $
 */
public class BindingAdapterFactoryTest {

    @Test
    public void testOrder() {
        BindingAdapterFactoryImpl bindingAdapterFactory = new BindingAdapterFactoryImpl();
        bindingAdapterFactory.addBindingAdapters(RuntimeTestConfiguration
            .getClassesByServiceLoader(BindingAdapter.class));
        BindingAdapter bindingAdapter = bindingAdapterFactory.getBindingAdapter(XBindingAdapter.X);
        Assert.assertTrue(bindingAdapter instanceof XBindingAdapter2);
    }
}
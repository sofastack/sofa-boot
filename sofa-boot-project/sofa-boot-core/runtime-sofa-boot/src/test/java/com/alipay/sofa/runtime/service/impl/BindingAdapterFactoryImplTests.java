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
package com.alipay.sofa.runtime.service.impl;

import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.spi.binding.BindingAdapter;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.when;

/**
 * Tests for {@link BindingAdapterFactoryImpl}.
 *
 * @author huzijie
 * @version BindingAdapterFactoryImplTests.java, v 0.1 2023年04月10日 8:12 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class BindingAdapterFactoryImplTests {

    private final BindingType bindingType = new BindingType("test");

    @Mock
    private BindingAdapter    bindingAdapter1;

    @Mock
    private BindingAdapter    bindingAdapter2;

    @BeforeEach
    public void setUp() {
        when(bindingAdapter1.getBindingType()).thenReturn(bindingType);
        when(bindingAdapter2.getBindingType()).thenReturn(bindingType);
    }

    @Test
    public void getBindingAdapter() {
        BindingAdapterFactory bindingAdapterFactory = new BindingAdapterFactoryImpl();

        BindingAdapter bindingAdapter = bindingAdapterFactory.getBindingAdapter(bindingType);
        Assertions.assertThat(bindingAdapter).isNull();

        bindingAdapterFactory.addBindingAdapters(Set.of());
        Assertions.assertThat(bindingAdapter).isNull();

        bindingAdapterFactory.addBindingAdapters(Set.of(bindingAdapter1));
        bindingAdapter = bindingAdapterFactory.getBindingAdapter(bindingType);
        Assertions.assertThat(bindingAdapter).isEqualTo(bindingAdapter1);

        bindingAdapterFactory.addBindingAdapters(Set.of(bindingAdapter2));
        bindingAdapter = bindingAdapterFactory.getBindingAdapter(bindingType);
        Assertions.assertThat(bindingAdapter).isEqualTo(bindingAdapter1);
    }
}

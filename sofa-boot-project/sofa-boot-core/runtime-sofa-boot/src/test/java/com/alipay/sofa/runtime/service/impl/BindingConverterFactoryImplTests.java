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
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.mockito.Mockito.when;

/**
 * Tests for {@link BindingConverterFactoryImpl}.
 *
 * @author huzijie
 * @version BindingConverterFactoryImplTests.java, v 0.1 2023年04月10日 8:30 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class BindingConverterFactoryImplTests {

    private final BindingType bindingType = new BindingType("test");

    private final String      bindingTag  = "test";

    @Mock
    private BindingConverter  bindingConverter1;

    @Mock
    private BindingConverter  bindingConverter2;

    @BeforeEach
    public void setUp() {
        when(bindingConverter1.supportBindingType()).thenReturn(bindingType);
        when(bindingConverter2.supportBindingType()).thenReturn(bindingType);
        when(bindingConverter1.supportTagName()).thenReturn(bindingTag);
        when(bindingConverter2.supportTagName()).thenReturn(bindingTag);
    }

    @Test
    public void getBindingConverter() {
        BindingConverterFactory bindingConverterFactory = new BindingConverterFactoryImpl();

        BindingConverter bindingConverter = bindingConverterFactory
            .getBindingConverter(bindingType);
        Assertions.assertThat(bindingConverter).isNull();

        bindingConverterFactory.addBindingConverters(Set.of());
        Assertions.assertThat(bindingConverter).isNull();

        bindingConverterFactory.addBindingConverters(Set.of(bindingConverter1));
        bindingConverter = bindingConverterFactory.getBindingConverter(bindingType);
        Assertions.assertThat(bindingConverter).isEqualTo(bindingConverter1);
        bindingConverter = bindingConverterFactory.getBindingConverterByTagName(bindingTag);
        Assertions.assertThat(bindingConverter).isEqualTo(bindingConverter1);

        bindingConverterFactory.addBindingConverters(Set.of(bindingConverter2));
        bindingConverter = bindingConverterFactory.getBindingConverter(bindingType);
        Assertions.assertThat(bindingConverter).isEqualTo(bindingConverter1);
        bindingConverter = bindingConverterFactory.getBindingConverterByTagName(bindingTag);
        Assertions.assertThat(bindingConverter).isEqualTo(bindingConverter1);
    }
}

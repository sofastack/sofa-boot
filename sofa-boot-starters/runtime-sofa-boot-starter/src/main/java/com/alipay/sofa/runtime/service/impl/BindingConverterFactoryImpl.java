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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author xuanbei 18/2/28
 */
public class BindingConverterFactoryImpl implements BindingConverterFactory {
    private Map<BindingType, BindingConverter> bindingTypeBindingConverterMap = new HashMap<>();
    private Map<String, BindingConverter>      tagBindingConverterMap         = new HashMap<>();

    @Override
    public BindingConverter getBindingConverter(BindingType bindingType) {
        return bindingTypeBindingConverterMap.get(bindingType);
    }

    @Override
    public BindingConverter getBindingConverterByTagName(String tagName) {
        return tagBindingConverterMap.get(tagName);
    }

    @Override
    public void addBindingConverters(Set<BindingConverter> bindingConverters) {
        if (bindingConverters == null || bindingConverters.size() == 0) {
            return;
        }
        for (BindingConverter bindingConverter : bindingConverters) {
            bindingTypeBindingConverterMap.put(bindingConverter.supportBindingType(),
                bindingConverter);
            tagBindingConverterMap.put(bindingConverter.supportTagName(), bindingConverter);
        }
    }
}

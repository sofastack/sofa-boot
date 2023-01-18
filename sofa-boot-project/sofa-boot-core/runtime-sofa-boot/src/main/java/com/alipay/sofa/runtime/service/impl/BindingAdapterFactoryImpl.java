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
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Base implementation of {@link BindingAdapterFactory}.
 *
 * @author xuanbei 18/2/28
 */
@SuppressWarnings("rawtypes")
public class BindingAdapterFactoryImpl implements BindingAdapterFactory {

    private final Map<BindingType, BindingAdapter> bindingTypeBindingAdapterMap = new HashMap<>();

    @Override
    public BindingAdapter getBindingAdapter(BindingType bindingType) {
        return bindingTypeBindingAdapterMap.get(bindingType);
    }

    @Override
    public void addBindingAdapters(Set<BindingAdapter> bindingAdapters) {
        if (bindingAdapters == null || bindingAdapters.size() == 0) {
            return;
        }
        List<BindingAdapter> sortedBindingAdapters = new ArrayList<>(bindingAdapters);
        sortedBindingAdapters.sort(AnnotationAwareOrderComparator.INSTANCE);

        for (BindingAdapter bindingAdapter : sortedBindingAdapters) {
            bindingTypeBindingAdapterMap.putIfAbsent(bindingAdapter.getBindingType(),
                bindingAdapter);
        }
    }
}

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
package com.alipay.sofa.runtime.spi.service;

import com.alipay.sofa.runtime.api.binding.BindingType;

import java.util.Set;

/**
 * Binding Converter Factory
 *
 * @author xuanbei 18/2/28
 */
public interface BindingConverterFactory {
    /**
     * get binding converter by binding type
     *
     * @param bindingType Binding type
     * @return binding converter
     */
    BindingConverter getBindingConverter(BindingType bindingType);

    /**
     * get binding converter by tag name
     *
     * @param tagName tag name
     * @return binding converter
     */
    BindingConverter getBindingConverterByTagName(String tagName);

    /***
     * add binding converters
     *
     * @param bindingConverters
     */
    void addBindingConverters(Set<BindingConverter> bindingConverters);
}
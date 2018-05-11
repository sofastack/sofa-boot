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
import com.alipay.sofa.runtime.api.binding.SofaServiceDefinition;
import com.alipay.sofa.runtime.api.client.param.BindingParam;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.spring.TagNameSupport;
import org.w3c.dom.Element;

/**
 * Binding Converter, convert {@link BindingParam} or xml Element to concrete {@link Binding}
 *
 * @author xuanbei 18/2/28
 */
public interface BindingConverter<L extends BindingParam, R extends Binding> extends TagNameSupport {
    /**
     * convert {@link BindingParam} to concrete {@link Binding}
     *
     * @param bindingParam binding parameter
     * @param bindingConverterContext binding converter context
     * @return Binding Object
     */
    R convert(L bindingParam, BindingConverterContext bindingConverterContext);

    /**
     * convert xml Element to concrete {@link Binding}
     *
     * @param element xml Element
     * @param bindingConverterContext binding converter context
     * @return Binding Object
     */
    R convert(Element element, BindingConverterContext bindingConverterContext);

    /**
     * convert {@link SofaServiceDefinition} to concrete {@link Binding}
     *
     * @param bindingDefinition xml Element
     * @param bindingConverterContext binding converter context
     * @return Binding Object
     */
    R convert(SofaServiceDefinition bindingDefinition,
              BindingConverterContext bindingConverterContext);

    /**
     * get supported binding type
     *
     * @return supported binding type
     */
    BindingType supportBindingType();
}

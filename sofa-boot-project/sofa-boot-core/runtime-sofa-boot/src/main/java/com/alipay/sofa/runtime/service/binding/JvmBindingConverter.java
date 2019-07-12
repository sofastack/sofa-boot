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
package com.alipay.sofa.runtime.service.binding;

import org.w3c.dom.Element;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;

/**
 * @author qilong.zql
 * @since 3.1.3
 */
public class JvmBindingConverter implements BindingConverter<JvmBindingParam, JvmBinding> {
    @Override
    public JvmBinding convert(JvmBindingParam bindingParam,
                              BindingConverterContext bindingConverterContext) {
        return new JvmBinding().setJvmBindingParam(bindingParam);
    }

    @Override
    public JvmBinding convert(Element element, BindingConverterContext bindingConverterContext) {
        JvmBindingParam jvmBindingParam = new JvmBindingParam();
        if (element != null) {
            jvmBindingParam.setSerialize(Boolean.TRUE.toString().equalsIgnoreCase(
                element.getAttribute(JvmBinding.XmlConstants.SERIALIZE)));
        }
        return new JvmBinding().setJvmBindingParam(jvmBindingParam);
    }

    @Override
    public JvmBinding convert(SofaService sofaServiceAnnotation,
                              SofaServiceBinding sofaServiceBindingAnnotation,
                              BindingConverterContext bindingConverterContext) {
        if (JvmBinding.XmlConstants.BINDING_TYPE.equals(sofaServiceBindingAnnotation.bindingType())) {
            JvmBindingParam jvmBindingParam = new JvmBindingParam();
            jvmBindingParam.setSerialize(sofaServiceBindingAnnotation.serialize());
            return new JvmBinding().setJvmBindingParam(jvmBindingParam);
        }
        return null;
    }

    @Override
    public JvmBinding convert(SofaReference sofaReferenceAnnotation,
                              SofaReferenceBinding sofaReferenceBindingAnnotation,
                              BindingConverterContext bindingConverterContext) {
        if (JvmBinding.XmlConstants.BINDING_TYPE.equals(sofaReferenceBindingAnnotation
            .bindingType())) {
            JvmBindingParam jvmBindingParam = new JvmBindingParam();
            jvmBindingParam.setSerialize(sofaReferenceBindingAnnotation.serialize());
            return new JvmBinding().setJvmBindingParam(jvmBindingParam);
        }
        return null;
    }

    @Override
    public BindingType supportBindingType() {
        return JvmBinding.JVM_BINDING_TYPE;
    }

    @Override
    public String supportTagName() {
        return JvmBinding.XmlConstants.SUPPORT_TAG_NAME;
    }

}
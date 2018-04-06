/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
package com.alipay.sofa.runtime.spi.service;

import com.alipay.sofa.runtime.api.binding.BindingType;
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
     * @param bindingParam binding paramter
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
     * get supported binding type
     *
     * @return supported binding type
     */
    BindingType supportBindingType();
}

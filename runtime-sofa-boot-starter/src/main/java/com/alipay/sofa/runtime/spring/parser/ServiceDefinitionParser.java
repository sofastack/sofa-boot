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
package com.alipay.sofa.runtime.spring.parser;

import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
import com.alipay.sofa.runtime.spring.parser.AbstractContractDefinitionParser;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author xuanbei 18/3/1
 */
public class ServiceDefinitionParser extends AbstractContractDefinitionParser {

    private static final String REF     = "ref";
    private static final String BEAN_ID = "beanId";

    @Override
    protected void doParseInternal(Element element, ParserContext parserContext,
                                   BeanDefinitionBuilder builder) {
        String ref = element.getAttribute(REF);
        builder.addPropertyReference(REF, ref);
        if (element.hasAttribute("id")) {
            String id = element.getAttribute("id");
            builder.addPropertyValue(BEAN_ID, id);
        } else {
            builder.addPropertyValue(BEAN_ID, ref);
        }
    }

    @Override
    protected Class getBeanClass(Element element) {
        return ServiceFactoryBean.class;
    }

    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }

    @Override
    public String supportTagName() {
        return "service";
    }
}

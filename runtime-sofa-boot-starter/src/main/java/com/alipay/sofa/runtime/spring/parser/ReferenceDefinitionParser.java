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

import com.alipay.sofa.runtime.spring.factory.ReferenceFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author xuanbei 18/3/1
 */
public class ReferenceDefinitionParser extends AbstractContractDefinitionParser {

    private static final String LOCAL_FIRST           = "local-first";
    private static final String JVM_SERVICE           = "jvm-service";
    private static final String PROPERTY_LOCAL_FIRST  = "localFirst";
    private static final String PROPERTY_JVM_SERVICE  = "jvmService";
    private static final String PROPERTY_LOAD_BALANCE = "loadBalance";

    @Override
    protected void doParseInternal(Element element, ParserContext parserContext,
                                   BeanDefinitionBuilder builder) {
        String localFirstString = element.getAttribute(LOCAL_FIRST);

        if (localFirstString != null && localFirstString.length() > 0) {
            if ("true".equalsIgnoreCase(localFirstString)) {
                builder.addPropertyValue(PROPERTY_LOCAL_FIRST, true);
            } else if ("false".equalsIgnoreCase(localFirstString)) {
                builder.addPropertyValue(PROPERTY_LOCAL_FIRST, false);
            } else {
                throw new RuntimeException(
                    "Invalid value of property local-first, can only be true or false.");
            }
        }

        String jvmServiceString = element.getAttribute(JVM_SERVICE);
        String id = element.getAttribute("id");

        if (jvmServiceString != null && jvmServiceString.length() > 0) {
            if ("true".equalsIgnoreCase(jvmServiceString)) {
                if (id != null && id.length() > 0) {
                    builder.addPropertyValue(PROPERTY_JVM_SERVICE, true);
                }
            } else if ("false".equalsIgnoreCase(jvmServiceString)) {
                builder.addPropertyValue(PROPERTY_JVM_SERVICE, false);
            } else {
                throw new RuntimeException(
                    "Invalid value of property jvm-service, can only be true or false");
            }
        }

        String loadBalance = element.getAttribute(PROPERTY_LOAD_BALANCE);
        if (loadBalance != null && loadBalance.length() > 0) {
            builder.addPropertyValue(PROPERTY_LOAD_BALANCE, loadBalance);
        }
    }

    @Override
    protected Class getBeanClass(Element element) {
        return ReferenceFactoryBean.class;
    }

    @Override
    public String supportTagName() {
        return "reference";
    }
}
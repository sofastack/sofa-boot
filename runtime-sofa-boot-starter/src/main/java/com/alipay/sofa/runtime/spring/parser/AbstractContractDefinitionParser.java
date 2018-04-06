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

import com.alipay.boot.middleware.config.spring.namespace.spi.Slite2MiddlewareTagNameSupport;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xuanbei 18/3/1
 */
public abstract class AbstractContractDefinitionParser extends AbstractSingleBeanDefinitionParser
                                                                                                 implements
                                                                                                 Slite2MiddlewareTagNameSupport {

    protected static final String INTERFACE_ELEMENT           = "interface";
    protected static final String INTERFACE_PROPERTY          = "interfaceType";
    private static final String   BEAN_ID_ELEMENT             = "id";
    private static final String   BEAN_ID_PROPERTY            = "beanId";
    private static final String   UNIQUE_ID_ELEMENT           = "unique-id";
    private static final String   UNIQUE_ID_PROPERTY          = "uniqueId";
    private static final String   ELEMENTS                    = "elements";
    private static final String   REPEAT_REFER_LIMIT_ELEMENT  = "repeatReferLimit";
    private static final String   REPEAT_REFER_LIMIT_PROPERTY = "repeatReferLimit";

    @Override
    protected void doParse(Element element, ParserContext parserContext,
                           BeanDefinitionBuilder builder) {
        String id = element.getAttribute(BEAN_ID_ELEMENT);
        builder.addPropertyValue(BEAN_ID_PROPERTY, id);

        String interfaceType = element.getAttribute(INTERFACE_ELEMENT);
        builder.addPropertyValue(INTERFACE_PROPERTY, interfaceType);

        String uniqueId = element.getAttribute(UNIQUE_ID_ELEMENT);
        builder.addPropertyValue(UNIQUE_ID_PROPERTY, uniqueId);

        String repeatReferLimit = element.getAttribute(REPEAT_REFER_LIMIT_ELEMENT);
        builder.addPropertyValue(REPEAT_REFER_LIMIT_PROPERTY, repeatReferLimit);

        List<Element> childElements = DomUtils.getChildElements(element);
        List<TypedStringValue> elementAsTypedStringValueList = new ArrayList<TypedStringValue>();

        for (Element childElement : childElements) {
            try {
                TransformerFactory transFactory = TransformerFactory.newInstance();
                Transformer transformer = transFactory.newTransformer();
                StringWriter buffer = new StringWriter();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.transform(new DOMSource(childElement), new StreamResult(buffer));
                String str = buffer.toString();
                elementAsTypedStringValueList.add(new TypedStringValue(str, Element.class));
            } catch (Exception e) {
                throw new ServiceRuntimeException(e);
            }
        }

        builder.addPropertyValue("documentEncoding", element.getOwnerDocument().getXmlEncoding());
        builder.addPropertyValue(ELEMENTS, elementAsTypedStringValueList);

        doParseInternal(element, parserContext, builder);
    }

    protected abstract void doParseInternal(Element element, ParserContext parserContext,
                                            BeanDefinitionBuilder builder);
}
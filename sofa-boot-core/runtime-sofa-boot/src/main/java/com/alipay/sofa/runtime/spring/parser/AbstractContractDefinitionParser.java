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
package com.alipay.sofa.runtime.spring.parser;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.alipay.sofa.boot.spring.namespace.spi.SofaBootTagNameSupport;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;

/**
 * @author xuanbei 18/3/1
 */
public abstract class AbstractContractDefinitionParser extends AbstractSingleBeanDefinitionParser
                                                                                                 implements
                                                                                                 SofaBootTagNameSupport {
    public static final String INTERFACE_ELEMENT            = "interface";
    public static final String INTERFACE_PROPERTY           = "interfaceType";
    public static final String INTERFACE_CLASS_PROPERTY     = "interfaceClass";
    public static final String BEAN_ID_ELEMENT              = "id";
    public static final String BEAN_ID_PROPERTY             = "beanId";
    public static final String UNIQUE_ID_ELEMENT            = "unique-id";
    public static final String UNIQUE_ID_PROPERTY           = "uniqueId";
    public static final String ELEMENTS                     = "elements";
    public static final String BINDINGS                     = "bindings";
    public static final String REPEAT_REFER_LIMIT_ELEMENT   = "repeatReferLimit";
    public static final String REPEAT_REFER_LIMIT_PROPERTY  = "repeatReferLimit";
    public static final String DEFINITION_BUILDING_API_TYPE = "apiType";

    @Override
    protected void doParse(Element element, ParserContext parserContext,
                           BeanDefinitionBuilder builder) {
        String id = element.getAttribute(BEAN_ID_ELEMENT);
        builder.addPropertyValue(BEAN_ID_PROPERTY, id);

        String interfaceType = element.getAttribute(INTERFACE_ELEMENT);
        builder.addPropertyValue(INTERFACE_PROPERTY, interfaceType);
        builder.getBeanDefinition().getConstructorArgumentValues()
            .addIndexedArgumentValue(0, interfaceType);

        String uniqueId = element.getAttribute(UNIQUE_ID_ELEMENT);
        builder.addPropertyValue(UNIQUE_ID_PROPERTY, uniqueId);

        String repeatReferLimit = element.getAttribute(REPEAT_REFER_LIMIT_ELEMENT);
        builder.addPropertyValue(REPEAT_REFER_LIMIT_PROPERTY, repeatReferLimit);

        List<Element> childElements = DomUtils.getChildElements(element);
        List<TypedStringValue> elementAsTypedStringValueList = new ArrayList<>();

        for (Element childElement : childElements) {
            try {
                TransformerFactory transFactory = TransformerFactory.newInstance();
                Transformer transformer = transFactory.newTransformer();
                StringWriter buffer = new StringWriter();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.transform(new DOMSource(childElement), new StreamResult(buffer));
                String str = buffer.toString();
                elementAsTypedStringValueList.add(new TypedStringValue(str, Element.class));
            } catch (Throwable e) {
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

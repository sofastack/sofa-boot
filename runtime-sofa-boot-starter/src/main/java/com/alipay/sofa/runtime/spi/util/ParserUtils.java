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
package com.alipay.sofa.runtime.spi.util;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
public class ParserUtils {

    /**
     * Parse custom attributes, as ID, LAZY-INIT, DEPENDS-ON。
     *
     * @param element element
     * @param parserContext parser context
     * @param builder builder
     * @param callback callback
     */
    public static void parseCustomAttributes(Element element, ParserContext parserContext,
                                             BeanDefinitionBuilder builder,
                                             AttributeCallback callback) {
        NamedNodeMap attributes = element.getAttributes();

        for (int x = 0; x < attributes.getLength(); x++) {
            Attr attribute = (Attr) attributes.item(x);
            String name = attribute.getLocalName();

            if (BeanDefinitionParserDelegate.DEPENDS_ON_ATTRIBUTE.equals(name)) {
                builder.getBeanDefinition().setDependsOn(
                    (StringUtils.tokenizeToStringArray(attribute.getValue(),
                        BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS)));
            } else if (BeanDefinitionParserDelegate.LAZY_INIT_ATTRIBUTE.equals(name)) {
                builder.setLazyInit(Boolean.getBoolean(attribute.getValue()));
            } else if (BeanDefinitionParserDelegate.ABSTRACT_ATTRIBUTE.equals(name)) {
                builder.setAbstract(Boolean.parseBoolean(attribute.getValue()));
            } else if (BeanDefinitionParserDelegate.PARENT_ATTRIBUTE.equals(name)) {
                builder.setParentName(attribute.getValue());
            } else {
                callback.process(element, attribute, builder, parserContext);
            }
        }
    }

    /**
     *
     * @author xi.hux@alipay.com
     * @since 2.6.0
     */
    public interface AttributeCallback {

        /**
         * Parser attribute
         *
         * @param parent element parent
         * @param attribute attribute
         * @param builder builder
         * @param parserContext parser context
         */
        void process(Element parent, Attr attribute, BeanDefinitionBuilder builder,
                     ParserContext parserContext);
    }

}

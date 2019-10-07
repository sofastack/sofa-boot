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
package com.alipay.sofa.runtime.ext.spring.parser;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alipay.sofa.runtime.ext.spring.ExtensionFactoryBean;

/**
 * Extension definition parser
 *
 * @author xi.hux@alipay.com
 * @author yangyanzhao@alipay.com
 * @author ruoshan
 * @since 2.6.0
 */
public class ExtensionBeanDefinitionParser extends AbstractExtBeanDefinitionParser {

    public static final String                         CONTENT  = "content";

    private static final ExtensionBeanDefinitionParser instance = new ExtensionBeanDefinitionParser();

    public ExtensionBeanDefinitionParser() {
    }

    public static ExtensionBeanDefinitionParser getInstance() {
        return instance;
    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return ExtensionFactoryBean.class;
    }

    @Override
    protected void parserSubElement(Element element, ParserContext parserContext,
                                    BeanDefinitionBuilder builder) {
        NodeList nl = element.getChildNodes();

        // parse all sub elements
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element subElement = (Element) node;
                // osgi:content
                if (CONTENT.equals(subElement.getLocalName())) {
                    builder.addPropertyValue(CONTENT, subElement);
                }
            }
        }
    }

    @Override
    public String supportTagName() {
        return "extension";
    }
}

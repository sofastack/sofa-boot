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

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.Conventions;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alipay.sofa.runtime.ext.spring.ExtensionPointFactoryBean;
import com.alipay.sofa.runtime.log.SofaLogger;
import com.alipay.sofa.runtime.spi.util.ParserUtils;

/**
 * Extension point definition parser
 *
 * @author xi.hux@alipay.com
 * @author yangyanzhao@alipay.com
 * @author ruoshan
 * @since 2.6.0
 */
public class ExtensionPointBeanDefinitionParser extends AbstractExtBeanDefinitionParser {

    public static final String                              CLASS        = "class";

    public static final String                              OBJECT       = "object";

    public static final String                              CONTRIBUTION = "contribution";

    private static final ExtensionPointBeanDefinitionParser instance     = new ExtensionPointBeanDefinitionParser();

    public ExtensionPointBeanDefinitionParser() {
    }

    public static ExtensionPointBeanDefinitionParser getInstance() {
        return instance;
    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return ExtensionPointFactoryBean.class;
    }

    @Override
    protected void parserSubElement(Element element, ParserContext parserContext,
                                    BeanDefinitionBuilder builder) {
        // determine nested/referred beans
        // only referred bean is supported now
        Object target = null;
        if (element.hasAttribute(REF)) {
            target = new RuntimeBeanReference(element.getAttribute(REF));
        }

        NodeList nl = element.getChildNodes();
        final Set<String> contributions = new LinkedHashSet<String>();
        // parse all sub elements
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element subElement = (Element) node;
                // sofa:object
                if (OBJECT.equals(subElement.getLocalName())) {
                    ParserUtils.parseCustomAttributes(subElement, parserContext, builder,
                        new ParserUtils.AttributeCallback() {

                            public void process(Element parent, Attr attribute,
                                                BeanDefinitionBuilder builder,
                                                ParserContext parserContext) {

                                String name = attribute.getLocalName();
                                if (CLASS.equals(name)) {
                                    contributions.add(attribute.getValue());
                                } else {
                                    builder.addPropertyValue(
                                        Conventions.attributeNameToPropertyName(name),
                                        attribute.getValue());
                                }
                            }
                        });
                } else {
                    if (element.hasAttribute(REF)) {
                        SofaLogger
                            .error("nested bean definition/reference cannot be used when attribute 'ref' is specified");
                    }
                    target = parserContext.getDelegate().parsePropertySubElement(subElement,
                        builder.getBeanDefinition());
                }
            }
        }
        builder.addPropertyValue(CONTRIBUTION, contributions);
        // do we have a bean reference ?
        if (target instanceof RuntimeBeanReference) {
            builder.addPropertyValue("targetBeanName",
                ((RuntimeBeanReference) target).getBeanName());
        }
        // or a nested bean? -- not supported yet
        else {
            builder.addPropertyValue("target", target);
        }
    }

    @Override
    public String supportTagName() {
        return "extension-point";
    }
}

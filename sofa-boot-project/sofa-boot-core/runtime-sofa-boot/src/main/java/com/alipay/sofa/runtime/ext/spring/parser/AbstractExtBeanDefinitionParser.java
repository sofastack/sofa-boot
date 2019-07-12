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
import org.springframework.core.Conventions;
import org.springframework.core.io.Resource;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.alipay.sofa.boot.spring.namespace.spi.SofaBootTagNameSupport;
import com.alipay.sofa.runtime.spi.util.ParserUtils;

/**
 * Common parser for extension and extension point
 *
 * @author yangyanzhao@alipay.com
 * @author yangyanzhao
 * @since 2.6.0
 */
public abstract class AbstractExtBeanDefinitionParser extends
                                                     AbstractSingleExtPointBeanDefinitionParser
                                                                                               implements
                                                                                               SofaBootTagNameSupport {
    public static final String  REF               = "ref";

    private static final String BEAN_CLASS_LOADER = "beanClassLoader";

    /**
     *
     * @param element       the XML element being parsed
     * @param parserContext the object encapsulating the current state of the parsing process
     * @param builder       used to define the <code>BeanDefinition</code>
     */
    @Override
    protected void doParse(Element element, ParserContext parserContext,
                           BeanDefinitionBuilder builder) {
        Resource res = parserContext.getReaderContext().getResource();

        builder.getBeanDefinition().setResource(res);
        builder.getRawBeanDefinition().setResource(res);

        configBeanClassLoader(parserContext, builder);

        // parse attributes
        parseAttribute(element, parserContext, builder);

        // parser subElement(i.e. <content/>)
        parserSubElement(element, parserContext, builder);
    }

    protected void configBeanClassLoader(ParserContext parserContext, BeanDefinitionBuilder builder) {
        ClassLoader beanClassLoader = parserContext.getReaderContext().getBeanClassLoader();
        builder.addPropertyValue(BEAN_CLASS_LOADER, beanClassLoader);
    }

    protected void parseAttribute(Element element, ParserContext parserContext,
                                  BeanDefinitionBuilder builder) {
        ParserUtils.parseCustomAttributes(element, parserContext, builder,
            new ParserUtils.AttributeCallback() {

                public void process(Element parent, Attr attribute, BeanDefinitionBuilder builder,
                                    ParserContext parserContext) {
                    String name = attribute.getLocalName();

                    // fallback mechanism
                    if (!REF.equals(name)) {
                        builder.addPropertyValue(Conventions.attributeNameToPropertyName(name),
                            attribute.getValue());
                    }

                }
            });
    }

    protected abstract void parserSubElement(Element element, ParserContext parserContext,
                                             BeanDefinitionBuilder builder);

    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }
}

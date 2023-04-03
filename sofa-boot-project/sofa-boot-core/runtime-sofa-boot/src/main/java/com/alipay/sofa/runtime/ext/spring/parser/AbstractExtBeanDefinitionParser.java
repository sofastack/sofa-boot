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

import com.alipay.sofa.boot.spring.namespace.spi.SofaBootTagNameSupport;
import com.alipay.sofa.runtime.ext.spring.ClassLoaderWrapper;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.Conventions;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Common parser for extension and extension point.
 *
 * @author yangyanzhao@alipay.com
 * @author yangyanzhao
 * @since 2.6.0
 */
public abstract class AbstractExtBeanDefinitionParser extends
                                                     AbstractSingleExtPointBeanDefinitionParser
                                                                                               implements
                                                                                               SofaBootTagNameSupport {
    public static final String  REF                       = "ref";

    private static final String BEAN_CLASS_LOADER_WRAPPER = "beanClassLoaderWrapper";

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
        // not support setClassLoder since spring framework 5.2.20.RELEASE

        builder
            .addPropertyValue(BEAN_CLASS_LOADER_WRAPPER, new ClassLoaderWrapper(beanClassLoader));
    }

    protected void parseAttribute(Element element, ParserContext parserContext,
                                  BeanDefinitionBuilder builder) {
        parseCustomAttributes(element, parserContext, builder,
                (parent, attribute, builder1, parserContext1) -> {
                    String name = attribute.getLocalName();

                    // fallback mechanism
                    if (!REF.equals(name)) {
                        builder1.addPropertyValue(Conventions.attributeNameToPropertyName(name),
                            attribute.getValue());
                    }

                });
    }

    /**
     * Actually parse sub element.
     *
     * @param element element
     * @param parserContext parserContext
     * @param builder builder
     */
    protected abstract void parserSubElement(Element element, ParserContext parserContext,
                                             BeanDefinitionBuilder builder);

    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }

    /**
     * Parse custom attributes, as ID, LAZY-INIT, DEPENDS-ON。
     *
     * @param element element
     * @param parserContext parser context
     * @param builder builder
     * @param callback callback
     */
    protected void parseCustomAttributes(Element element, ParserContext parserContext,
                                         BeanDefinitionBuilder builder, AttributeCallback callback) {
        NamedNodeMap attributes = element.getAttributes();

        for (int x = 0; x < attributes.getLength(); x++) {
            Attr attribute = (Attr) attributes.item(x);
            String name = attribute.getLocalName();

            if (BeanDefinitionParserDelegate.DEPENDS_ON_ATTRIBUTE.equals(name)) {
                builder.getBeanDefinition().setDependsOn(
                    (StringUtils.tokenizeToStringArray(attribute.getValue(),
                        BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS)));
            } else if (BeanDefinitionParserDelegate.LAZY_INIT_ATTRIBUTE.equals(name)) {
                builder.setLazyInit(Boolean.parseBoolean(attribute.getValue()));
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

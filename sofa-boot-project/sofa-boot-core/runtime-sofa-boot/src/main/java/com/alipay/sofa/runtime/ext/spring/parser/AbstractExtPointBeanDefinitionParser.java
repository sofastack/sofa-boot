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

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Abstract parser for extension and extension point.
 *
 * @author fengqi.lin
 * @since 2.6.0
 */
public abstract class AbstractExtPointBeanDefinitionParser implements BeanDefinitionParser {

    /**
     * Constant for the id attribute
     */
    public static final String ID_ATTRIBUTE = "id";

    @Override
    public final BeanDefinition parse(Element element, ParserContext parserContext) {
        AbstractBeanDefinition definition = parseInternal(element, parserContext);
        if (definition != null && !parserContext.isNested()) {
            try {
                String id = resolveId(element, definition, parserContext);
                if (!StringUtils.hasText(id)) {
                    parserContext.getReaderContext().error(
                        "Id is required for element '"
                                + parserContext.getDelegate().getLocalName(element)
                                + "' when used as a top-level tag", element);
                }
                BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, id);
                registerBeanDefinition(holder, parserContext.getRegistry());
                if (shouldFireEvents()) {
                    BeanComponentDefinition componentDefinition = new BeanComponentDefinition(
                        holder);
                    postProcessComponentDefinition(componentDefinition);
                    parserContext.registerComponent(componentDefinition);
                }
            } catch (BeanDefinitionStoreException ex) {
                parserContext.getReaderContext().error(ex.getMessage(), element);
                return null;
            }
        }
        return definition;
    }

    /**
     * Resolve the ID for the supplied {@link BeanDefinition}.
     * <p>When using {@link #shouldGenerateId generation}, a name is generated automatically.
     * Otherwise, the ID is extracted from the "id" attribute, potentially with a
     * {@link #shouldGenerateIdAsFallback() fallback} to a generated id.
     *
     * @param element       the element that the bean definition has been built from
     * @param definition    the bean definition to be registered
     * @param parserContext the object encapsulating the current state of the parsing process;
     *                      provides access to a {@link BeanDefinitionRegistry}
     * @return the resolved id
     * @throws BeanDefinitionStoreException if no unique name could be generated
     *                                      for the given bean definition
     */
    protected String resolveId(Element element, AbstractBeanDefinition definition,
                               ParserContext parserContext) throws BeanDefinitionStoreException {

        if (shouldGenerateId()) {
            return parserContext.getReaderContext().generateBeanName(definition);
        } else {
            String id = element.getAttribute(ID_ATTRIBUTE);
            if (!StringUtils.hasText(id) && shouldGenerateIdAsFallback()) {
                id = parserContext.getReaderContext().generateBeanName(definition);
            }
            return id;
        }
    }

    /**
     * Register the supplied {@link BeanDefinitionHolder bean} with the supplied
     * {@link BeanDefinitionRegistry registry}.
     * <p>Subclasses can override this method to control whether or not the supplied
     * {@link BeanDefinitionHolder bean} is actually even registered, or to
     * register even more beans.
     * <p>The default implementation registers the supplied {@link BeanDefinitionHolder bean}
     * with the supplied {@link BeanDefinitionRegistry registry} only if the <code>isNested</code>
     * parameter is <code>false</code>, because one typically does not want inner beans
     * to be registered as top level beans.
     *
     * @param definition the bean definition to be registered
     * @param registry   the registry that the bean is to be registered with
     * @see BeanDefinitionReaderUtils#registerBeanDefinition(BeanDefinitionHolder, BeanDefinitionRegistry)
     */
    protected void registerBeanDefinition(BeanDefinitionHolder definition,
                                          BeanDefinitionRegistry registry) {
        BeanDefinitionReaderUtils.registerBeanDefinition(definition, registry);
    }

    /**
     * Central template method to actually parse the supplied {@link Element}
     * into one or more {@link BeanDefinition BeanDefinitions}.
     *
     * @param element       the element that is to be parsed into one or more {@link BeanDefinition BeanDefinitions}
     * @param parserContext the object encapsulating the current state of the parsing process;
     *                      provides access to a {@link BeanDefinitionRegistry}
     * @return the primary {@link BeanDefinition} resulting from the parsing of the supplied {@link Element}
     * @see #parse(Element, ParserContext)
     * @see #postProcessComponentDefinition(BeanComponentDefinition)
     */
    protected abstract AbstractBeanDefinition parseInternal(Element element,
                                                            ParserContext parserContext);

    /**
     * Should an ID be generated instead of read from the passed in {@link Element}?
     * <p>Disabled by default; subclasses can override this to enable ID generation.
     * Note that this flag is about <i>always</i> generating an ID; the parser
     * won't even check for an "id" attribute in this case.
     *
     * @return whether the parser should always generate an id
     */
    protected boolean shouldGenerateId() {
        return false;
    }

    /**
     * Should an ID be generated instead if the passed in {@link Element} does not
     * specify an "id" attribute explicitly?
     * <p>Disabled by default; subclasses can override this to enable ID generation
     * as fallback: The parser will first check for an "id" attribute in this case,
     * only falling back to a generated ID if no value was specified.
     *
     * @return whether the parser should generate an id if no id was specified
     */
    protected boolean shouldGenerateIdAsFallback() {
        return false;
    }

    /**
     * Controls whether this parser is supposed to fire a
     * {@link BeanComponentDefinition}
     * event after parsing the bean definition.
     * <p>This implementation returns <code>true</code> by default; that is,
     * an event will be fired when a bean definition has been completely parsed.
     * Override this to return <code>false</code> in order to suppress the event.
     *
     * @return <code>true</code> in order to fire a component registration event
     * after parsing the bean definition; <code>false</code> to suppress the event
     * @see #postProcessComponentDefinition
     * @see org.springframework.beans.factory.parsing.ReaderContext#fireComponentRegistered
     */
    protected boolean shouldFireEvents() {
        return true;
    }

    /**
     * Hook method called after the primary parsing of a
     * {@link BeanComponentDefinition} but before the
     * {@link BeanComponentDefinition} has been registered with a
     * {@link BeanDefinitionRegistry}.
     * <p>Derived classes can override this method to supply any custom logic that
     * is to be executed after all the parsing is finished.
     * <p>The default implementation is a no-op.
     *
     * @param componentDefinition the {@link BeanComponentDefinition} that is to be processed
     */
    protected void postProcessComponentDefinition(BeanComponentDefinition componentDefinition) {
    }

}

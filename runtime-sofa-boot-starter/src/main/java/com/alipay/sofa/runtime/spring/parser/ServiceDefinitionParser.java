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

import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
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

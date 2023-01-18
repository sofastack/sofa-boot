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

import com.alipay.sofa.boot.spring.namespace.spi.SofaBootTagNameSupport;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import static com.alipay.sofa.runtime.async.AsyncInitMethodManager.ASYNC_INIT_METHOD_NAME;

/**
 * Async init definition decorator.
 *
 * @author qilong.zql
 * @author xuanbei
 * @since 2.6.0
 */
public class AsyncInitBeanDefinitionDecorator implements BeanDefinitionDecorator,
                                             SofaBootTagNameSupport {
    @Override
    public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition,
                                         ParserContext parserContext) {
        if (!Boolean.TRUE.toString().equalsIgnoreCase(((Attr) node).getValue())) {
            return definition;
        }

        String initMethodName = definition.getBeanDefinition().getInitMethodName();
        if (StringUtils.hasText(initMethodName)) {
            definition.getBeanDefinition().setAttribute(ASYNC_INIT_METHOD_NAME, initMethodName);
        }
        return definition;
    }

    @Override
    public String supportTagName() {
        return "async-init";
    }
}

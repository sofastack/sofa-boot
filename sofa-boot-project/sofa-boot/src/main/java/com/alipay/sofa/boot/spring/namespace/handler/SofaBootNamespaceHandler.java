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
package com.alipay.sofa.boot.spring.namespace.handler;

import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.boot.spring.namespace.spi.SofaBootTagNameSupport;
import org.slf4j.Logger;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import java.util.ServiceLoader;

/**
 * Implementation of {@link NamespaceHandlerSupport} to register {@link SofaBootTagNameSupport}.
 *
 * @author yangguanchao
 * @author qilong.zql
 * @since 2018/04/08
 */
public class SofaBootNamespaceHandler extends NamespaceHandlerSupport {

    private static final Logger logger = SofaBootLoggerFactory
                                           .getLogger(SofaBootNamespaceHandler.class);

    @Override
    public void init() {
        ServiceLoader<SofaBootTagNameSupport> serviceLoaderSofaBoot = ServiceLoader.load(SofaBootTagNameSupport.class);
        serviceLoaderSofaBoot.forEach(this::registerTagParser);
    }

    private void registerTagParser(SofaBootTagNameSupport tagNameSupport) {
        if (tagNameSupport instanceof BeanDefinitionParser) {
            registerBeanDefinitionParser(tagNameSupport.supportTagName(),
                (BeanDefinitionParser) tagNameSupport);
        } else if (tagNameSupport instanceof BeanDefinitionDecorator) {
            registerBeanDefinitionDecoratorForAttribute(tagNameSupport.supportTagName(),
                (BeanDefinitionDecorator) tagNameSupport);
        } else {
            logger
                .error(
                    "{} class supported [{}] parser are not instance of BeanDefinitionParser or BeanDefinitionDecorator",
                    tagNameSupport.getClass().getName(), tagNameSupport.supportTagName());
        }
    }
}

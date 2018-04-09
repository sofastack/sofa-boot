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
package com.alipay.sofa.infra.config.spring.namespace.handler;

import com.alipay.sofa.infra.config.spring.namespace.spi.SofaBootTagNameSupport;
import com.alipay.sofa.infra.log.InfraHealthCheckLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import java.util.ServiceLoader;

/**
 * SofaBootNamespaceHandler
 *
 * @author yangguanchao
 * @since 2018/04/08
 */
public class SofaBootNamespaceHandler extends NamespaceHandlerSupport {

    private static final Logger logger = InfraHealthCheckLoggerFactory
                                           .getLogger(SofaBootNamespaceHandler.class);

    @Override
    public void init() {
        ServiceLoader<SofaBootTagNameSupport> serviceLoaderSofaBoot = ServiceLoader
            .load(SofaBootTagNameSupport.class);
        //SOFABoot
        for (SofaBootTagNameSupport tagNameSupport : serviceLoaderSofaBoot) {
            this.registerTagParser(tagNameSupport);
        }
    }

    private void registerTagParser(SofaBootTagNameSupport tagNameSupport) {
        if (!(tagNameSupport instanceof BeanDefinitionParser)) {
            logger.error(tagNameSupport.getClass() + " tag name supported ["
                         + tagNameSupport.supportTagName() + "] parser are not instance of "
                         + BeanDefinitionParser.class);
            return;
        }
        String tagName = tagNameSupport.supportTagName();
        registerBeanDefinitionParser(tagName, (BeanDefinitionParser) tagNameSupport);
    }
}

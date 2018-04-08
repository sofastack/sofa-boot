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
package com.alipay.boot.middleware.config.spring.namespace.handler;

import com.alipay.boot.middleware.config.spring.namespace.spi.Slite2MiddlewareTagNameSupport;
import com.alipay.sofa.infra.config.spring.namespace.spi.SofaBootTagNameSupport;
import com.alipay.sofa.infra.log.InfraHealthCheckLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import java.util.ServiceLoader;

/**
 * SOFABoot 命名空间解析
 * <p>
 * Created by yangguanchao on 16/9/1.
 */
public class Slite2NamespaceHandler extends NamespaceHandlerSupport {

    private static final Logger logger = InfraHealthCheckLoggerFactory.getLogger(Slite2NamespaceHandler.class);

    @Override
    public void init() {
        ServiceLoader<Slite2MiddlewareTagNameSupport> serviceLoader = ServiceLoader.load(Slite2MiddlewareTagNameSupport.class);
        ServiceLoader<SofaBootTagNameSupport> serviceLoaderSofaBoot = ServiceLoader.load(SofaBootTagNameSupport.class);

        //compatible
        for (Slite2MiddlewareTagNameSupport tagNameSupport : serviceLoader) {
            this.registerTagParser(tagNameSupport);
        }
        //SOFABoot
        for (Slite2MiddlewareTagNameSupport tagNameSupport : serviceLoaderSofaBoot) {
            this.registerTagParser(tagNameSupport);
        }
    }

    private void registerTagParser(Slite2MiddlewareTagNameSupport tagNameSupport) {
        if (!(tagNameSupport instanceof BeanDefinitionParser)) {
            logger.error(tagNameSupport.getClass() + " tag name supported [" + tagNameSupport.supportTagName() + "] parser are not instance of " + BeanDefinitionParser.class);
            return;
        }
        String tagName = tagNameSupport.supportTagName();
        registerBeanDefinitionParser(tagName, (BeanDefinitionParser) tagNameSupport);
    }
}

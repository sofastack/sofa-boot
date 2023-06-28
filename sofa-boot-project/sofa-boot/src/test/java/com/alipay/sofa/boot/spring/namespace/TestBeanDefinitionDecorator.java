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
package com.alipay.sofa.boot.spring.namespace;

import com.alipay.sofa.boot.spring.namespace.spi.SofaBootTagNameSupport;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Node;

/**
 * @author huzijie
 * @version TestBeanDefinitionDecorator.java, v 0.1 2023年02月01日 11:53 AM huzijie Exp $
 */
public class TestBeanDefinitionDecorator implements SofaBootTagNameSupport, BeanDefinitionDecorator {

    @Override
    public String supportTagName() {
        return "test-decorator";
    }

    @Override
    public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition,
                                         ParserContext parserContext) {
        return null;
    }
}

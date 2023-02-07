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

import com.alipay.sofa.boot.spring.namespace.handler.SofaBootNamespaceHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaBootNamespaceHandler}.
 *
 * @author huzijie
 * @version SofaBootNamespaceHandlerTests.java, v 0.1 2023年02月01日 11:50 AM huzijie Exp $
 */
public class SofaBootNamespaceHandlerTests {

    @Test
    public void registerSofaBootNamespaceHandler() {
        SofaBootNamespaceHandler sofaBootNamespaceHandler = new SofaBootNamespaceHandler();
        sofaBootNamespaceHandler.init();

        Field parsersField = ReflectionUtils.findField(SofaBootNamespaceHandler.class, "parsers");
        parsersField.setAccessible(true);
        Map<String, BeanDefinitionParser> parsers = (Map<String, BeanDefinitionParser>) ReflectionUtils
            .getField(parsersField, sofaBootNamespaceHandler);

        Field decoratorsField = ReflectionUtils.findField(SofaBootNamespaceHandler.class,
            "attributeDecorators");
        decoratorsField.setAccessible(true);
        Map<String, BeanDefinitionDecorator> decorators = (Map<String, BeanDefinitionDecorator>) ReflectionUtils
            .getField(decoratorsField, sofaBootNamespaceHandler);

        assertThat(parsers.get("test-parser")).isNotNull();
        assertThat(decorators.get("test-decorator")).isNotNull();
        assertThat(parsers.get("test-normal")).isNull();
        assertThat(decorators.get("test-normal")).isNull();
    }
}

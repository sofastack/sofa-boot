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
package com.alipay.sofa.test.mock.injector;

import com.alipay.sofa.test.mock.injector.definition.Definition;
import com.alipay.sofa.test.mock.injector.parser.DefinitionParser;
import com.alipay.sofa.test.mock.injector.resolver.SofaBootBeanResolver;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

/**
 * @author pengym
 * @version InjectorMockTestExecutionListener.java, v 0.1 2023年08月07日 15:51 pengym
 */
public class InjectorMockTestExecutionListener extends AbstractTestExecutionListener {

    private static final String STUBBED_FIELDS = "_SOFA_BOOT_STUBBED_FIELDS";

    private static final String STUBBED_DEFINITIONS = "_SOFA_BOOT_STUBBED_DEFINITIONS";

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void prepareTestInstance(@Nonnull TestContext testContext) throws Exception {
        // Parse annotations and inject fields
        injectFields(testContext);
    }

    private void injectFields(TestContext testContext) {
        DefinitionParser parser = new DefinitionParser();
        parser.parse(testContext.getTestClass());

        Collection<BeanStubbedField> beanStubbedFields = createStubs(parser, testContext);
        // Register fields to be stubbed
        testContext.setAttribute(STUBBED_FIELDS, beanStubbedFields);

        injectTestClass(parser, testContext);
        // Persist
        testContext.setAttribute(STUBBED_DEFINITIONS, parser.getDefinitions());
    }

    private void injectTestClass(DefinitionParser parser, TestContext testContext) {
        parser.getDefinitions().forEach(definition -> {
            Field field = parser.getField(definition);
            if (field != null) {
               ReflectionUtils.makeAccessible(field);
               ReflectionUtils.setField(field, testContext.getTestInstance(), definition.getStub());
            }
        });
    }

    /**
     * create stubs and register to target fields.
     * @param parser
     * @param testContext
     */
    private Collection<BeanStubbedField> createStubs(DefinitionParser parser, TestContext testContext) {
        SofaBootBeanResolver sofaBootBeanResolver = new SofaBootBeanResolver(testContext.getApplicationContext());
        for(Definition definition : parser.getDefinitions()) {
            resolveStub(definition, sofaBootBeanResolver);
        }
        return null;
    }

    private void resolveStub(Definition definition, SofaBootBeanResolver sofaBootBeanResolver) {

    }

    @Override
    public void beforeTestMethod(@Nonnull TestContext testContext) {
        Set<Definition> stubbedDefinitions = (Set<Definition>) testContext.getAttribute(STUBBED_DEFINITIONS);
        if (!CollectionUtils.isEmpty(stubbedDefinitions)) {
            stubbedDefinitions.stream().filter(definition -> definition.getReset().equals(MockReset.BEFORE)).forEach(Definition::resetMock);
        }
    }


    @Override
    public void afterTestMethod(@Nonnull TestContext testContext) {
        Set<Definition> stubbedDefinitions = (Set<Definition>) testContext.getAttribute(STUBBED_DEFINITIONS);
        if (!CollectionUtils.isEmpty(stubbedDefinitions)) {
            stubbedDefinitions.stream().filter(definition -> definition.getReset().equals(MockReset.AFTER)).forEach(Definition::resetMock);
        }
    }

    @Override
    public void afterTestClass(TestContext testContext) {
        Collection<BeanStubbedField> beanStubbedFields = (Collection<BeanStubbedField>) testContext.getAttribute(STUBBED_FIELDS);
        if (!CollectionUtils.isEmpty(beanStubbedFields)) {
            beanStubbedFields.forEach(BeanStubbedField::reset);
        }
        testContext.removeAttribute(STUBBED_DEFINITIONS);
        testContext.removeAttribute(STUBBED_FIELDS);
    }

    private static class BeanStubbedField  {
        /**
         * Stub Definition
         */
        private final Definition definition;
        /**
         * Field to be stubbed
         */
        private final Field          field;
        /**
         * The original value of the stubbed field
         */
        private final Object         originalValue;
        /**
         * The bean to be stubbed
         */
        private final Object         bean;

        public BeanStubbedField(Definition definition, Field field, Object originalValue,
                                Object bean) {
            this.definition = definition;
            this.field = field;
            this.originalValue = originalValue;
            this.bean = bean;
        }

        public void inject() {
            ReflectionUtils.setField(field, bean, definition.getStub());
        }

        public void reset() {
            ReflectionUtils.setField(field, bean, originalValue);
        }
    }
}

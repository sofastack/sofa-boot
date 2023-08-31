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

import com.alipay.sofa.boot.log.ErrorCode;
import com.alipay.sofa.test.mock.injector.annotation.MockBeanInjector;
import com.alipay.sofa.test.mock.injector.annotation.SpyBeanInjector;
import com.alipay.sofa.test.mock.injector.definition.Definition;
import com.alipay.sofa.test.mock.injector.parser.DefinitionParser;
import com.alipay.sofa.test.mock.injector.resolver.BeanInjectorResolver;
import com.alipay.sofa.test.mock.injector.resolver.BeanInjectorStub;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * {@link TestExecutionListener} to enable {@link MockBeanInjector} and
 * {@link SpyBeanInjector} support.
 *
 * @author pengym
 * @version InjectorMockTestExecutionListener.java, v 0.1 2023年08月07日 15:51 pengym
 */
public class InjectorMockTestExecutionListener extends AbstractTestExecutionListener {

    static final String STUBBED_FIELDS      = "_SOFA_BOOT_STUBBED_FIELDS";

    static final String STUBBED_DEFINITIONS = "_SOFA_BOOT_STUBBED_DEFINITIONS";

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void prepareTestInstance(TestContext testContext) {
        // parse annotations and inject fields
        injectFields(testContext);
    }

    private void injectFields(TestContext testContext) {
        // create definitions form annotation
        DefinitionParser parser = new DefinitionParser();
        parser.parse(testContext.getTestClass());
        testContext.setAttribute(STUBBED_DEFINITIONS, parser.getDefinitions());

        // create stubs form definitions
        Collection<BeanInjectorStub> beanInjectorStubs = createStubs(parser, testContext);
        testContext.setAttribute(STUBBED_FIELDS, beanInjectorStubs);

        // inject mock/spy to test class fields
        injectTestClass(parser, testContext);
    }

    private void injectTestClass(DefinitionParser parser, TestContext testContext) {
        parser.getDefinitions().forEach(definition -> {
            Field field = parser.getField(definition);
            if (field != null) {
                Object target = testContext.getTestInstance();
                ReflectionUtils.makeAccessible(field);
                Object existingValue = ReflectionUtils.getField(field, target);
                Object injectValue =  definition.getMockInstance();
                if (existingValue == injectValue) {
                    return;
                }
                Assert.state(existingValue == null, () -> ErrorCode.convert("01-30000", existingValue, field, injectValue));
                ReflectionUtils.setField(field, target, injectValue);
            }
        });
    }

    private Collection<BeanInjectorStub> createStubs(DefinitionParser parser,
                                                     TestContext testContext) {
        Collection<BeanInjectorStub> beanInjectorStubs = new ArrayList<>();
        BeanInjectorResolver resolver = new BeanInjectorResolver(
            testContext.getApplicationContext());
        for (Definition definition : parser.getDefinitions()) {
            BeanInjectorStub field = resolver.resolveStub(definition);
            if (field != null) {
                beanInjectorStubs.add(field);
            }
        }
        return beanInjectorStubs;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void beforeTestMethod(TestContext testContext) {
        Set<Definition> stubbedDefinitions = (Set<Definition>) testContext.getAttribute(STUBBED_DEFINITIONS);
        if (!CollectionUtils.isEmpty(stubbedDefinitions)) {
            stubbedDefinitions.stream().filter(definition -> definition.getReset().equals(MockReset.BEFORE)).forEach(Definition::resetMock);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterTestMethod(TestContext testContext) {
        Set<Definition> stubbedDefinitions = (Set<Definition>) testContext.getAttribute(STUBBED_DEFINITIONS);
        if (!CollectionUtils.isEmpty(stubbedDefinitions)) {
            stubbedDefinitions.stream().filter(definition -> definition.getReset().equals(MockReset.AFTER)).forEach(Definition::resetMock);
        }
        Collection<BeanInjectorStub> beanStubbedFields = (Collection<BeanInjectorStub>) testContext.getAttribute(STUBBED_FIELDS);
        if (!CollectionUtils.isEmpty(beanStubbedFields)) {
            beanStubbedFields.forEach(BeanInjectorStub::reset);
        }
    }
}

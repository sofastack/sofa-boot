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
package com.alipay.sofa.test;

import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.test.model.definition.MockDefinition;
import com.alipay.sofa.test.model.definition.StubDefinition;
import com.alipay.sofa.test.model.stub.Stub;
import com.alipay.sofa.test.parser.SofaBootTestAnnotationParser;
import com.alipay.sofa.test.resolver.SofaBootReferenceResolver;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

import static java.util.Objects.isNull;

/**
 * @author pengym
 * @version SofaBootTestExecutionListener.java, v 0.1 2023年08月07日 15:51 pengym
 */
public class SofaBootTestExecutionListener extends AbstractTestExecutionListener {
    private static final Logger LOGGER         = SofaBootLoggerFactory
            .getLogger(SofaBootTestExecutionListener.class);
    private static final String STUBBED_FIELDS = "_SOFA_BOOT_STUBBED_FIELDS";

    private static ClientFactory clientFactory;

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void prepareTestInstance(@Nonnull TestContext testContext) throws Exception {
        info("Executing SofaBootTestExecutionListener#prepareTestInstance ...");

        // Initialize SofaBootReferenceResolver
        SofaBootReferenceResolver.init(testContext, clientFactory);

        // Reset all stubs
        StubbedField.resetAll();

        // Parse annotations
        SofaBootTestAnnotationParser parser = new SofaBootTestAnnotationParser();
        Map<StubDefinition, Set<Stub>> definitions = parser.parse(testContext.getTestClass());
        List<StubbedField> stubbedFields = new ArrayList<>();

        // Register fields to be stubbed
        for (Entry<StubDefinition, Set<Stub>> entry : definitions.entrySet()) {
            StubDefinition stubDefinition = entry.getKey();
            Class<?> stubClass = stubDefinition.extractClass();
            for (Stub target : entry.getValue()) {
                registerStubBeanFields(testContext, stubbedFields, stubDefinition, stubClass, target);
            }
            registerStubTestField(testContext, stubbedFields, stubDefinition, stubClass);
        }

        // Persist
        testContext.setAttribute(STUBBED_FIELDS, stubbedFields);
        super.prepareTestInstance(testContext);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void beforeTestMethod(@Nonnull TestContext testContext) throws Exception {
        info("Executing SofaBootTestExecutionListener#beforeTestMethod ...");

        ArrayList<StubbedField> stubbedFields = (ArrayList<StubbedField>) testContext.getAttribute(STUBBED_FIELDS);
        if (isNull(stubbedFields)) {
            return;
        }
        stubbedFields.forEach(field -> field.doStub(testContext));
        super.beforeTestMethod(testContext);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void afterTestMethod(@Nonnull TestContext testContext) throws Exception {
        info("Executing SofaBootTestExecutionListener#afterTestMethod ...");

        ArrayList<StubbedField> stubbedFields = (ArrayList<StubbedField>) testContext.getAttribute(STUBBED_FIELDS);
        if (isNull(stubbedFields)) {
            return;
        }
        stubbedFields.forEach(field -> field.doReset(testContext));
        super.afterTestMethod(testContext);
    }

    @SuppressWarnings({"rawtypes"})
    private static void registerStubBeanFields(TestContext testContext, List<StubbedField> stubbedFields,
                                               StubDefinition stubDefinition, Class<?> stubClass, Stub target) {
        Set targetBeans = target.resolveTargets(testContext);
        for (Object bean : targetBeans) {
            if (isNull(bean)) {continue;}

            Field beanField = ReflectionUtils.findField(bean.getClass(), stubDefinition.getFieldName(), stubClass);

            if (beanField == null) {
                String stubType = (stubDefinition instanceof MockDefinition) ? "mock" : "spy";
                LOGGER.error(
                        String.format("No fields can be found in Bean [%s] with type [%s] and name [%s], skipped injecting [%s] instance",
                                bean.getClass(), stubClass,
                                stubDefinition.getFieldName(), stubType));
                continue;
            }

            beanField.setAccessible(true);

            BeanStubbedField stubField = new BeanStubbedField(
                    stubDefinition,
                    beanField,
                    ReflectionUtils.getField(beanField, bean),
                    bean);
            stubbedFields.add(stubField);
        }
    }

    private static void registerStubTestField(TestContext testContext, List<StubbedField> stubbedFields, StubDefinition stubDefinition,
                                              Class<?> stubClass) {
        Field testField = ReflectionUtils.findField(testContext.getTestClass(), stubDefinition.getBeanName(), stubClass);
        Preconditions.checkNotNull(testField,
                String.format("Cannot find field in testClass: [%s] with type [%s] and/or name [%s]", testContext.getTestClass(),
                        stubDefinition.getBeanName(), stubClass));
        testField.setAccessible(true);

        TestStubbedField stubField = new TestStubbedField(stubDefinition, testField);
        stubbedFields.add(stubField);
    }

    public void setClientFactory(ClientFactory clientFactory) {
        if (clientFactory != null) {
            SofaBootTestExecutionListener.clientFactory = clientFactory;
        }
    }

    private static class StubbedField {
        /**
         * Cache stubs
         */
        protected static Map<StubDefinition, Object> stubs = new HashMap<>();

        /**
         * Perform the stub operation. The default implementation is empty.
         *
         * @param context {@link TestContext}
         */
        public void doStub(TestContext context) {
            // default implementation
        }

        /**
         * Reset the stubbed field. The default implementation is empty.
         *
         * @param context {@link TestContext}
         */
        public void doReset(TestContext context) {
            // default implementation
        }

        public static void resetAll() {
            stubs.clear();
        }
    }

    private static class BeanStubbedField extends StubbedField {
        /**
         * Stub Definition
         */
        private final StubDefinition definition;
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

        public BeanStubbedField(StubDefinition definition, Field field, Object originalValue, Object bean) {
            this.definition = definition;
            this.field = field;
            this.originalValue = originalValue;
            this.bean = bean;
        }

        @Override
        public void doStub(TestContext context) {
            Object stub = stubs.get(definition);
            if (isNull(stub)) {
                // Extract the proxied object before creating stubs. This is to ensure that the stubbing does actually work.
                Object extracted = SofaBootReferenceResolver.extractProxiedObject(originalValue);
                stub = definition.create(extracted);
                stubs.put(definition, stub);
            }

            ReflectionUtils.setField(field, bean, stub);

            String stubType = (definition instanceof MockDefinition) ? "Mocked" : "Spied";
            info(String.format("%s field [%s] for bean [%s] with value [%s]", stubType, field, bean, stub));
        }

        @Override
        public void doReset(TestContext context) {
            ReflectionUtils.setField(field, bean, originalValue);

            info(String.format("Reset field [%s] for bean [%s]", field, bean));
        }
    }

    private static class TestStubbedField extends StubbedField {
        /**
         * Stub Definition
         */
        private final StubDefinition definition;
        /**
         * Field to be stubbed
         */
        private final Field          field;

        public TestStubbedField(StubDefinition stubDefinition, Field testField) {
            this.definition = stubDefinition;
            this.field = testField;
        }

        @Override
        public void doStub(TestContext context) {
            Object stub = stubs.get(definition);

            if (isNull(stub)) {
                stub = definition.create(null);
                stubs.put(definition, stub);
            }
            ReflectionUtils.setField(field, context.getTestInstance(), stub);
        }
    }

    private static void info(String format, Object... args) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(format, args);
        }
    }
}
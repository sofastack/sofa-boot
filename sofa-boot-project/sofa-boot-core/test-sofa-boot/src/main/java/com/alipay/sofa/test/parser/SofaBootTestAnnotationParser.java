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
package com.alipay.sofa.test.parser;

import com.alipay.sofa.test.api.annotation.SofaMockBeanFor;
import com.alipay.sofa.test.api.annotation.SofaSpyBeanFor;
import com.alipay.sofa.test.model.definition.MockDefinition;
import com.alipay.sofa.test.model.definition.SpyDefinition;
import com.alipay.sofa.test.model.definition.StubDefinition;
import com.alipay.sofa.test.model.stub.SofaStub;
import com.alipay.sofa.test.model.stub.Stub;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.test.context.TestContext;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.lang.model.type.TypeVariable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Parser for processing the {@link SofaMockBeanFor} and {@link SofaSpyBeanFor} annotations for the testClass
 *
 * @author pengym
 * @version SofaBootTestAnnotationParser.java, v 0.1 2023年08月07日 17:52 pengym
 */
public class SofaBootTestAnnotationParser {
    /**
     * A Mock or Spy definition can be injected into multiple classes, hence one-to-many relationships are used here
     */
    private Map<StubDefinition, Set<Stub>> targets;

    /**
     * Parse the {@link SofaMockBeanFor} and {@link SofaSpyBeanFor} annotations for the testClass
     *
     * @param testClass The testClass, see {@link TestContext#getTestClass()}
     * @return An Unmodifiable view of StubDefinitions
     */
    public Map<StubDefinition, Set<Stub>> parse(@Nonnull Class<?> testClass) {
        Preconditions.checkNotNull(testClass);

        targets = new HashMap<>();
        ReflectionUtils.doWithFields(testClass, field -> parseTestField(field, testClass));

        // returns an unmodifiable view of the definitions
        return Collections.unmodifiableMap(targets);
    }

    private void parseTestField(Field testField, Class<?> testClass) {
        final MergedAnnotations mergedAnnotations = MergedAnnotations.from(testField, SearchStrategy.SUPERCLASS);

        mergedAnnotations
                .stream(SofaMockBeanFor.class)
                .map(MergedAnnotation::synthesize)
                .forEach(annotation -> parseSofaMockBeanAnnotation(annotation, testField, testClass));

        mergedAnnotations
                .stream(SofaSpyBeanFor.class)
                .map(MergedAnnotation::synthesize)
                .forEach(annotation -> parseSofaSpyBeanAnnotation(annotation, testField, testClass));
    }

    private void parseSofaMockBeanAnnotation(SofaMockBeanFor annotation, Field testField,
                                             Class<?> testClass) {
        ResolvableType typeToMock = deduceType(testField, testClass);
        String targetField = StringUtils.isNotBlank(annotation.field()) ? annotation.field()
            : testField.getName();
        StubDefinition definition = new MockDefinition(typeToMock, testField.getName(), targetField);
        SofaStub target = SofaStub.builder().type(annotation.target())
            .qualifier(annotation.qualifier()).uniqueId(annotation.uniqueId())
            .moduleName(annotation.module()).build();
        registerStubTarget(definition, target);
    }

    private void parseSofaSpyBeanAnnotation(SofaSpyBeanFor annotation, Field testField,
                                            Class<?> testClass) {
        ResolvableType typeToSpy = deduceType(testField, testClass);
        String targetField = StringUtils.isNotBlank(annotation.field()) ? annotation.field()
            : testField.getName();
        StubDefinition definition = new SpyDefinition(typeToSpy, testField.getName(), targetField);
        SofaStub target = SofaStub.builder().type(annotation.target())
            .qualifier(annotation.qualifier()).uniqueId(annotation.uniqueId())
            .moduleName(annotation.module()).build();
        registerStubTarget(definition, target);
    }

    private ResolvableType deduceType(Field testField, Class<?> source) {
        if (testField.getGenericType() instanceof TypeVariable) {
            return ResolvableType.forField(testField, source);
        } else {
            return ResolvableType.forField(testField);
        }
    }

    /**
     * Add a target to the definition map in a thread-safe manner
     *
     * @param definition definition
     * @param target     target to be added
     */
    private void registerStubTarget(StubDefinition definition, Stub target) {
        synchronized (this) {
            if (null == targets.get(definition)) {
                Set<Stub> set = new HashSet<>();
                targets.put(definition, set);
            }
            targets.get(definition).add(target);
        }
    }
}

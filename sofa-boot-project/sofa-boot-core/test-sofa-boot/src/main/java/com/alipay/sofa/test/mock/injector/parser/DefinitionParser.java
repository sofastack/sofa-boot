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
package com.alipay.sofa.test.mock.injector.parser;

import com.alipay.sofa.test.mock.injector.annotation.MockBeanInjector;
import com.alipay.sofa.test.mock.injector.annotation.SpyBeanInjector;
import com.alipay.sofa.test.mock.injector.definition.Definition;
import com.alipay.sofa.test.mock.injector.definition.MockDefinition;
import com.alipay.sofa.test.mock.injector.definition.QualifierDefinition;
import com.alipay.sofa.test.mock.injector.definition.SpyDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.test.context.TestContext;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Parser for processing the {@link MockBeanInjector} and {@link SpyBeanInjector} annotations for the testClass
 *
 * @author pengym
 * @version SofaBootTestAnnotationParser.java, v 0.1 2023年08月07日 17:52 pengym
 */
public class DefinitionParser {

    private final Set<Definition>        definitions;

    private final Map<Definition, Field> definitionFields;

    public DefinitionParser() {
        this.definitions = new LinkedHashSet<>();
        this.definitionFields = new LinkedHashMap<>();
    }

    /**
     * Parse the {@link MockBeanInjector} and {@link SpyBeanInjector} annotations for the testClass
     *
     * @param testClass The testClass, see {@link TestContext#getTestClass()}
     */
    public void parse(Class<?> testClass) {
        Assert.notNull(testClass, "testClass must not be null");
        ReflectionUtils.doWithFields(testClass, field -> parseTestField(field, testClass));
    }

    private void parseTestField(Field testField, Class<?> testClass) {
        final MergedAnnotations mergedAnnotations = MergedAnnotations.from(testField, SearchStrategy.SUPERCLASS);
        mergedAnnotations
                .stream(MockBeanInjector.class)
                .map(MergedAnnotation::synthesize)
                .forEach(annotation -> parseSofaMockBeanAnnotation(annotation, testField, testClass));

        mergedAnnotations
                .stream(SpyBeanInjector.class)
                .map(MergedAnnotation::synthesize)
                .forEach(annotation -> parseSofaSpyBeanAnnotation(annotation, testField, testClass));
    }

    private void parseSofaMockBeanAnnotation(MockBeanInjector annotation, Field field,
                                             Class<?> testClass) {
        ResolvableType typesToMock = deduceType(field, testClass);
        MockDefinition mockDefinition = new MockDefinition(typesToMock, annotation.name(),
            ResolvableType.forClass(annotation.value()), annotation.module(), annotation.field(),
            annotation.extraInterfaces(), annotation.answer(), annotation.serializable(),
            annotation.reset(), QualifierDefinition.forElement(field));
        registerDefinition(mockDefinition, field, "mock");
    }

    private void registerDefinition(Definition definition, Field field, String type) {
        boolean isNewDefinition = this.definitions.add(definition);
        Assert.state(isNewDefinition, () -> "Duplicate " + type + " definition " + definition);
        this.definitionFields.put(definition, field);
    }

    private void parseSofaSpyBeanAnnotation(SpyBeanInjector annotation, Field field,
                                            Class<?> testClass) {
        ResolvableType typesToMock = deduceType(field, testClass);
        SpyDefinition spyDefinition = new SpyDefinition(typesToMock, annotation.name(),
            ResolvableType.forClass(annotation.value()), annotation.module(), annotation.field(),
            annotation.reset(), annotation.proxyTargetAware(),
            QualifierDefinition.forElement(field));
        registerDefinition(spyDefinition, field, "spy");
    }

    private ResolvableType deduceType(Field field, Class<?> source) {
        return (field.getGenericType() instanceof java.lang.reflect.TypeVariable) ? ResolvableType
            .forField(field, source) : ResolvableType.forField(field);
    }

    public Set<Definition> getDefinitions() {
        return Collections.unmodifiableSet(this.definitions);
    }

    public Field getField(Definition definition) {
        return this.definitionFields.get(definition);
    }
}

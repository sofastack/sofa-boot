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
import com.alipay.sofa.test.mock.injector.definition.SpyDefinition;
import com.alipay.sofa.test.mock.injector.example.ExampleExtraInterface;
import com.alipay.sofa.test.mock.injector.example.ExampleService;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.boot.test.mock.mockito.MockReset;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link DefinitionParser}.
 *
 * @author huzijie
 * @version DefinitionParserTests.java, v 0.1 2023年08月21日 2:57 PM huzijie Exp $
 */
public class DefinitionParserTests {

    private final DefinitionParser parser = new DefinitionParser();

    @Test
    void parseSingleMockBeanInjector() {
        this.parser.parse(SingleMockBeanInjector.class);
        assertThat(getDefinitions()).hasSize(1);
        assertThat(getMockDefinition(0).getMockType().resolve()).isEqualTo(ExampleService.class);
    }

    @Test
    void parseMockBeanInjectorAttributes() {
        this.parser.parse(MockBeanInjectorAttributes.class);
        assertThat(getDefinitions()).hasSize(1);
        MockDefinition definition = getMockDefinition(0);
        assertThat(definition.getField()).isEqualTo("Field");
        assertThat(definition.getName()).isEqualTo("Name");
        assertThat(definition.getModule()).isEqualTo("Module");
        assertThat(definition.getMockType().resolve()).isEqualTo(ExampleService.class);
        assertThat(definition.getExtraInterfaces()).containsExactly(ExampleExtraInterface.class);
        assertThat(definition.getAnswer()).isEqualTo(Answers.RETURNS_SMART_NULLS);
        assertThat(definition.isSerializable()).isTrue();
        assertThat(definition.getReset()).isEqualTo(MockReset.NONE);
        assertThat(definition.getQualifier()).isNull();
    }

    @Test
    void parseDuplicateMockBeanInjector() {
        assertThatIllegalStateException().isThrownBy(() -> this.parser.parse(DuplicateMockBeanInjector.class))
                .withMessageContaining("Duplicate mock definition");
    }

    @Test
    void parseSingleSpyBeanInjector() {
        this.parser.parse(SingleSpyBeanInjector.class);
        assertThat(getDefinitions()).hasSize(1);
        assertThat(getSpyDefinition(0).getMockType().resolve()).isEqualTo(ExampleService.class);
    }

    @Test
    void parseSpyBeanInjectorAttributes() {
        this.parser.parse(SpyBeanInjectorAttributes.class);
        assertThat(getDefinitions()).hasSize(1);
        SpyDefinition definition = getSpyDefinition(0);
        assertThat(definition.getField()).isEqualTo("Field");
        assertThat(definition.getName()).isEqualTo("Name");
        assertThat(definition.getModule()).isEqualTo("Module");
        assertThat(definition.getMockType().resolve()).isEqualTo(ExampleService.class);
        assertThat(definition.getReset()).isEqualTo(MockReset.NONE);
        assertThat(definition.getQualifier()).isNull();
    }

    private MockDefinition getMockDefinition(int index) {
        return (MockDefinition) getDefinitions().get(index);
    }

    private SpyDefinition getSpyDefinition(int index) {
        return (SpyDefinition) getDefinitions().get(index);
    }

    private List<Definition> getDefinitions() {
        return new ArrayList<>(this.parser.getDefinitions());
    }

    static class SingleMockBeanInjector {

        @MockBeanInjector(field = "exampleService", type = ExampleService.class)
        private ExampleService exampleService;

    }

    static class MockBeanInjectorAttributes {

        @MockBeanInjector(field = "Field", module = "Module", name = "Name", type = ExampleService.class, extraInterfaces = ExampleExtraInterface.class, answer = Answers.RETURNS_SMART_NULLS, serializable = true, reset = MockReset.NONE)
        private ExampleService exampleService;
    }

    static class DuplicateMockBeanInjector {

        @MockBeanInjector(field = "exampleService", type = ExampleService.class)
        private ExampleService exampleServiceA;

        @MockBeanInjector(field = "exampleService", type = ExampleService.class)
        private ExampleService exampleServiceB;
    }

    static class SingleSpyBeanInjector {

        @SpyBeanInjector(field = "exampleService", type = ExampleService.class)
        private ExampleService exampleService;
    }

    static class SpyBeanInjectorAttributes {

        @SpyBeanInjector(field = "Field", module = "Module", name = "Name", type = ExampleService.class, reset = MockReset.NONE, proxyTargetAware = false)
        private ExampleService exampleService;
    }
}

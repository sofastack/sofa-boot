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
package com.alipay.sofa.test.mock.injector.definition;

import com.alipay.sofa.test.mock.injector.example.ExampleExtraInterface;
import com.alipay.sofa.test.mock.injector.example.ExampleService;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.mockito.mock.MockCreationSettings;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.core.ResolvableType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link MockDefinition}.
 *
 * @author huzijie
 * @version MockDefinitionTests.java, v 0.1 2023年08月21日 3:30 PM huzijie Exp $
 */
public class MockDefinitionTests {

    private static final ResolvableType EXAMPLE_SERVICE_TYPE = ResolvableType
                                                                 .forClass(ExampleService.class);

    @Test
	public void classToMockMustNotBeNull() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> new MockDefinition(null,null,null, null, null,
					null, null, false, null, null))
			.withMessageContaining("MockType must not be null");
	}

    @Test
    public void createWithDefaults() {
        MockDefinition definition = new MockDefinition(EXAMPLE_SERVICE_TYPE, null, null, null,
            "Field", null, null, false, null, null);
        assertThat(definition.getName()).isNull();
        assertThat(definition.getModule()).isNull();
        assertThat(definition.getField()).isEqualTo("Field");
        assertThat(definition.getMockType()).isEqualTo(EXAMPLE_SERVICE_TYPE);
        assertThat(definition.getType()).isEqualTo(null);
        assertThat(definition.getExtraInterfaces()).isEmpty();
        assertThat(definition.getAnswer()).isEqualTo(Answers.RETURNS_DEFAULTS);
        assertThat(definition.isSerializable()).isFalse();
        assertThat(definition.getReset()).isEqualTo(MockReset.AFTER);
        assertThat(definition.getQualifier()).isNull();
    }

    @Test
    public void createExplicit() {
        QualifierDefinition qualifier = mock(QualifierDefinition.class);
        MockDefinition definition = new MockDefinition(EXAMPLE_SERVICE_TYPE, "name",
            EXAMPLE_SERVICE_TYPE, "Module", "Field",
            new Class<?>[] { ExampleExtraInterface.class }, Answers.RETURNS_SMART_NULLS, true,
            MockReset.BEFORE, qualifier);
        assertThat(definition.getName()).isEqualTo("name");
        assertThat(definition.getModule()).isEqualTo("Module");
        assertThat(definition.getField()).isEqualTo("Field");
        assertThat(definition.getType()).isEqualTo(EXAMPLE_SERVICE_TYPE);
        assertThat(definition.getMockType()).isEqualTo(EXAMPLE_SERVICE_TYPE);
        assertThat(definition.getExtraInterfaces()).containsExactly(ExampleExtraInterface.class);
        assertThat(definition.getAnswer()).isEqualTo(Answers.RETURNS_SMART_NULLS);
        assertThat(definition.isSerializable()).isTrue();
        assertThat(definition.getReset()).isEqualTo(MockReset.BEFORE);
        assertThat(definition.getQualifier()).isEqualTo(qualifier);
    }

    @Test
    public void createMock() {
        MockDefinition definition = new MockDefinition(EXAMPLE_SERVICE_TYPE, "name",
            EXAMPLE_SERVICE_TYPE, "Module", "Field",
            new Class<?>[] { ExampleExtraInterface.class }, Answers.RETURNS_SMART_NULLS, true,
            MockReset.BEFORE, null);
        ExampleService mock = definition.createMock();
        MockCreationSettings<?> settings = Mockito.mockingDetails(mock).getMockCreationSettings();
        assertThat(mock).isEqualTo(definition.getMockInstance());
        assertThat(mock).isInstanceOf(ExampleService.class);
        assertThat(mock).isInstanceOf(ExampleExtraInterface.class);
        assertThat(settings.getDefaultAnswer()).isEqualTo(Answers.RETURNS_SMART_NULLS);
        assertThat(settings.isSerializable()).isTrue();
    }

}

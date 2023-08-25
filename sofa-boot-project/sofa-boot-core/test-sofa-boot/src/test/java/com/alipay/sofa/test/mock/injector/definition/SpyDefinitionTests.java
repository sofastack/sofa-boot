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

import com.alipay.sofa.test.mock.injector.example.ExampleService;
import com.alipay.sofa.test.mock.injector.example.ExampleServiceCaller;
import com.alipay.sofa.test.mock.injector.example.RealExampleService;
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
 * Tests for {@link SpyDefinition}.
 *
 * @author huzijie
 * @version SpyDefinitionTests.java, v 0.1 2023年08月21日 3:31 PM huzijie Exp $
 */
public class SpyDefinitionTests {

    private static final ResolvableType REAL_SERVICE_TYPE = ResolvableType
                                                              .forClass(ExampleService.class);

    @Test
	public void classToSpyMustNotBeNull() {
		assertThatIllegalArgumentException().isThrownBy(() -> new SpyDefinition(null, null, null,
						null, null,null, true, null))
			.withMessageContaining("MockType must not be null");
	}

    @Test
    public void createWithDefaults() {
        SpyDefinition definition = new SpyDefinition(REAL_SERVICE_TYPE, null, null, null, null,
            null, true, null);
        assertThat(definition.getName()).isNull();
        assertThat(definition.getType()).isNull();
        assertThat(definition.getModule()).isNull();
        assertThat(definition.getField()).isNull();
        assertThat(definition.getMockType()).isEqualTo(REAL_SERVICE_TYPE);
        assertThat(definition.getReset()).isEqualTo(MockReset.AFTER);
        assertThat(definition.getQualifier()).isNull();
    }

    @Test
    public void createExplicit() {
        QualifierDefinition qualifier = mock(QualifierDefinition.class);
        SpyDefinition definition = new SpyDefinition(REAL_SERVICE_TYPE, "name", REAL_SERVICE_TYPE,
            "Module", "Field", MockReset.BEFORE, false, qualifier);
        assertThat(definition.getName()).isEqualTo("name");
        assertThat(definition.getType()).isEqualTo(REAL_SERVICE_TYPE);
        assertThat(definition.getModule()).isEqualTo("Module");
        assertThat(definition.getField()).isEqualTo("Field");
        assertThat(definition.getMockType()).isEqualTo(REAL_SERVICE_TYPE);
        assertThat(definition.getReset()).isEqualTo(MockReset.BEFORE);
        assertThat(definition.getQualifier()).isEqualTo(qualifier);
    }

    @Test
    public void createSpy() {
        SpyDefinition definition = new SpyDefinition(REAL_SERVICE_TYPE, "name", REAL_SERVICE_TYPE,
            "Module", "Field", MockReset.BEFORE, false, null);
        RealExampleService spy = definition.createSpy(new RealExampleService("hello"));
        MockCreationSettings<?> settings = Mockito.mockingDetails(spy).getMockCreationSettings();
        assertThat(spy).isInstanceOf(ExampleService.class);
        assertThat(spy).isEqualTo(definition.getMockInstance());
        assertThat(settings.getDefaultAnswer()).isEqualTo(Answers.CALLS_REAL_METHODS);
    }

    @Test
	void createSpyWhenNullInstanceShouldThrowException() {
		SpyDefinition definition =  new SpyDefinition(REAL_SERVICE_TYPE,
				"name", REAL_SERVICE_TYPE, "Module", "Field", MockReset.BEFORE, false, null);
		assertThatIllegalArgumentException().isThrownBy(() -> definition.createSpy(null))
			.withMessageContaining("Instance must not be null");
	}

    @Test
	void createSpyWhenWrongInstanceShouldThrowException() {
		SpyDefinition definition =  new SpyDefinition(REAL_SERVICE_TYPE,
				"name", REAL_SERVICE_TYPE, "Module", "Field", MockReset.BEFORE, false, null);
		assertThatIllegalArgumentException().isThrownBy(() -> definition.createSpy(new ExampleServiceCaller(null)))
			.withMessageContaining("must be an instance of");
	}

    @Test
    void createSpyTwice() {
        SpyDefinition definition = new SpyDefinition(REAL_SERVICE_TYPE, "name", REAL_SERVICE_TYPE,
            "Module", "Field", MockReset.BEFORE, false, null);
        Object instance = new RealExampleService("hello");
        instance = definition.createSpy(instance);
        assertThat(instance).isEqualTo(definition.createSpy(instance));
    }

}

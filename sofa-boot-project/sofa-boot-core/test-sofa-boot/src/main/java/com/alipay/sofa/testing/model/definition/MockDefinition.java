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
package com.alipay.sofa.testing.model.definition;

import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import static org.mockito.Mockito.mock;

/**
 * @author pengym
 * @version MockDefinition.java, v 0.1 2023年08月07日 19:42 pengym
 */
public class MockDefinition extends StubDefinition {
    public MockDefinition(ResolvableType typeToMock, @Nullable String name, @Nullable String fieldName) {
        super(typeToMock, name, fieldName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T create(Object originalValue) {
        Class<?> cls = resolvableType.resolve();
        Assert.notNull(cls, "cannot resolve resolvableType");
        Assert.state(!Mockito.mockingDetails(originalValue).isMock(), "originalValue is already a mock");

        MockSettings settings = MockReset.withSettings(MockReset.AFTER);
        settings.name(beanName);
        return (T) mock(resolvableType.resolve(), settings);
    }

    @Override
    public String toString() {
        return "MockDefinition{" +
                "typeToMock=" + resolvableType +
                ", beanName='" + beanName + '\'' +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }
}
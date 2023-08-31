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

import org.mockito.Answers;
import org.mockito.MockSettings;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.core.ResolvableType;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;

/**
 * A complete definition that can be used to create a Mockito mock.
 *
 * @author pengym
 * @version MockDefinition.java, v 0.1 2023年08月07日 19:42 pengym
 */
public class MockDefinition extends Definition {

    private static final int    MULTIPLIER = 31;

    private final Set<Class<?>> extraInterfaces;

    private final Answers       answer;

    private final boolean       serializable;

    public MockDefinition(ResolvableType resolvableType, String name, ResolvableType type,
                          String module, String field, Class<?>[] extraInterfaces, Answers answer,
                          boolean serializable, MockReset reset, QualifierDefinition qualifier) {
        super(resolvableType, name, type, module, field, reset, qualifier);
        this.extraInterfaces = asClassSet(extraInterfaces);
        this.answer = (answer != null) ? answer : Answers.RETURNS_DEFAULTS;
        this.serializable = serializable;
    }

    public Set<Class<?>> getExtraInterfaces() {
        return extraInterfaces;
    }

    public Answers getAnswer() {
        return answer;
    }

    public boolean isSerializable() {
        return serializable;
    }

    @SuppressWarnings("unchecked")
    public <T> T createMock() {
        if (mockInstance == null) {
            MockSettings settings = MockReset.withSettings(getReset());
            if (!this.extraInterfaces.isEmpty()) {
                settings.extraInterfaces(ClassUtils.toClassArray(this.extraInterfaces));
            }
            settings.defaultAnswer(this.answer);
            if (this.serializable) {
                settings.serializable();
            }
            mockInstance = (T) mock(this.getMockType().resolve(), settings);
        }
        return (T) mockInstance;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        MockDefinition other = (MockDefinition) obj;
        boolean result = super.equals(obj);
        result = result && ObjectUtils.nullSafeEquals(this.extraInterfaces, other.extraInterfaces);
        result = result && ObjectUtils.nullSafeEquals(this.answer, other.answer);
        result = result && this.serializable == other.serializable;
        return result;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = MULTIPLIER * result + ObjectUtils.nullSafeHashCode(this.extraInterfaces);
        result = MULTIPLIER * result + ObjectUtils.nullSafeHashCode(this.answer);
        result = MULTIPLIER * result + Boolean.hashCode(this.serializable);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("mockType", this.getMockType())
            .append("name", this.getName()).append("type", this.getType())
            .append("module", this.getModule()).append("field", this.getField())
            .append("extraInterfaces", this.extraInterfaces).append("answer", this.answer)
            .append("serializable", this.serializable).append("reset", getReset())
            .append("qualifier", getQualifier()).toString();
    }

    private Set<Class<?>> asClassSet(Class<?>[] classes) {
        Set<Class<?>> classSet = new LinkedHashSet<>();
        if (classes != null) {
            classSet.addAll(Arrays.asList(classes));
        }
        return Collections.unmodifiableSet(classSet);
    }
}

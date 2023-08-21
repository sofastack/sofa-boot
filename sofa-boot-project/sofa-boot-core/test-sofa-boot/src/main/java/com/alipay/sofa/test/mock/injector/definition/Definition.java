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

import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Base class for {@link MockDefinition} or {@link SpyDefinition}
 *
 * @author pengym
 */
public abstract class Definition {

    private static final int          MULTIPLIER = 31;

    private final String              name;

    private final ResolvableType      type;

    private final String              module;

    private final String              field;

    private final MockReset           reset;

    private final QualifierDefinition qualifier;

    private final ResolvableType      mockType;

    protected Object                  mockInstance;

    public Definition(ResolvableType mockType, String name, ResolvableType type, String module,
                      String field, MockReset reset, QualifierDefinition qualifier) {
        Assert.notNull(mockType, "MockType must not be null");
        this.mockType = mockType;
        this.type = type;
        this.name = name;
        this.module = module;
        this.field = field;
        this.reset = (reset != null) ? reset : MockReset.AFTER;
        this.qualifier = qualifier;
    }

    public String getName() {
        return name;
    }

    public ResolvableType getType() {
        return type;
    }

    public String getModule() {
        return module;
    }

    public String getField() {
        return field;
    }

    public MockReset getReset() {
        return reset;
    }

    public ResolvableType getMockType() {
        return this.mockType;
    }

    public QualifierDefinition getQualifier() {
        return qualifier;
    }

    public Object getMockInstance() {
        return mockInstance;
    }

    public void resetMock() {
        if (mockInstance != null) {
            Mockito.reset(mockInstance);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        Definition other = (Definition) obj;
        boolean result = true;
        result = result && ObjectUtils.nullSafeEquals(this.mockType, other.mockType);
        result = result && ObjectUtils.nullSafeEquals(this.name, other.name);
        result = result && ObjectUtils.nullSafeEquals(this.type, other.type);
        result = result && ObjectUtils.nullSafeEquals(this.module, other.module);
        result = result && ObjectUtils.nullSafeEquals(this.field, other.field);
        result = result && ObjectUtils.nullSafeEquals(this.reset, other.reset);
        result = result && ObjectUtils.nullSafeEquals(this.qualifier, other.qualifier);
        return result;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = MULTIPLIER * result + ObjectUtils.nullSafeHashCode(this.mockType);
        result = MULTIPLIER * result + ObjectUtils.nullSafeHashCode(this.name);
        result = MULTIPLIER * result + ObjectUtils.nullSafeHashCode(this.type);
        result = MULTIPLIER * result + ObjectUtils.nullSafeHashCode(this.module);
        result = MULTIPLIER * result + ObjectUtils.nullSafeHashCode(this.field);
        result = MULTIPLIER * result + ObjectUtils.nullSafeHashCode(this.reset);
        result = MULTIPLIER * result + ObjectUtils.nullSafeHashCode(this.qualifier);
        return result;
    }
}

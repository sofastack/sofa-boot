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
package com.alipay.sofa.test.model.definition;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Base definition of a stub (e.g., {@link org.mockito.Mock} or {@link org.mockito.Spy})
 *
 * @author pengym
 */
public abstract class StubDefinition {
    @Getter
    protected final ResolvableType resolvableType;
    @Getter
    protected       String         beanName;
    @Getter
    protected       String         fieldName;

    public StubDefinition(ResolvableType resolvableType, @Nullable String beanName, @Nullable String fieldName) {
        Preconditions.checkNotNull(resolvableType, "resolvableType must not be null!");

        this.beanName = beanName;
        this.resolvableType = resolvableType;
        this.fieldName = fieldName;
    }

    /**
     * Create a stub
     * @param originalValue The original value of the object to be stubbed
     * @return Stub
     * @param <T> Type parameter
     */
    public abstract <T> T create(Object originalValue);

    public Class<?> extractClass() {
        Type type = resolvableType.getType();
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        return (Class<?>) type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        StubDefinition that = (StubDefinition) o;
        return new EqualsBuilder()
                .append(resolvableType, that.resolvableType)
                .append(beanName, that.beanName)
                .append(fieldName, that.fieldName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(resolvableType)
                .append(beanName)
                .append(fieldName)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "StubDefinition{" +
                "resolvableType=" + resolvableType +
                ", beanName='" + beanName + '\'' +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }
}
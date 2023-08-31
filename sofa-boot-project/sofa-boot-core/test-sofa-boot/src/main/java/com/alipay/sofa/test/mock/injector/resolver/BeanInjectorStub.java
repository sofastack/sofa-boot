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
package com.alipay.sofa.test.mock.injector.resolver;

import com.alipay.sofa.boot.log.ErrorCode;
import com.alipay.sofa.test.mock.injector.definition.Definition;
import com.alipay.sofa.test.mock.injector.definition.MockDefinition;
import com.alipay.sofa.test.mock.injector.definition.SpyDefinition;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * A bean injector stub which could transform target field value.
 *
 * @author huzijie
 * @version BeanStubbedField.java, v 0.1 2023年08月17日 7:31 PM huzijie Exp $
 */
public class BeanInjectorStub {

    /**
     * Mock/Spy Definition
     */
    private final Definition definition;

    /**
     * Field to inject
     */
    private final Field      field;

    /**
     * The original value of the injected field
     */
    private final Object     originalValue;

    /**
     * The bean to inject field
     */
    private final Object     bean;

    public BeanInjectorStub(Definition definition, Field field, Object bean) {
        this.definition = definition;
        this.field = field;
        this.bean = bean;
        ReflectionUtils.makeAccessible(field);
        this.originalValue = ReflectionUtils.getField(field, bean);
        if (definition instanceof SpyDefinition && this.originalValue == null) {
            throw new IllegalStateException(ErrorCode.convert("01-30001", field));
        }
    }

    /**
     * Inject the mock/spy to target field.
     */
    public void inject() {
        if (definition instanceof MockDefinition) {
            ReflectionUtils.setField(field, bean, ((MockDefinition) definition).createMock());
        } else if (definition instanceof SpyDefinition) {
            ReflectionUtils.setField(field, bean,
                ((SpyDefinition) definition).createSpy(originalValue));
        }
    }

    /**
     * Reset the target field value.
     */
    public void reset() {
        ReflectionUtils.setField(field, bean, originalValue);
    }
}

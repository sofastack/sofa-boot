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
import org.mockito.MockSettings;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * @author pengym
 * @version SpyDefinition.java, v 0.1 2023年08月07日 19:42 pengym
 */
public class SpyDefinition extends StubDefinition {
    public SpyDefinition(ResolvableType typeToSpy, @Nullable String beanName,
                         @Nullable String fieldName) {
        super(typeToSpy, beanName, fieldName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T create(Object originalValue) {
        Class<?> cls = this.resolvableType.resolve();
        Preconditions.checkNotNull(cls, "cannot resolve resolvableType");
        Assert.isInstanceOf(cls, originalValue);

        // Avoid spying a Spy object
        Preconditions.checkState(!Mockito.mockingDetails(originalValue).isSpy(),
            "originalValue is already a spy!");

        MockSettings settings = MockReset.withSettings(MockReset.AFTER).name(beanName)
            .spiedInstance(originalValue).defaultAnswer(Mockito.CALLS_REAL_METHODS);

        return (T) Mockito.mock(originalValue.getClass(), settings);
    }

    @Override
    public String toString() {
        return "SpyDefinition{" + "typeToSpy=" + resolvableType + ", beanName='" + beanName + '\''
               + ", fieldName='" + fieldName + '\'' + '}';
    }
}

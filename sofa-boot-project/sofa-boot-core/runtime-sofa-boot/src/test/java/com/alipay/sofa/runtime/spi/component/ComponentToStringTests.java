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
package com.alipay.sofa.runtime.spi.component;

import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ServiceImpl#toString()} and {@link ReferenceImpl#toString()}.
 *
 * @author huzijie
 * @author xuanbei
 */
public class ComponentToStringTests {

    @Test
    public void getString() throws Exception {

        ServiceImpl service = new ServiceImpl("uniqueId", ComponentToStringTests.class,
            new Object());
        assertThat(service.toString()).isEqualTo(
            "com.alipay.sofa.runtime.spi.component.ComponentToStringTests:uniqueId");

        service = new ServiceImpl("", ComponentToStringTests.class, new Object());
        assertThat(service.toString()).isEqualTo(
            "com.alipay.sofa.runtime.spi.component.ComponentToStringTests");

        ReferenceImpl reference = new ReferenceImpl("uniqueId", ComponentToStringTests.class,
            InterfaceMode.api, true);
        assertThat(reference.toString()).isEqualTo(
            "com.alipay.sofa.runtime.spi.component.ComponentToStringTests:uniqueId");

        reference = new ReferenceImpl(null, ComponentToStringTests.class, InterfaceMode.api, true);
        assertThat(reference.toString()).isEqualTo(
            "com.alipay.sofa.runtime.spi.component.ComponentToStringTests");
    }
}

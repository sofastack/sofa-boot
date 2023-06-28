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
package com.alipay.sofa.runtime.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ClientFactoryImpl}.
 *
 * @author huzijie
 * @version ClientFactoryImplTests.java, v 0.1 2023年04月10日 4:06 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class ClientFactoryImplTests {

    @InjectMocks
    private ClientFactoryImpl clientFactory;

    @Test
    public void registerClient() {
        // Given
        Class<?> clientType = String.class;
        Object clientInstance = "test";

        // When
        clientFactory.registerClient(clientType, clientInstance);

        // Then
        assertThat(clientFactory.getAllClientTypes()).contains(clientType);
    }

    @Test
    public void registerClientWithExistingClientType() {
        // Given
        Class<?> clientType = String.class;
        Object clientInstance1 = "test1";
        Object clientInstance2 = "test2";

        // When
        clientFactory.registerClient(clientType, clientInstance1);
        clientFactory.registerClient(clientType, clientInstance2);

        // Then
        assertThat(clientFactory.getAllClientTypes()).contains(clientType);
        assertThat(clientFactory.getClient(clientType)).isEqualTo(clientInstance1);
    }

    @Test
    public void getClient() {
        // Given
        Class<?> clientType = String.class;
        Object clientInstance = "test";
        clientFactory.registerClient(clientType, clientInstance);

        // When
        String client = (String) clientFactory.getClient(clientType);

        // Then
        assertThat(client).isEqualTo(clientInstance);
    }

    @Test
    public void getAllClientTypes() {
        // Given
        Class<?> clientType1 = String.class;
        Class<?> clientType2 = Integer.class;
        Object clientInstance1 = "test";
        Object clientInstance2 = 123;
        clientFactory.registerClient(clientType1, clientInstance1);
        clientFactory.registerClient(clientType2, clientInstance2);

        // When
        Collection<Class<?>> allClientTypes = clientFactory.getAllClientTypes();

        // Then
        assertThat(allClientTypes).containsExactlyInAnyOrder(clientType1, clientType2);
    }
}

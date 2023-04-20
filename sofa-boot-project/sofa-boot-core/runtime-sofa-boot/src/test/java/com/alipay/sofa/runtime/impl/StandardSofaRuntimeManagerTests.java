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

import com.alipay.sofa.boot.context.SofaGenericApplicationContext;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.spring.RuntimeShutdownAware;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.GenericApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StandardSofaRuntimeManager}.
 *
 * @author huzijie
 * @version StandardSofaRuntimeManagerTests.java, v 0.1 2023年04月10日 4:10 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class StandardSofaRuntimeManagerTests {

    @Mock
    private ClientFactoryInternal      clientFactoryInternal;

    private StandardSofaRuntimeManager sofaRuntimeManager;

    @BeforeEach
    public void setUp() {
        GenericApplicationContext applicationContext = new SofaGenericApplicationContext();
        sofaRuntimeManager = new StandardSofaRuntimeManager("testApp", this.getClass()
            .getClassLoader(), clientFactoryInternal);
        sofaRuntimeManager.setApplicationContext(applicationContext);
    }

    @Test
    public void getters() {
        assertThat(sofaRuntimeManager.getAppName()).isEqualTo("testApp");
        assertThat(sofaRuntimeManager.getAppClassLoader()).isEqualTo(
            this.getClass().getClassLoader());
        assertThat(sofaRuntimeManager.getClientFactoryInternal()).isEqualTo(clientFactoryInternal);
    }

    @Test
    public void shutdown() {
        // Given
        TestRuntimeShutdownAware shutdownAware1 = new TestRuntimeShutdownAware();
        TestRuntimeShutdownAware shutdownAware2 = new TestRuntimeShutdownAware();
        sofaRuntimeManager.registerShutdownAware(shutdownAware1);
        sofaRuntimeManager.registerShutdownAware(shutdownAware2);

        // When
        sofaRuntimeManager.shutdown();

        // Then
        assertThat(shutdownAware1.isShutdown()).isTrue();
        assertThat(shutdownAware2.isShutdown()).isTrue();
        assertThat(sofaRuntimeManager.getComponentManager()).isNull();
        assertThat(sofaRuntimeManager.getSofaRuntimeContext()).isNull();
        assertThat(sofaRuntimeManager.getClientFactoryInternal()).isNull();
    }

    @Test
    public void shutDownExternally() {
        // When
        sofaRuntimeManager.shutDownExternally();

        // Then
        assertThat(sofaRuntimeManager.getAppClassLoader()).isNull();
    }

    static class TestRuntimeShutdownAware implements RuntimeShutdownAware {

        private boolean shutdown = false;

        @Override
        public void shutdown() {
            shutdown = true;
        }

        public boolean isShutdown() {
            return shutdown;
        }
    }
}

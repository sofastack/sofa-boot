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
package com.alipay.sofa.boot.isle.spring;

import com.alipay.sofa.boot.isle.stage.PipelineContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;

/**
 * Tests for {@link SofaModuleContextLifecycle}.
 *
 * @author huzijie
 * @version SofaModuleContextLifecycleTests.java, v 0.1 2023年04月07日 11:04 AM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class SofaModuleContextLifecycleTests {

    @Mock
    private PipelineContext pipelineContext;

    @Test
    void start() {
        SofaModuleContextLifecycle lifecycle = new SofaModuleContextLifecycle(pipelineContext);
        lifecycle.start();
        Assertions.assertThat(lifecycle.isRunning()).isTrue();
    }

    @Test
    void stop() {
        SofaModuleContextLifecycle lifecycle = new SofaModuleContextLifecycle(pipelineContext);
        lifecycle.start();
        lifecycle.stop();
        Assertions.assertThat(lifecycle.isRunning()).isTrue();
    }

    @Test
    void getPhase() {
        SofaModuleContextLifecycle lifecycle = new SofaModuleContextLifecycle(pipelineContext);
        Assertions.assertThat(lifecycle.getPhase()).isEqualTo(-100);
    }

    @Test
    void exception() throws Exception {
        doThrow(new RuntimeException("failed to process")).when(pipelineContext).process();
        SofaModuleContextLifecycle lifecycle = new SofaModuleContextLifecycle(pipelineContext);
        Assertions.assertThatThrownBy(lifecycle::start).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("01-10000").hasRootCauseMessage("failed to process");
        Assertions.assertThat(lifecycle.isRunning()).isTrue();
    }
}

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
package com.alipay.sofa.boot.isle.stage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DefaultPipelineContext}.
 *
 * @author huzijie
 * @version DefaultPipelineContextTests.java, v 0.1 2023年02月02日 8:05 PM huzijie Exp $
 */
public class DefaultPipelineContextTests {

    @Test
    public void invokePipeline() throws Exception {
        TestPipelineStage stage1 = new TestPipelineStage(200);
        TestPipelineStage stage2 = new TestPipelineStage(100);
        TestPipelineStage stage3 = new TestPipelineStage(300);
        PipelineContext pipelineContext = new DefaultPipelineContext();
        pipelineContext.appendStage(stage1);
        pipelineContext.appendStage(stage2);
        pipelineContext.appendStage(stage3);
        pipelineContext.process();

        assertThat(stage2.getInvokeTime() < stage1.getInvokeTime()).isTrue();
        assertThat(stage1.getInvokeTime() < stage3.getInvokeTime()).isTrue();
    }

    @Test
    public void invokePipelineByList() throws Exception {
        TestPipelineStage stage1 = new TestPipelineStage(200);
        TestPipelineStage stage2 = new TestPipelineStage(100);
        TestPipelineStage stage3 = new TestPipelineStage(300);
        DefaultPipelineContext pipelineContext = new DefaultPipelineContext();
        pipelineContext.appendStages(List.of(stage1, stage2, stage3));
        pipelineContext.process();

        assertThat(stage2.getInvokeTime() < stage1.getInvokeTime()).isTrue();
        assertThat(stage1.getInvokeTime() < stage3.getInvokeTime()).isTrue();
        assertThat(pipelineContext.getStageList()).contains(stage1, stage2, stage3);
    }

    static class TestPipelineStage implements PipelineStage {

        private final int order;

        private long      invokeTime;

        public TestPipelineStage(int order) {
            this.order = order;
        }

        @Override
        public void process() throws Exception {
            Thread.sleep(1);
            this.invokeTime = System.currentTimeMillis();
        }

        @Override
        public String getName() {
            return "test";
        }

        @Override
        public int getOrder() {
            return this.order;
        }

        public long getInvokeTime() {
            return invokeTime;
        }

    }
}

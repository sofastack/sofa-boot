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
package com.alipay.sofa.isle.stage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xuanbei 18/3/12
 */
public class DefaultPipelineContext implements PipelineContext {
    private final List<PipelineStage> stageList = new ArrayList<>();

    @Override
    public void process() throws Exception {
        for (PipelineStage pipelineStage : stageList) {
            pipelineStage.process();
        }
    }

    @Override
    public PipelineContext appendStage(PipelineStage stage) {
        this.stageList.add(stage);
        return this;
    }

    @Override
    public PipelineContext appendStages(List<PipelineStage> stages) {
        for (PipelineStage pipelineStage : stages) {
            appendStage(pipelineStage);
        }
        return this;
    }
}

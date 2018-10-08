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

/**
 * {@link PipelineStage} is a stage of a {@link PipelineContext}.
 *
 * @author xuanbei 18/3/1
 */
public interface PipelineStage {
    /**
     * process this pipeline stage.
     *
     * @throws Exception if a failure occurred
     */
    void process() throws Exception;

    /**
     * get name of pipeline stage
     *
     * @return stage name
     */
    String getName();

    /**
     * get priority of pipeline stage.
     * when stages have same stage name, high priority will override low priority.
     *
     * @return priority of pipeline stage
     */
    int getPriority();
}

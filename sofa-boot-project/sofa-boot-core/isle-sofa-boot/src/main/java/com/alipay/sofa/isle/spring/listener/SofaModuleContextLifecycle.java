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
package com.alipay.sofa.isle.spring.listener;

import com.alipay.sofa.isle.stage.PipelineContext;
import com.alipay.sofa.runtime.log.SofaLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.Phased;
import org.springframework.context.SmartLifecycle;

/**
 * SofaModuleContextLifecycle implement to SmartLifecycle;
 * In order to ensure this class execute at first, this class override
 * ${@link Phased#getPhase()} method and return Integer.MIN_VALUE.
 *
 * @author xuanbei 18/3/12
 */
public class SofaModuleContextLifecycle implements SmartLifecycle {

    @Autowired
    private PipelineContext pipelineContext;

    @Override
    public void start() {
        try {
            pipelineContext.process();
        } catch (Throwable t) {
            SofaLogger.error("process pipeline error", t);
            throw new RuntimeException(t);
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE + 1;
    }
}

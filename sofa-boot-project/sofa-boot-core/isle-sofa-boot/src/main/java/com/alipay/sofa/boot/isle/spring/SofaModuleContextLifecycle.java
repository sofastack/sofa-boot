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
import com.alipay.sofa.boot.log.ErrorCode;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.SmartLifecycle;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of {@link SmartLifecycle} to process isle pipeline.
 *
 * SofaModuleContextLifecycle is a SmartLifecycle bean which will be started after ApplicationContext refreshed.
 * SofaModuleContextLifecycle has -100 phase, which overrides default the lowest order, and has the following effect:
 * 1. Start before default Lifecycle bean.
 * 2. Any other bean has option to precede.
 *
 * @author xuanbei 18/3/12
 * @author huzijie
 */
public class SofaModuleContextLifecycle implements SmartLifecycle {

    private static final Logger   LOGGER        = SofaBootLoggerFactory
                                                    .getLogger(SofaModuleContextLifecycle.class);

    private final AtomicBoolean   isleRefreshed = new AtomicBoolean(false);

    private final PipelineContext pipelineContext;

    public SofaModuleContextLifecycle(PipelineContext pipelineContext) {
        this.pipelineContext = pipelineContext;
    }

    @Override
    public void start() {
        if (isleRefreshed.compareAndSet(false, true)) {
            try {
                pipelineContext.process();
            } catch (Throwable t) {
                LOGGER.error(ErrorCode.convert("01-10000"), t);
                throw new RuntimeException(ErrorCode.convert("01-10000"), t);
            }
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isRunning() {
        return isleRefreshed.get();
    }

    @Override
    public int getPhase() {
        return -100;
    }
}

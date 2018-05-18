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

import com.alipay.sofa.healthcheck.startup.HealthCheckTrigger;
import com.alipay.sofa.isle.stage.DefaultPipelineContext;
import com.alipay.sofa.isle.stage.ModelCreatingStage;
import com.alipay.sofa.isle.stage.ModuleLogOutputStage;
import com.alipay.sofa.isle.stage.SpringContextInstallStage;
import com.alipay.sofa.runtime.spi.log.SofaLogger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SofaModuleContextRefreshedListener listens to ContextRefreshedEvent, this class should execute before ${@link HealthCheckTrigger}.
 * In order to ensure this class execute at first, this class implement ${@link PriorityOrdered} interface and return ${@link Ordered#HIGHEST_PRECEDENCE}.
 *
 * @author xuanbei 18/3/12
 */
public class SofaModuleContextRefreshedListener implements PriorityOrdered,
                                               ApplicationListener<ContextRefreshedEvent> {
    private static final AtomicBoolean INIT = new AtomicBoolean(false);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (INIT.compareAndSet(false, true)) {
            DefaultPipelineContext pipelineContext = new DefaultPipelineContext();
            pipelineContext.appendStage(new ModelCreatingStage((AbstractApplicationContext) event
                .getApplicationContext()));
            pipelineContext.appendStage(new SpringContextInstallStage(
                (AbstractApplicationContext) event.getApplicationContext()));
            pipelineContext.appendStage(new ModuleLogOutputStage((AbstractApplicationContext) event
                .getApplicationContext()));
            try {
                pipelineContext.process();
            } catch (Throwable t) {
                SofaLogger.error(t, "process pipeline error");
                throw new RuntimeException(t);
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

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

import com.alipay.sofa.isle.stage.DefaultPipelineContext;
import com.alipay.sofa.isle.stage.PipelineStage;
import com.alipay.sofa.runtime.spi.log.SofaLogger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SofaModuleContextRefreshedListener listens to ContextRefreshedEvent;
 * In order to ensure this class execute at first, this class implement
 * ${@link PriorityOrdered} interface and return ${@link Ordered#HIGHEST_PRECEDENCE}.
 *
 * @author xuanbei 18/3/12
 */
public class SofaModuleContextRefreshedListener implements PriorityOrdered,
                                               ApplicationListener<ContextRefreshedEvent>,
                                               ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (applicationContext.equals(event.getApplicationContext())) {
            DefaultPipelineContext pipelineContext = new DefaultPipelineContext();
            pipelineContext.appendStages(getPipelineStagesInApplicationContext(event
                .getApplicationContext()));
            try {
                pipelineContext.process();
            } catch (Throwable t) {
                SofaLogger.error(t, "process pipeline error");
                throw new RuntimeException(t);
            }
        }
    }

    private List<PipelineStage> getPipelineStagesInApplicationContext(ApplicationContext applicationContext) {
        Map<String, PipelineStage> beanName2PipelineStage = applicationContext
            .getBeansOfType(PipelineStage.class);
        Map<String, PipelineStage> pipelineName2PipelineStage = new HashMap<>();

        for (Map.Entry<String, PipelineStage> beanName2PipelineStageEntry : beanName2PipelineStage
            .entrySet()) {
            String pipelineName = beanName2PipelineStageEntry.getValue().getName();
            PipelineStage oldValue = pipelineName2PipelineStage.get(pipelineName);
            if (oldValue == null
                || oldValue.getPriority() < beanName2PipelineStageEntry.getValue().getPriority()) {
                pipelineName2PipelineStage
                    .put(pipelineName, beanName2PipelineStageEntry.getValue());
            }
        }

        List<PipelineStage> pipelineStages = new ArrayList<>(pipelineName2PipelineStage.values());
        OrderComparator.sort(pipelineStages);
        return pipelineStages;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

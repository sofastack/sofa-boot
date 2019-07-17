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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import com.alipay.sofa.isle.stage.PipelineContext;
import com.alipay.sofa.runtime.log.SofaLogger;

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

    @Autowired
    private PipelineContext    pipelineContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (applicationContext.equals(event.getApplicationContext())) {
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

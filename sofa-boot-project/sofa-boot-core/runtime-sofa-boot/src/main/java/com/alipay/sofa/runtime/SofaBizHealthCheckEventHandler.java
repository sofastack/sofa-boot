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
package com.alipay.sofa.runtime;

import com.alipay.sofa.ark.spi.event.biz.AfterBizStartupEvent;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.service.PriorityOrdered;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class SofaBizHealthCheckEventHandler implements EventHandler<AfterBizStartupEvent> {

    @Override
    public void handleEvent(AfterBizStartupEvent event) {
        doHealthCheck(event.getSource());
    }

    private void doHealthCheck(Biz biz) {
        SofaRuntimeManager sofaRuntimeManager = getSofaRuntimeManager(biz);
        if (!sofaRuntimeManager.isReadinessHealth()) {
            throw new RuntimeException("Health check failed.");
        }
    }

    private SofaRuntimeManager getSofaRuntimeManager(Biz biz) {
        for (SofaRuntimeManager sofaRuntimeManager : SofaFramework.getRuntimeSet()) {
            if (sofaRuntimeManager.getAppClassLoader().equals(biz.getBizClassLoader())) {
                return sofaRuntimeManager;
            }
        }
        throw new RuntimeException("No SofaRuntimeManager Found!");
    }

    @Override
    public int getPriority() {
        return PriorityOrdered.DEFAULT_PRECEDENCE;
    }
}
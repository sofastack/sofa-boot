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
package com.alipay.sofa.runtime.integration.service;

import com.alipay.sofa.ark.spi.constant.Constants;
import com.alipay.sofa.ark.spi.event.ArkEvent;
import com.alipay.sofa.ark.spi.event.BizEvent;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.SofaRuntimeProperties;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class SofaEventHandler implements EventHandler {
    @Override
    public void handleEvent(ArkEvent event) {
        if (Constants.BIZ_EVENT_TOPIC_UNINSTALL.equals(event.getTopic())) {
            doUninstallBiz((BizEvent) event);
        } else if (Constants.BIZ_EVENT_TOPIC_HEALTH_CHECK.equals(event.getTopic())) {
            doHealthCheck((BizEvent) event);
        }
    }

    private void doUninstallBiz(BizEvent event) {
        SofaRuntimeProperties.unRegisterProperties(event.getBiz().getBizClassLoader());
        SofaRuntimeManager sofaRuntimeManager = getSofaRuntimeManager(event.getBiz());
        SofaFramework.unRegisterSofaRuntimeManager(sofaRuntimeManager);
        sofaRuntimeManager.shutdown();
    }

    private void doHealthCheck(BizEvent event) {
        SofaRuntimeManager sofaRuntimeManager = getSofaRuntimeManager(event.getBiz());
        if (!sofaRuntimeManager.isHealthCheckPassed()) {
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
        return DEFAULT_PRECEDENCE;
    }
}
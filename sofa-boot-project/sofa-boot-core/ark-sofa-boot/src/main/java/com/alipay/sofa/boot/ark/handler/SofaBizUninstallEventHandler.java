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
package com.alipay.sofa.boot.ark.handler;

import com.alipay.sofa.ark.spi.event.biz.BeforeBizStopEvent;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.service.PriorityOrdered;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import com.alipay.sofa.boot.ark.SofaRuntimeContainer;
import com.alipay.sofa.boot.ark.invoke.DynamicJvmServiceProxyFinder;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;

/**
 * Implementation of {@link EventHandler<BeforeBizStopEvent>} to shut down {@link SofaRuntimeManager}.
 *
 * @author qilong.zql
 * @author huzijie
 * @since 2.5.0
 */
public class SofaBizUninstallEventHandler implements EventHandler<BeforeBizStopEvent> {

    @Override
    public void handleEvent(BeforeBizStopEvent event) {
        doUninstallBiz(event.getSource());
    }

    private void doUninstallBiz(Biz biz) {
        // Remove dynamic JVM service cache
        DynamicJvmServiceProxyFinder.getInstance().afterBizUninstall(biz);

        SofaRuntimeManager sofaRuntimeManager = SofaRuntimeContainer.getSofaRuntimeManager(biz
            .getBizClassLoader());

        if (sofaRuntimeManager == null) {
            throw new IllegalStateException("No SofaRuntimeManager match classLoader");
        }
        sofaRuntimeManager.shutDownExternally();
    }

    @Override
    public int getPriority() {
        return PriorityOrdered.DEFAULT_PRECEDENCE;
    }
}

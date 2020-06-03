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
package com.alipay.sofa.runtime.spring;

import com.alipay.sofa.ark.spi.event.AfterFinishStartupEvent;
import com.alipay.sofa.ark.spi.service.PriorityOrdered;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import com.alipay.sofa.runtime.invoke.DynamicJvmServiceProxyFinder;

/**
 * @author caojie.cj@antfin.com
 * @since 2019/11/28
 */
public class FinishStartupEventHandler implements EventHandler<AfterFinishStartupEvent> {
    @Override
    public void handleEvent(AfterFinishStartupEvent event) {
        DynamicJvmServiceProxyFinder.getDynamicJvmServiceProxyFinder().setHasFinishStartup(true);
    }

    @Override
    public int getPriority() {
        return PriorityOrdered.DEFAULT_PRECEDENCE;
    }
}

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
package com.alipay.sofa.runtime.integration.activator;

import com.alipay.sofa.ark.spi.model.PluginContext;
import com.alipay.sofa.ark.spi.service.PluginActivator;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import com.alipay.sofa.runtime.integration.invoke.DynamicJvmServiceProxyFinder;
import com.alipay.sofa.runtime.integration.service.SofaEventHandler;
import com.alipay.sofa.runtime.spi.log.SofaLogger;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class SofaRuntimeActivator implements PluginActivator {
    @Override
    public void start(PluginContext context) {
        SofaLogger.info("SofaRuntime is activating.");
        context.publishService(EventHandler.class, new SofaEventHandler());
        context.publishService(DynamicJvmServiceProxyFinder.class,
            DynamicJvmServiceProxyFinder.getDynamicJvmServiceProxyFinder());
    }

    @Override
    public void stop(PluginContext context) {
        // no op
    }
}
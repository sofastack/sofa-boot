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
package com.alipay.sofa.boot.ark;

import com.alipay.sofa.ark.spi.model.PluginContext;
import com.alipay.sofa.ark.spi.service.PluginActivator;
import com.alipay.sofa.ark.spi.service.event.EventAdminService;
import com.alipay.sofa.boot.ark.handler.AfterBizStartupEventHandler;
import com.alipay.sofa.boot.ark.handler.FinishStartupEventHandler;
import com.alipay.sofa.boot.ark.handler.SofaBizHealthCheckEventHandler;
import com.alipay.sofa.boot.ark.handler.SofaBizUninstallEventHandler;
import com.alipay.sofa.boot.ark.invoke.DynamicJvmServiceProxyFinder;

/**
 * Implementation of {@link PluginActivator} to register SOFABoot bridge components.
 *
 * @author qilong.zql
 * @author huzijie
 * @since 2.5.0
 */
public class SofaRuntimeActivator implements PluginActivator {

    @Override
    public void start(PluginContext context) {
        registerEventHandler(context);
        context.publishService(DynamicJvmServiceProxyFinder.class,
            DynamicJvmServiceProxyFinder.getInstance());
    }

    private void registerEventHandler(final PluginContext context) {
        EventAdminService eventAdminService = context.referenceService(EventAdminService.class)
            .getService();
        eventAdminService.register(new SofaBizUninstallEventHandler());
        eventAdminService.register(new SofaBizHealthCheckEventHandler());
        eventAdminService.register(new FinishStartupEventHandler());
        eventAdminService.register(new AfterBizStartupEventHandler());
    }

    @Override
    public void stop(PluginContext context) {
        // no op
    }
}

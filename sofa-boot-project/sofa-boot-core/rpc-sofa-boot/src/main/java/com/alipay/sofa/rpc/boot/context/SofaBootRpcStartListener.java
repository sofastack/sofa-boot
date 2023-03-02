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
package com.alipay.sofa.rpc.boot.context;

import com.alipay.sofa.rpc.boot.common.SofaBootRpcParserUtil;
import com.alipay.sofa.rpc.boot.config.FaultToleranceConfigurator;
import com.alipay.sofa.rpc.boot.container.ProviderConfigContainer;
import com.alipay.sofa.rpc.boot.container.RegistryConfigContainer;
import com.alipay.sofa.rpc.boot.container.ServerConfigContainer;
import com.alipay.sofa.rpc.boot.context.event.SofaBootRpcStartEvent;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.event.LookoutSubscriber;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * {@link SofaBootRpcStartEvent} 事件监听器.
 * 加载并初始化 SOFABoot RPC 需要的配置。
 * 开启服务器并发布服务元数据信息。
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class SofaBootRpcStartListener implements ApplicationListener<SofaBootRpcStartEvent> {

    protected final ProviderConfigContainer    providerConfigContainer;

    protected final FaultToleranceConfigurator faultToleranceConfigurator;

    protected final ServerConfigContainer      serverConfigContainer;

    protected final RegistryConfigContainer    registryConfigContainer;

    private String                             lookoutCollectDisable;

    public SofaBootRpcStartListener(ProviderConfigContainer providerConfigContainer,
                                    FaultToleranceConfigurator faultToleranceConfigurator,
                                    ServerConfigContainer serverConfigContainer,
                                    RegistryConfigContainer registryConfigContainer) {
        this.providerConfigContainer = providerConfigContainer;
        this.faultToleranceConfigurator = faultToleranceConfigurator;
        this.serverConfigContainer = serverConfigContainer;
        this.registryConfigContainer = registryConfigContainer;
    }

    @Override
    public void onApplicationEvent(SofaBootRpcStartEvent event) {
        //choose disable metrics lookout
        disableLookout();

        //extra info
        processExtra(event);

        //start fault tolerance
        if (faultToleranceConfigurator != null) {
            faultToleranceConfigurator.startFaultTolerance();
        }

        Collection<ProviderConfig> allProviderConfig = providerConfigContainer
            .getAllProviderConfig();
        if (!CollectionUtils.isEmpty(allProviderConfig)) {
            //start server
            serverConfigContainer.startServers();
        }

        //set allow all publish
        providerConfigContainer.setAllowPublish(true);

        //register registry
        providerConfigContainer.publishAllProviderConfig();

        //export dubbo
        providerConfigContainer.exportAllDubboProvideConfig();
    }

    protected void processExtra(SofaBootRpcStartEvent event) {

    }

    protected void disableLookout() {
        Boolean disable = SofaBootRpcParserUtil.parseBoolean(lookoutCollectDisable);

        if (disable != null) {
            LookoutSubscriber.setLookoutCollectDisable(disable);
        }
    }

    public void setLookoutCollectDisable(String lookoutCollectDisable) {
        this.lookoutCollectDisable = lookoutCollectDisable;
    }
}

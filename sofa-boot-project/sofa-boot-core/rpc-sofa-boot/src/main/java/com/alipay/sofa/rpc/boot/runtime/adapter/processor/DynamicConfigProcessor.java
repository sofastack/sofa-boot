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
package com.alipay.sofa.rpc.boot.runtime.adapter.processor;

import com.alipay.sofa.rpc.common.utils.StringUtils;
import com.alipay.sofa.rpc.config.AbstractInterfaceConfig;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.dynamic.DynamicConfigKeys;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author zhaowang
 * @version : DynamicConfigProcessor.java, v 0.1 2020年03月11日 11:55 上午 zhaowang Exp $
 */
public class DynamicConfigProcessor implements ConsumerConfigProcessor, ProviderConfigProcessor {

    @Value("${com.alipay.sofa.rpc.dynamic-config}")
    private String dynamicConfig;

    @Override
    public void processorConsumer(ConsumerConfig consumerConfig) {
        setDynamicConfig(consumerConfig);
    }

    @Override
    public void processorProvider(ProviderConfig providerConfig) {
        setDynamicConfig(providerConfig);
    }

    private void setDynamicConfig(AbstractInterfaceConfig config) {
        String configAlias = config.getParameter(DynamicConfigKeys.DYNAMIC_ALIAS);
        if (StringUtils.isBlank(configAlias) && StringUtils.isNotBlank(dynamicConfig)) {
            config.setParameter(DynamicConfigKeys.DYNAMIC_ALIAS, dynamicConfig);
        }
    }

    public String getDynamicConfig() {
        return dynamicConfig;
    }

    public void setDynamicConfig(String dynamicConfig) {
        this.dynamicConfig = dynamicConfig;
    }
}
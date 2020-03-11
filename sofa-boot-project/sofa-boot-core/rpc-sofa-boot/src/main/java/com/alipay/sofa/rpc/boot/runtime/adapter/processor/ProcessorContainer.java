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

import com.alipay.sofa.rpc.common.utils.CommonUtils;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;

import java.util.List;

/**
 * @author zhaowang
 * @version : ProcessorContainer.java, v 0.1 2020年03月11日 10:28 上午 zhaowang Exp $
 */
public class ProcessorContainer {

    private List<ProviderConfigProcessor> providerProcessors;

    private List<ConsumerConfigProcessor> consumerProcessors;

    public ProcessorContainer(List<ProviderConfigProcessor> providerProcessors,
                              List<ConsumerConfigProcessor> consumerProcessors) {
        this.providerProcessors = providerProcessors;
        this.consumerProcessors = consumerProcessors;
    }

    public void processorConsumer(ConsumerConfig consumerConfig) {
        if (CommonUtils.isNotEmpty(consumerProcessors)) {
            for (ConsumerConfigProcessor consumerProcessor : consumerProcessors) {
                consumerProcessor.processorConsumer(consumerConfig);
            }
        }
    }

    public void processorProvider(ProviderConfig providerConfig) {
        if (CommonUtils.isNotEmpty(providerProcessors)) {
            for (ProviderConfigProcessor providerProcessor : providerProcessors) {
                providerProcessor.processorProvider(providerConfig);
            }
        }

    }

}
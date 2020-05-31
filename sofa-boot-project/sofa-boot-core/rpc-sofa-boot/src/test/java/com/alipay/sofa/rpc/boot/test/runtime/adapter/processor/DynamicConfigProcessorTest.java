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
package com.alipay.sofa.rpc.boot.test.runtime.adapter.processor;

import com.alipay.sofa.boot.util.StringUtils;
import com.alipay.sofa.rpc.boot.runtime.adapter.processor.DynamicConfigProcessor;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.dynamic.DynamicConfigKeys;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhaowang
 * @version : DynamicConfigProcessorTest.java, v 0.1 2020年03月11日 3:21 下午 zhaowang Exp $
 */
public class DynamicConfigProcessorTest {

    public static final String     CONFIG    = "config";
    public static final String     ANOTHER   = "another";
    private DynamicConfigProcessor processor = new DynamicConfigProcessor();

    @Test
    public void test() {
        processor.setDynamicConfig(CONFIG);

        ConsumerConfig consumerConfig = new ConsumerConfig();
        processor.processorConsumer(consumerConfig);
        Assert.assertEquals(CONFIG, consumerConfig.getParameter(DynamicConfigKeys.DYNAMIC_ALIAS));

        ProviderConfig providerConfig = new ProviderConfig();
        processor.processorProvider(providerConfig);
        Assert.assertEquals(CONFIG, providerConfig.getParameter(DynamicConfigKeys.DYNAMIC_ALIAS));

        consumerConfig = new ConsumerConfig();
        consumerConfig.setParameter(DynamicConfigKeys.DYNAMIC_ALIAS, ANOTHER);
        processor.processorConsumer(consumerConfig);
        Assert.assertEquals(ANOTHER, consumerConfig.getParameter(DynamicConfigKeys.DYNAMIC_ALIAS));

        providerConfig = new ProviderConfig();
        providerConfig.setParameter(DynamicConfigKeys.DYNAMIC_ALIAS, ANOTHER);
        processor.processorProvider(providerConfig);
        Assert.assertEquals(ANOTHER, providerConfig.getParameter(DynamicConfigKeys.DYNAMIC_ALIAS));

        processor.setDynamicConfig("");
        consumerConfig = new ConsumerConfig();
        processor.processorConsumer(consumerConfig);
        Assert.assertFalse(StringUtils.hasText(consumerConfig
            .getParameter(DynamicConfigKeys.DYNAMIC_ALIAS)));

        providerConfig = new ProviderConfig();
        processor.processorProvider(providerConfig);
        Assert.assertFalse(StringUtils.hasText(providerConfig
            .getParameter(DynamicConfigKeys.DYNAMIC_ALIAS)));

    }

}
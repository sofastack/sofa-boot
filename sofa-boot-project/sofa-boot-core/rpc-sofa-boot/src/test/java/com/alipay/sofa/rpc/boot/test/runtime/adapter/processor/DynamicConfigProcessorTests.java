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

import com.alipay.sofa.rpc.boot.runtime.adapter.processor.DynamicConfigProcessor;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.dynamic.DynamicConfigKeys;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DynamicConfigProcessor}.
 * 
 * @author zhaowang
 * @version : DynamicConfigProcessorTest.java, v 0.1 2020年03月11日 3:21 下午 zhaowang Exp $
 */
public class DynamicConfigProcessorTests {

    public static final String     CONFIG    = "config";
    public static final String     ANOTHER   = "another";
    private DynamicConfigProcessor processor = new DynamicConfigProcessor("");

    @Test
    public void checkConfig() {
        processor.setDynamicConfig(CONFIG);

        ConsumerConfig consumerConfig = new ConsumerConfig();
        processor.processorConsumer(consumerConfig);
        assertThat(CONFIG).isEqualTo(consumerConfig.getParameter(DynamicConfigKeys.DYNAMIC_ALIAS));

        ProviderConfig providerConfig = new ProviderConfig();
        processor.processorProvider(providerConfig);
        assertThat(CONFIG).isEqualTo(providerConfig.getParameter(DynamicConfigKeys.DYNAMIC_ALIAS));

        consumerConfig = new ConsumerConfig();
        consumerConfig.setParameter(DynamicConfigKeys.DYNAMIC_ALIAS, ANOTHER);
        processor.processorConsumer(consumerConfig);
        assertThat(ANOTHER).isEqualTo(consumerConfig.getParameter(DynamicConfigKeys.DYNAMIC_ALIAS));

        providerConfig = new ProviderConfig();
        providerConfig.setParameter(DynamicConfigKeys.DYNAMIC_ALIAS, ANOTHER);
        processor.processorProvider(providerConfig);
        assertThat(ANOTHER).isEqualTo(providerConfig.getParameter(DynamicConfigKeys.DYNAMIC_ALIAS));

        processor.setDynamicConfig("");
        consumerConfig = new ConsumerConfig();
        processor.processorConsumer(consumerConfig);
        assertThat(
            StringUtils.hasText(consumerConfig.getParameter(DynamicConfigKeys.DYNAMIC_ALIAS)))
            .isFalse();

        providerConfig = new ProviderConfig();
        processor.processorProvider(providerConfig);
        assertThat(
            StringUtils.hasText(providerConfig.getParameter(DynamicConfigKeys.DYNAMIC_ALIAS)))
            .isFalse();
    }

}

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

import com.alipay.sofa.rpc.boot.runtime.adapter.processor.ConsumerMockProcessor;
import com.alipay.sofa.rpc.common.MockMode;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConsumerMockProcessor}.
 *
 * @author zhaowang
 * @version : ConsumerMockProcessorTest.java, v 0.1 2020年03月11日 2:32 下午 zhaowang Exp $
 */
public class ConsumerMockProcessorTests {

    public static final String          MOCK_URL  = "mock";
    private final ConsumerMockProcessor processor = new ConsumerMockProcessor("");

    @BeforeEach
    public void before() {
        processor.setMockUrl(MOCK_URL);
    }

    @Test
    public void processor() {
        ConsumerConfig consumerConfig = new ConsumerConfig();
        processor.processorConsumer(consumerConfig);
        assertThat(MOCK_URL).isEqualTo(consumerConfig.getParameter(ConsumerMockProcessor.MOCK_URL));
        assertThat(MockMode.REMOTE).isEqualTo(consumerConfig.getMockMode());

        processor.setMockUrl("");
        ConsumerConfig consumerConfig2 = new ConsumerConfig();
        processor.processorConsumer(consumerConfig2);
        assertThat(StringUtils.hasText(consumerConfig2.getMockMode())).isFalse();
        assertThat(
            StringUtils.hasText(consumerConfig2.getParameter(ConsumerMockProcessor.MOCK_URL)))
            .isFalse();
    }

    @Test
    public void testMockSet() {
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setMockMode(MockMode.LOCAL);
        processor.processorConsumer(consumerConfig);
        assertThat(StringUtils.hasText(consumerConfig.getParameter(ConsumerMockProcessor.MOCK_URL)))
            .isFalse();
        assertThat(MockMode.LOCAL).isEqualTo(consumerConfig.getMockMode());

        consumerConfig = new ConsumerConfig();
        consumerConfig.setMockMode(MockMode.REMOTE);
        consumerConfig.setParameter(ConsumerMockProcessor.MOCK_URL, "another");
        processor.processorConsumer(consumerConfig);
        assertThat("another")
            .isEqualTo(consumerConfig.getParameter(ConsumerMockProcessor.MOCK_URL));
        assertThat(MockMode.REMOTE).isEqualTo(consumerConfig.getMockMode());
    }

}
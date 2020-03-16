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

import com.alipay.sofa.boot.util.StringUtils;
import com.alipay.sofa.rpc.common.MockMode;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author zhaowang
 * @version : ConsumerMockProcessor.java, v 0.1 2020年03月11日 11:24 上午 zhaowang Exp $
 */
public class ConsumerMockProcessor implements ConsumerConfigProcessor {

    public static final String MOCK_URL = "mockUrl";
    @Value("${com.alipay.sofa.rpc.mock-url}")
    private String             mockUrl;

    @Override
    public void processorConsumer(ConsumerConfig consumerConfig) {
        String mockMode = consumerConfig.getMockMode();
        if (StringUtils.hasText(mockUrl)
            && (!StringUtils.hasText(mockMode) || MockMode.REMOTE.equals(mockMode))) {
            String originMockUrl = consumerConfig.getParameter(MOCK_URL);
            if (!StringUtils.hasText(originMockUrl)) {
                consumerConfig.setMockMode(MockMode.REMOTE);
                consumerConfig.setParameter(MOCK_URL, mockUrl);
            }
        }
    }

    public String getMockUrl() {
        return mockUrl;
    }

    public void setMockUrl(String mockUrl) {
        this.mockUrl = mockUrl;
    }
}
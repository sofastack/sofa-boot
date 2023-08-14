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
package com.alipay.sofa.boot.autoconfigure.test;

import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.test.SofaBootTestExecutionListener;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestExecutionListener;

/**
 * {@link EnableAutoConfiguration Auto-configuraiton} for sofa test
 *
 * @author pengym
 * @version SofaTestAutoConfiguration.java, v 0.1 2023年08月07日 15:45 pengym
 */
@AutoConfigureAfter(SofaRuntimeAutoConfiguration.class)
@AutoConfiguration
public class SofaTestAutoConfiguration implements ClientFactoryAware {
    private ClientFactory clientFactory;

    @Bean
    @ConditionalOnMissingBean
    public TestExecutionListener sofaBootTestExecutionListener() {
        SofaBootTestExecutionListener listener = new SofaBootTestExecutionListener();
        listener.setClientFactory(clientFactory);
        return listener;
    }

    @Override
    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }
}
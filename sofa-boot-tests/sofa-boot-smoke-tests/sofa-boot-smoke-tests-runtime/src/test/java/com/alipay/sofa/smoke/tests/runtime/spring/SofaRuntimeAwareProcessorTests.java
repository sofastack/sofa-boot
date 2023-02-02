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
package com.alipay.sofa.smoke.tests.runtime.spring;

import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.aware.ExtensionClientAware;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.api.client.ExtensionClient;
import com.alipay.sofa.runtime.filter.JvmFilter;
import com.alipay.sofa.runtime.filter.JvmFilterContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.spring.RuntimeShutdownAware;
import com.alipay.sofa.runtime.spi.spring.SofaRuntimeContextAware;
import com.alipay.sofa.runtime.spring.SofaRuntimeAwareProcessor;
import com.alipay.sofa.smoke.tests.runtime.RuntimeSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SofaRuntimeAwareProcessor}.
 *
 * @author huzijie
 * @version SofaRuntimeAwareProcessorTests.java, v 0.1 2023年02月02日 4:02 PM huzijie Exp $
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class)
@Import(SofaRuntimeAwareProcessorTests.SofaRuntimeAwareProcessorTestConfiguration.class)
public class SofaRuntimeAwareProcessorTests {

    @Autowired
    private SofaRuntimeAwareBean sofaRuntimeAwareBean;

    @Autowired
    private SofaRuntimeContext   sofaRuntimeContext;

    @Test
    public void clientFactory() {
        assertThat(sofaRuntimeAwareBean.getClientFactory()).isNotNull();
    }

    @Test
    public void extensionClient() {
        assertThat(sofaRuntimeAwareBean.getExtensionClient()).isNotNull();
    }

    @Test
    public void sofaRuntimeContext() {
        assertThat(sofaRuntimeAwareBean.getSofaRuntimeContext()).isNotNull();
    }

    @Test
    public void jvmFilter() {
        assertThat(sofaRuntimeContext.getJvmFilterHolder().getJvmFilters()).contains(
            sofaRuntimeAwareBean);
    }

    @Test
    public void runtimeShutdown() {
        assertThat(sofaRuntimeAwareBean.isTrigger()).isFalse();
        sofaRuntimeContext.getSofaRuntimeManager().shutdown();
        assertThat(sofaRuntimeAwareBean.isTrigger()).isTrue();
    }

    @TestConfiguration
    static class SofaRuntimeAwareProcessorTestConfiguration {

        @Bean
        public SofaRuntimeAwareBean sofaRuntimeAwareBean() {
            return new SofaRuntimeAwareBean();
        }
    }

    static class SofaRuntimeAwareBean implements SofaRuntimeContextAware, ClientFactoryAware,
                                     ExtensionClientAware, JvmFilter, RuntimeShutdownAware {

        private ClientFactory      clientFactory;
        private ExtensionClient    extensionClient;
        private SofaRuntimeContext sofaRuntimeContext;
        private boolean            trigger;

        @Override
        public void setClientFactory(ClientFactory clientFactory) {
            this.clientFactory = clientFactory;
        }

        @Override
        public void setExtensionClient(ExtensionClient extensionClient) {
            this.extensionClient = extensionClient;
        }

        @Override
        public boolean before(JvmFilterContext context) {
            return false;
        }

        @Override
        public boolean after(JvmFilterContext context) {
            return false;
        }

        @Override
        public void shutdown() {
            trigger = true;
        }

        @Override
        public void setSofaRuntimeContext(SofaRuntimeContext sofaRuntimeContext) {
            this.sofaRuntimeContext = sofaRuntimeContext;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        public ClientFactory getClientFactory() {
            return clientFactory;
        }

        public ExtensionClient getExtensionClient() {
            return extensionClient;
        }

        public SofaRuntimeContext getSofaRuntimeContext() {
            return sofaRuntimeContext;
        }

        public boolean isTrigger() {
            return trigger;
        }
    }
}

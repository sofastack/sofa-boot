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
package com.alipay.sofa.smoke.tests.ark;

import com.alipay.sofa.ark.spi.event.biz.BeforeBizStopEvent;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.service.ArkInject;
import com.alipay.sofa.boot.ark.invoke.DynamicJvmServiceProxyFinder;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.spring.RuntimeShutdownAware;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for ark handlers.
 *
 * @author huzijie
 * @version ArkHandlerTests.java, v 0.1 2023年04月06日 7:14 PM huzijie Exp $
 */
@DirtiesContext
@Import(ArkHandlerTests.MockRuntimeShutdownAware.class)
@TestExecutionListeners(value = DependencyInjectionTestExecutionListener.class, inheritListeners = false)
public class ArkHandlerTests extends ArkTestBase {

    @ArkInject
    private DynamicJvmServiceProxyFinder dynamicJvmServiceProxyFinder;

    @Autowired
    private SofaRuntimeManager           sofaRuntimeManager;

    @Autowired
    private MockRuntimeShutdownAware     mockRuntimeShutdownAware;

    @Autowired
    private GenericApplicationContext    applicationContext;

    private Biz                          biz;

    @Test
    public void checkDynamicJvmServiceProxyFinder() {
        List<Biz> bizList = bizManagerService.getBiz("smoke-tests-ark");
        assertThat(bizList.size()).isEqualTo(1);
        biz = bizList.get(0);

        assertThat(dynamicJvmServiceProxyFinder).isEqualTo(
            DynamicJvmServiceProxyFinder.getInstance());
        assertThat(biz).isEqualTo(DynamicJvmServiceProxyFinder.getBiz(sofaRuntimeManager));
        assertThat(dynamicJvmServiceProxyFinder.isHasFinishStartup()).isTrue();

        assertThat(mockRuntimeShutdownAware.isFinished()).isFalse();
    }

    @AfterEach
    public void checkShutdown() {
        adminService.sendEvent(new BeforeBizStopEvent(biz));
        assertThat(DynamicJvmServiceProxyFinder.getBiz(sofaRuntimeManager)).isNull();
        assertThat(mockRuntimeShutdownAware.isFinished()).isTrue();
        assertThat(applicationContext.isActive()).isFalse();
    }

    static class MockRuntimeShutdownAware implements RuntimeShutdownAware {

        private boolean finished;

        @Override
        public void shutdown() {
            finished = true;
        }

        public boolean isFinished() {
            return finished;
        }
    }
}

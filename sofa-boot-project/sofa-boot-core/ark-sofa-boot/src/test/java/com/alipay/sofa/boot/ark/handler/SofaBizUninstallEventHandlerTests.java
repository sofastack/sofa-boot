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
package com.alipay.sofa.boot.ark.handler;

import com.alipay.sofa.ark.spi.event.biz.BeforeBizStopEvent;
import com.alipay.sofa.boot.ark.MockBiz;
import com.alipay.sofa.boot.ark.SofaRuntimeContainer;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.GenericApplicationContext;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link SofaBizUninstallEventHandler}.
 *
 * @author huzijie
 * @version SofaBizUninstallEventHandlerTests.java, v 0.1 2023年04月06日 12:02 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class SofaBizUninstallEventHandlerTests {

    private final SofaBizUninstallEventHandler sofaBizUninstallEventHandler = new SofaBizUninstallEventHandler();

    @Mock
    private SofaRuntimeManager                 sofaRuntimeManager;

    @BeforeEach
    public void clear() {
        SofaRuntimeContainer.clear();
    }

    @Test
    public void noSofaRuntimeManager() {
        MockBiz mockBiz = new MockBiz();
        assertThatThrownBy(() -> sofaBizUninstallEventHandler.handleEvent(new BeforeBizStopEvent(mockBiz)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No SofaRuntimeManager match classLoader");
    }

    @Test
    public void shutDown() {
        SofaRuntimeContainer sofaRuntimeContainer = new SofaRuntimeContainer(sofaRuntimeManager);
        sofaRuntimeContainer.setApplicationContext(new GenericApplicationContext());

        MockBiz mockBiz = new MockBiz();
        sofaBizUninstallEventHandler.handleEvent(new BeforeBizStopEvent(mockBiz));

        verify(sofaRuntimeManager, times(1)).shutDownExternally();
    }
}

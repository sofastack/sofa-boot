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

import com.alipay.sofa.ark.spi.event.biz.AfterBizStartupEvent;
import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import com.alipay.sofa.boot.ark.MockBiz;
import com.alipay.sofa.boot.ark.SofaRuntimeContainer;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.support.GenericApplicationContext;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link SofaBizHealthCheckEventHandler}.
 *
 * @author huzijie
 * @version SofaBizHealthCheckEventHandlerTests.java, v 0.1 2023年04月06日 11:43 AM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class SofaBizHealthCheckEventHandlerTests {

    @Mock
    private GenericApplicationContext                    applicationContext;

    @Mock
    private ReadinessCheckListener                       readinessCheckListener;

    @Mock
    private SofaRuntimeManager                           sofaRuntimeManager;

    private final SofaBizHealthCheckEventHandler         sofaBizHealthCheckEventHandler = new SofaBizHealthCheckEventHandler();

    private final ObjectProvider<ReadinessCheckListener> objectProvider                 = new ObjectProvider<>() {

                                                                                            @Override
                                                                                            public ReadinessCheckListener getObject(Object... args)
                                                                                                                                                   throws BeansException {
                                                                                                return readinessCheckListener;
                                                                                            }

                                                                                            @Override
                                                                                            public ReadinessCheckListener getIfAvailable()
                                                                                                                                          throws BeansException {
                                                                                                return readinessCheckListener;
                                                                                            }

                                                                                            @Override
                                                                                            public ReadinessCheckListener getIfUnique()
                                                                                                                                       throws BeansException {
                                                                                                return readinessCheckListener;
                                                                                            }

                                                                                            @Override
                                                                                            public ReadinessCheckListener getObject()
                                                                                                                                     throws BeansException {
                                                                                                return readinessCheckListener;
                                                                                            }
                                                                                        };

    private final ObjectProvider<ReadinessCheckListener> nullProvider                   = new ObjectProvider<>() {

                                                                                            @Override
                                                                                            public ReadinessCheckListener getObject(Object... args)
                                                                                                                                                   throws BeansException {
                                                                                                return null;
                                                                                            }

                                                                                            @Override
                                                                                            public ReadinessCheckListener getIfAvailable()
                                                                                                                                          throws BeansException {
                                                                                                return null;
                                                                                            }

                                                                                            @Override
                                                                                            public ReadinessCheckListener getIfUnique()
                                                                                                                                       throws BeansException {
                                                                                                return null;
                                                                                            }

                                                                                            @Override
                                                                                            public ReadinessCheckListener getObject()
                                                                                                                                     throws BeansException {
                                                                                                return null;
                                                                                            }
                                                                                        };

    @BeforeEach
    public void clear() {
        SofaRuntimeContainer.clear();
    }

    @Test
    public void noApplicationContext() {
        MockBiz mockBiz = new MockBiz();
        assertThatThrownBy(() -> sofaBizHealthCheckEventHandler.handleEvent(new AfterBizStartupEvent(mockBiz)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No application match classLoader");
    }

    @Test
    public void noReadinessCheckListenerBean() {
        MockBiz mockBiz = new MockBiz();
        SofaRuntimeContainer sofaRuntimeContainer = new SofaRuntimeContainer(sofaRuntimeManager);
        sofaRuntimeContainer.setApplicationContext(applicationContext);
        when(applicationContext.getBeanProvider(ReadinessCheckListener.class)).thenReturn(
            nullProvider);

        sofaBizHealthCheckEventHandler.handleEvent(new AfterBizStartupEvent(mockBiz));

        verify(readinessCheckListener, never()).aggregateReadinessHealth();
    }

    @Test
    public void readinessUp() {
        MockBiz mockBiz = new MockBiz();
        SofaRuntimeContainer sofaRuntimeContainer = new SofaRuntimeContainer(sofaRuntimeManager);
        sofaRuntimeContainer.setApplicationContext(applicationContext);
        when(applicationContext.getBeanProvider(ReadinessCheckListener.class)).thenReturn(
            objectProvider);
        when(readinessCheckListener.aggregateReadinessHealth()).thenReturn(Health.up().build());

        sofaBizHealthCheckEventHandler.handleEvent(new AfterBizStartupEvent(mockBiz));

        verify(readinessCheckListener, times(1)).aggregateReadinessHealth();
    }

    @Test
    public void readinessDown() {
        MockBiz mockBiz = new MockBiz();
        SofaRuntimeContainer sofaRuntimeContainer = new SofaRuntimeContainer(sofaRuntimeManager);
        sofaRuntimeContainer.setApplicationContext(applicationContext);
        when(applicationContext.getBeanProvider(ReadinessCheckListener.class)).thenReturn(objectProvider);
        when(readinessCheckListener.aggregateReadinessHealth()).thenReturn(Health.down().build());

        assertThatThrownBy(() -> sofaBizHealthCheckEventHandler.handleEvent(new AfterBizStartupEvent(mockBiz)))
                .isInstanceOf(RuntimeException.class).hasMessage("Readiness health check failed.");

        verify(readinessCheckListener, times(1)).aggregateReadinessHealth();
    }
}

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
package com.alipay.sofa.boot.actuator.threadpool;

import com.alipay.sofa.common.thread.ThreadPoolConfig;
import com.alipay.sofa.common.thread.ThreadPoolGovernor;
import com.alipay.sofa.common.thread.ThreadPoolMonitorWrapper;
import com.alipay.sofa.common.thread.ThreadPoolStatistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author huzijie
 * @version ThreadPoolEndpointTests.java, v 0.1 2024年03月22日 12:01 huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class ThreadPoolEndpointTests {

    @Mock
    private ThreadPoolGovernor threadPoolGovernor;

    @InjectMocks
    private ThreadPoolEndpoint threadPoolEndpoint;

    @BeforeEach
    public void setUp() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 6, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(8));

        ThreadPoolConfig threadPoolConfig = Mockito.mock(ThreadPoolConfig.class);
        when(threadPoolConfig.getThreadPoolName()).thenReturn("mockThreadPoolName");
        when(threadPoolConfig.getSpaceName()).thenReturn("mockSpaceName");
        when(threadPoolConfig.getTaskTimeoutMilli()).thenReturn(10L);
        when(threadPoolConfig.getPeriod()).thenReturn(9L);

        ThreadPoolStatistics threadPoolStatistics = new ThreadPoolStatistics(threadPoolExecutor);
        ThreadPoolMonitorWrapper threadPoolMonitorWrapper = new ThreadPoolMonitorWrapper(
            threadPoolExecutor, threadPoolConfig, threadPoolStatistics);
        when(threadPoolGovernor.getAllThreadPoolWrappers()).thenReturn(
            List.of(threadPoolMonitorWrapper));
    }

    @Test
    public void threadPools() {
        List<ThreadPoolEndpoint.ThreadPoolInfo> descriptor = threadPoolEndpoint.threadPools()
            .threadPoolInfoList();
        assertThat(descriptor).hasSize(1);
        ThreadPoolEndpoint.ThreadPoolInfo threadPoolInfo = descriptor.get(0);
        assertThat(threadPoolInfo.threadPoolName()).isEqualTo("mockThreadPoolName");
        assertThat(threadPoolInfo.spaceName()).isEqualTo("mockSpaceName");
        assertThat(threadPoolInfo.threadPoolClassName()).isEqualTo(
            "java.util.concurrent.ThreadPoolExecutor");
        assertThat(threadPoolInfo.coreSize()).isEqualTo(5);
        assertThat(threadPoolInfo.maxSize()).isEqualTo(6);
        assertThat(threadPoolInfo.queueSize()).isEqualTo(0);
        assertThat(threadPoolInfo.queueRemainingCapacity()).isEqualTo(8);
        assertThat(threadPoolInfo.queueClassName()).isEqualTo(
            "java.util.concurrent.LinkedBlockingQueue");
        assertThat(threadPoolInfo.monitorPeriod()).isEqualTo(9);
        assertThat(threadPoolInfo.taskTimeout()).isEqualTo(10);
    }
}

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
package com.alipay.sofa.rpc.boot.test.common;

import com.alipay.sofa.rpc.boot.common.RpcThreadPoolMonitor;
import com.alipay.sofa.rpc.boot.log.LoggerConstant;
import org.junit.jupiter.api.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RpcThreadPoolMonitor}.
 *
 * @author zhaowang
 * @version : ThreadPoolMonitorTest.java, v 0.1 2020年09月08日 4:15 下午 zhaowang Exp $
 */
public class ThreadPoolMonitorTests {

    @Test
    public void checkStop() throws InterruptedException {
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(10, 11, 1, TimeUnit.DAYS,
            new LinkedBlockingQueue<>(10));
        RpcThreadPoolMonitor rpcThreadPoolMonitor = new RpcThreadPoolMonitor(executorService,
            LoggerConstant.TRIPLE_THREAD_LOGGER_NAME);
        rpcThreadPoolMonitor.start();

        Thread monitor = rpcThreadPoolMonitor.getMonitor();
        assertThat(Thread.State.TERMINATED == monitor.getState()).isFalse();
        rpcThreadPoolMonitor.stop();
        Thread.sleep(100);
        System.out.println(monitor.getState());
        assertThat(Thread.State.TERMINATED == monitor.getState()).isTrue();
    }
}

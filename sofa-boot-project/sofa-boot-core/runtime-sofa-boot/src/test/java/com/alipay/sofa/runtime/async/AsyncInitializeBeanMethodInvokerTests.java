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
package com.alipay.sofa.runtime.async;

import com.alipay.sofa.runtime.sample.SampleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.framework.ProxyFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link AsyncInitializeBeanMethodInvoker}.
 *
 * @author huzijie
 * @version AsyncInitializeBeanMethodInvokerTests.java, v 0.1 2023年04月10日 10:41 AM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class AsyncInitializeBeanMethodInvokerTests {

    @Mock
    private AsyncInitMethodManager           asyncInitMethodManager;

    private final String                     currentThreadName = Thread.currentThread().getName();

    private final String                     beanName          = "test";

    private SampleService                    asyncSampleService;

    private AsyncInitializeBeanMethodInvoker asyncInitializeBeanMethodInvoker;

    @BeforeEach
    void setUp() {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(SampleService.class);
        asyncInitializeBeanMethodInvoker = new AsyncInitializeBeanMethodInvoker(
            asyncInitMethodManager, new AsyncSampleServiceImpl(), beanName, "service");
        proxyFactory.addAdvice(asyncInitializeBeanMethodInvoker);
        asyncSampleService = (SampleService) proxyFactory.getProxy();
    }

    @Test
    void invokeNormal() {
        when(asyncInitMethodManager.isStartUpFinish()).thenReturn(true);
        assertThat(asyncSampleService.service()).isEqualTo(currentThreadName);
    }

    @Test
    void invokeCallOtherMethod() throws ExecutionException, InterruptedException {
        when(asyncInitMethodManager.isStartUpFinish()).thenReturn(false);
        assertThat(asyncSampleService.service()).isEqualTo(null);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> asyncSampleService.test());
        assertThat(future.isDone()).isFalse();
        asyncInitializeBeanMethodInvoker.asyncMethodFinish();
        assertThat(future.get()).isNotEqualTo(currentThreadName);
    }

    @Test
    void invokeTwice() throws ExecutionException, InterruptedException {
        when(asyncInitMethodManager.isStartUpFinish()).thenReturn(false);
        assertThat(asyncSampleService.service()).isEqualTo(null);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> asyncSampleService.service());
        assertThat(future.isDone()).isFalse();
        asyncInitializeBeanMethodInvoker.asyncMethodFinish();
        assertThat(future.get()).isNotEqualTo(currentThreadName);
        assertThat(asyncSampleService.service()).isEqualTo(currentThreadName);
    }
}

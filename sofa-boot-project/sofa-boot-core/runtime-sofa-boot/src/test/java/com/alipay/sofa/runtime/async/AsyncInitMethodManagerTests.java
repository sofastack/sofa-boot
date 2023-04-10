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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static com.alipay.sofa.runtime.async.AsyncInitMethodManager.ASYNC_INIT_METHOD_EXECUTOR_BEAN_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link AsyncInitMethodManager}.
 *
 * @author huzijie
 * @version AsyncInitMethodManagerTests.java, v 0.1 2023年04月10日 10:39 AM huzijie Exp $
 */
public class AsyncInitMethodManagerTests {

    @Test
    void submitTaskSuccess() {
        AsyncInitMethodManager manager = new AsyncInitMethodManager();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        GenericApplicationContext applicationContext = new GenericApplicationContext();
        applicationContext.getBeanFactory().registerSingleton(ASYNC_INIT_METHOD_EXECUTOR_BEAN_NAME, (Supplier<Object>) () -> executorService);
        applicationContext.refresh();
        manager.setApplicationContext(applicationContext);
        // Submit 3 tasks
        for (int i = 0; i < 3; i++) {
            manager.submitTask(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        manager.ensureAsyncTasksFinish();

        // Verify that all tasks finished successfully
        assertThat(manager.isStartUpFinish()).isTrue();

        // Shut down the executor service
        executorService.shutdown();
    }

    @Test
    void submitTaskException() {
        AsyncInitMethodManager manager = new AsyncInitMethodManager();
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        GenericApplicationContext applicationContext = new GenericApplicationContext();
        applicationContext.getBeanFactory().registerSingleton(ASYNC_INIT_METHOD_EXECUTOR_BEAN_NAME, (Supplier<Object>) () -> executorService);
        applicationContext.refresh();
        manager.setApplicationContext(applicationContext);
        // Submit 3 tasks
        for (int i = 0; i < 3; i++) {
            manager.submitTask(() -> {
                throw new RuntimeException("task fail");
            });
        }

        assertThatThrownBy(manager::ensureAsyncTasksFinish)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Async init task finish fail")
                .hasRootCauseMessage("task fail");

        // Verify that all tasks finished successfully
        assertThat(manager.isStartUpFinish()).isFalse();

        // Shut down the executor service
        executorService.shutdown();
    }

    @Test
    void registerAsyncInitBean() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        AsyncInitMethodManager manager = new AsyncInitMethodManager();
        String beanName = "myBean";
        String asyncInitMethodName = "initAsync";
        manager.registerAsyncInitBean(beanFactory, beanName, asyncInitMethodName);
        assertThat(manager.findAsyncInitMethod(beanFactory, beanName)).isEqualTo(
            asyncInitMethodName);
    }

}

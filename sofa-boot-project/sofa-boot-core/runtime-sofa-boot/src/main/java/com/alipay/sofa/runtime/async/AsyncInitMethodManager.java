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

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.boot.util.NamedThreadFactory;
import com.alipay.sofa.common.thread.SofaThreadPoolExecutor;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author huzijie
 * @version AsyncInitMethodManager.java, v 0.1 2023年01月17日 11:55 AM huzijie Exp $
 */
public class AsyncInitMethodManager implements PriorityOrdered,
        ApplicationListener<ContextRefreshedEvent>,
        ApplicationContextAware {

    private static final Logger LOGGER = SofaBootLoggerFactory.getLogger(AsyncInitMethodManager.class);

    public static final String ASYNC_INIT_METHOD_NAME = "async-init-method-name";

    private final AtomicReference<ThreadPoolExecutor> threadPoolExecutorRef = new AtomicReference<>();

    private final Map<String, String> asyncInitBeanNameMap = new HashMap<>();

    private final List<Future<?>> futures         = new ArrayList<>();

    private ApplicationContext applicationContext;

    private boolean startUpFinish         = false;

    private int executorCoreSize = Runtime.getRuntime().availableProcessors() + 1;

    private int executorMaxSize = Runtime.getRuntime().availableProcessors() + 1;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (applicationContext.equals(event.getApplicationContext())) {
            ensureAsyncTasksFinish();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void submitTask(Runnable runnable) {
        if (threadPoolExecutorRef.get() == null) {
            ThreadPoolExecutor threadPoolExecutor = createAsyncExecutor();
            boolean success = threadPoolExecutorRef.compareAndSet(null, threadPoolExecutor);
            if (!success) {
                threadPoolExecutor.shutdown();
            }
        }
        Future<?> future = threadPoolExecutorRef.get().submit(runnable);
        futures.add(future);
    }

    private void ensureAsyncTasksFinish() {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        startUpFinish = true;
        futures.clear();
        if (threadPoolExecutorRef.get() != null) {
            threadPoolExecutorRef.get().shutdown();
            threadPoolExecutorRef.set(null);
        }
    }

    public boolean isStartUpFinish() {
        return startUpFinish;
    }

    public void registerAsyncInitBean(String beanName, String asyncInitMethodName) {
        asyncInitBeanNameMap.put(beanName, asyncInitMethodName);
    }

    public String findAsyncInitMethod(String beanName) {
        return asyncInitBeanNameMap.get(beanName);
    }

    private ThreadPoolExecutor createAsyncExecutor() {
        LOGGER.info("create async-init-bean thread pool, corePoolSize: {}, maxPoolSize: {}.",
                executorCoreSize, executorMaxSize);
        return new SofaThreadPoolExecutor(executorCoreSize, executorMaxSize, 30,
                TimeUnit.SECONDS, new SynchronousQueue<>(), new NamedThreadFactory(
                "async-init-bean"), new ThreadPoolExecutor.CallerRunsPolicy(), "async-init-bean",
                SofaBootConstants.SOFA_BOOT_SPACE_NAME);
    }

    public int getExecutorCoreSize() {
        return executorCoreSize;
    }

    public void setExecutorCoreSize(int executorCoreSize) {
        this.executorCoreSize = executorCoreSize;
    }

    public int getExecutorMaxSize() {
        return executorMaxSize;
    }

    public void setExecutorMaxSize(int executorMaxSize) {
        this.executorMaxSize = executorMaxSize;
    }
}

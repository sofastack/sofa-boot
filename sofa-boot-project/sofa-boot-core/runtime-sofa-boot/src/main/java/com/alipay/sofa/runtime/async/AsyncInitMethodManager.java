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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Manager to store and invoke async init method beans.
 *
 * @author huzijie
 * @version AsyncInitMethodManager.java, v 0.1 2023年01月17日 11:55 AM huzijie Exp $
 */
public class AsyncInitMethodManager implements PriorityOrdered,
                                   ApplicationListener<ContextRefreshedEvent>,
                                   ApplicationContextAware {

    public static final String                          ASYNC_INIT_METHOD_EXECUTOR_BEAN_NAME = "async-init-method-executor";

    public static final String                          ASYNC_INIT_METHOD_NAME               = "async-init-method-name";

    private final AtomicReference<ThreadPoolExecutor>   threadPoolExecutorRef                = new AtomicReference<>();

    private final Map<BeanFactory, Map<String, String>> asyncInitBeanNameMap                 = new ConcurrentHashMap<>();

    private final List<Future<?>>                       futures                              = new ArrayList<>();

    private ApplicationContext                          applicationContext;

    private boolean                                     startUpFinish                        = false;

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

    private ThreadPoolExecutor createAsyncExecutor() {
        return (ThreadPoolExecutor) applicationContext.getBean(
            ASYNC_INIT_METHOD_EXECUTOR_BEAN_NAME, Supplier.class).get();
    }

    void ensureAsyncTasksFinish() {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Throwable e) {
                throw new RuntimeException("Async init task finish fail", e);
            }
        }

        startUpFinish = true;
        futures.clear();
        asyncInitBeanNameMap.clear();
        if (threadPoolExecutorRef.get() != null) {
            threadPoolExecutorRef.get().shutdown();
            threadPoolExecutorRef.set(null);
        }
    }

    public boolean isStartUpFinish() {
        return startUpFinish;
    }

    public void registerAsyncInitBean(ConfigurableListableBeanFactory beanFactory, String beanName, String asyncInitMethodName) {
        Map<String, String> map = asyncInitBeanNameMap.computeIfAbsent(beanFactory, k -> new ConcurrentHashMap<>());
        map.put(beanName, asyncInitMethodName);
    }

    public String findAsyncInitMethod(ConfigurableListableBeanFactory beanFactory, String beanName) {
        Map<String, String> map = asyncInitBeanNameMap.get(beanFactory);
        if (map == null) {
            return null;
        } else {
            return map.get(beanName);
        }
    }
}

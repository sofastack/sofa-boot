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
package com.alipay.sofa.runtime.spring.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.core.env.Environment;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.util.NamedThreadFactory;
import com.alipay.sofa.runtime.log.SofaLogger;

/**
 * @author qilong.zql
 * @author xuanbei
 * @since 2.6.0
 */
public class AsyncTaskExecutor {
    protected static final int                                 CPU_COUNT       = Runtime
                                                                                   .getRuntime()
                                                                                   .availableProcessors();
    protected static final AtomicReference<ThreadPoolExecutor> THREAD_POOL_REF = new AtomicReference<ThreadPoolExecutor>();

    protected static final List<Future>                        FUTURES         = new ArrayList<>();
    protected static final AtomicBoolean                       STARTED         = new AtomicBoolean(
                                                                                   false);

    public static Future submitTask(Environment environment, Runnable runnable) {
        if (THREAD_POOL_REF.get() == null) {
            ThreadPoolExecutor threadPoolExecutor = createThreadPoolExecutor(environment);
            boolean success = THREAD_POOL_REF.compareAndSet(null, threadPoolExecutor);
            if (!success) {
                threadPoolExecutor.shutdown();
            }
        }
        Future future = THREAD_POOL_REF.get().submit(runnable);
        FUTURES.add(future);
        return future;
    }

    /**
     * Create thread pool to execute async init task
     * @return
     */
    private static ThreadPoolExecutor createThreadPoolExecutor(Environment environment) {
        int threadPoolCoreSize = CPU_COUNT + 1;
        String coreSizeStr = environment.getProperty(SofaBootConstants.ASYNC_INIT_BEAN_CORE_SIZE);
        if (coreSizeStr != null) {
            threadPoolCoreSize = Integer.parseInt(coreSizeStr);
        }

        int threadPoolMaxSize = CPU_COUNT + 1;
        String maxSizeStr = environment.getProperty(SofaBootConstants.ASYNC_INIT_BEAN_MAX_SIZE);
        if (maxSizeStr != null) {
            threadPoolMaxSize = Integer.parseInt(maxSizeStr);
        }

        SofaLogger.info(String.format(
            "create async-init-bean thread pool, corePoolSize: %d, maxPoolSize: %d.",
            threadPoolCoreSize, threadPoolMaxSize));
        return new ThreadPoolExecutor(threadPoolCoreSize, threadPoolMaxSize, 30, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), new NamedThreadFactory("async-init-bean"),
            new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static void ensureAsyncTasksFinish() {
        for (Future future : FUTURES) {
            try {
                future.get();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        STARTED.set(true);
        FUTURES.clear();
        if (THREAD_POOL_REF.get() != null) {
            THREAD_POOL_REF.get().shutdown();
            THREAD_POOL_REF.set(null);
        }
    }

    public static boolean isStarted() {
        return STARTED.get();
    }
}
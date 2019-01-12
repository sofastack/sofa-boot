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

import com.alipay.sofa.runtime.spi.log.SofaLogger;
import com.alipay.sofa.runtime.util.NamedThreadFactory;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.alipay.sofa.infra.constants.CommonMiddlewareConstants.ASYNC_INIT_BEAN_CORE_SIZE;

/**
 * @author qilong.zql
 * @author xuanbei
 * @since 2.6.0
 */
public class AsyncTaskExecutor {
    private static final int                                 CPU_COUNT       = Runtime
                                                                                 .getRuntime()
                                                                                 .availableProcessors();
    private static final AtomicReference<ThreadPoolExecutor> THREAD_POOL_REF = new AtomicReference<ThreadPoolExecutor>();

    private static final List<Future>                        FUTURES         = new ArrayList<>();
    private static final AtomicBoolean                       STARTED         = new AtomicBoolean(
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
     * 根据sofa-config.properties创建线程池
     * 对于合并部署情况，会使用第一个配置异步化应用的sofa-config.properties配置
     * @return
     */
    private static ThreadPoolExecutor createThreadPoolExecutor(Environment environment) {
        int threadPoolCoreSize = CPU_COUNT + 1;
        String coreSizeStr = environment.getProperty(ASYNC_INIT_BEAN_CORE_SIZE);
        if (coreSizeStr != null) {
            threadPoolCoreSize = Integer.parseInt(coreSizeStr);
        }

        int threadPoolMaxSize = CPU_COUNT + 1;
        String maxSizeStr = environment.getProperty("ASYNC_INIT_BEAN_MAX_SIZE");
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

    public static void ensureAsyncTasksFinish(String appName) {
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
        }
    }

    public static boolean isStartPhase() {
        return STARTED.get();
    }
}
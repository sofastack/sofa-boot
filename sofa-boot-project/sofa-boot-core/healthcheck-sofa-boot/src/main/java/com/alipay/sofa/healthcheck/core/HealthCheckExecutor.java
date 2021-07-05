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
package com.alipay.sofa.healthcheck.core;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.util.NamedThreadFactory;
import com.alipay.sofa.common.thread.SofaThreadPoolExecutor;
import com.alipay.sofa.healthcheck.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.log.HealthCheckLoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.actuate.health.Health;
import org.springframework.core.env.Environment;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Health Check Thread Pool
 *
 * @author linnan
 * @since 3.7.1
 */
public class HealthCheckExecutor {

    private static Logger                                    logger          = HealthCheckLoggerFactory
                                                                                 .getLogger(HealthCheckerProcessor.class);
    private static final int                                 CPU_COUNT       = Runtime
                                                                                 .getRuntime()
                                                                                 .availableProcessors();
    private static final AtomicReference<ThreadPoolExecutor> THREAD_POOL_REF = new AtomicReference<ThreadPoolExecutor>();

    public static Future<Health> submitTask(Environment environment, Callable<Health> callable) {
        if (THREAD_POOL_REF.get() == null) {
            ThreadPoolExecutor threadPoolExecutor = createThreadPoolExecutor(environment);
            boolean success = THREAD_POOL_REF.compareAndSet(null, threadPoolExecutor);
            if (!success) {
                threadPoolExecutor.shutdown();
            }
        }
        return THREAD_POOL_REF.get().submit(callable);
    }

    /**
     * Create thread pool to execute health check.
     * @return
     */
    private static ThreadPoolExecutor createThreadPoolExecutor(Environment environment) {
        int threadPoolCoreSize = CPU_COUNT + 1;
        String coreSizeStr = environment
            .getProperty(SofaBootConstants.SOFABOOT_HEALTH_CHECK_THREAD_POOL_CORE_SIZE);
        if (coreSizeStr != null) {
            threadPoolCoreSize = Integer.parseInt(coreSizeStr);
        }

        int threadPoolMaxSize = CPU_COUNT + 1;
        String maxSizeStr = environment
            .getProperty(SofaBootConstants.SOFABOOT_HEALTH_CHECK_THREAD_POOL_MAX_SIZE);
        if (maxSizeStr != null) {
            threadPoolMaxSize = Integer.parseInt(maxSizeStr);
        }

        logger.info("create health-check thread pool, corePoolSize: {}, maxPoolSize: {}.",
            threadPoolCoreSize, threadPoolMaxSize);
        return new SofaThreadPoolExecutor(threadPoolCoreSize, threadPoolMaxSize, 30,
            TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new NamedThreadFactory(
                "health-check"), new ThreadPoolExecutor.CallerRunsPolicy(), "health-check",
            "sofa-boot");
    }
}

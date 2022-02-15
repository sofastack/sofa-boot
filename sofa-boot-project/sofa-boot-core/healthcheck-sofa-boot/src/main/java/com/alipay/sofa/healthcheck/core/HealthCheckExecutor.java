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
import com.alipay.sofa.healthcheck.log.HealthCheckLoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.actuate.health.Health;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Health Check Thread Pool
 *
 * @author linnan
 * @since 3.7.1
 */
public class HealthCheckExecutor {
    private static Logger                   logger          = HealthCheckLoggerFactory.DEFAULT_LOG;

    private static final ThreadPoolExecutor THREAD_POOL_REF = createThreadPoolExecutor();

    public static Future<Health> submitTask(Callable<Health> callable) {
        return THREAD_POOL_REF.submit(callable);
    }

    /**
     * Create thread pool to execute health check.
     * @return thread pool to execute health check.
     */
    private static ThreadPoolExecutor createThreadPoolExecutor() {
        logger.info("Create health-check thread pool, corePoolSize: 1, maxPoolSize: 1.");
        return new SofaThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), new NamedThreadFactory("health-check"),
            new ThreadPoolExecutor.CallerRunsPolicy(), "health-check",
            SofaBootConstants.SOFABOOT_SPACE_NAME);
    }
}

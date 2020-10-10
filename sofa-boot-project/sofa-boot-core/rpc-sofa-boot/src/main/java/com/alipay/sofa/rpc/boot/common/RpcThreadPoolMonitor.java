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
package com.alipay.sofa.rpc.boot.common;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.alipay.sofa.rpc.common.annotation.VisibleForTesting;
import org.slf4j.Logger;

import com.alipay.sofa.rpc.boot.log.SofaBootRpcLoggerFactory;

/**
 *
 * 线程池监测器
 *
 * @author <a href="mailto:caojie.cj@antfin.com">CaoJie</a>
 */
public class RpcThreadPoolMonitor {

    private static final long  DEFAULT_SLEEP_TIME = 30000;

    private final Logger       logger;

    private long               sleepTimeMS;

    /**
     * 线程池
     */
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 开启标志
     */
    private AtomicInteger      startTimes         = new AtomicInteger(0);

    private volatile boolean   active             = true;

    private Thread             monitor;

    public RpcThreadPoolMonitor(String loggerName) {
        this(null, loggerName, DEFAULT_SLEEP_TIME);
    }

    public RpcThreadPoolMonitor(final ThreadPoolExecutor threadPoolExecutor, String loggerName) {
        this(threadPoolExecutor, loggerName, DEFAULT_SLEEP_TIME);
    }

    public RpcThreadPoolMonitor(final ThreadPoolExecutor threadPoolExecutor, String loggerName,
                                long sleepTimeMS) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.logger = SofaBootRpcLoggerFactory.getLogger(loggerName);
        this.sleepTimeMS = sleepTimeMS;
    }

    /**
     * 开启线程池监测
     */
    public void start() {
        synchronized (this) {
            if (threadPoolExecutor != null) {
                if (startTimes.intValue() == 0) {
                    if (startTimes.incrementAndGet() == 1) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("coreSize:" + threadPoolExecutor.getCorePoolSize() + ",");
                        sb.append("maxPoolSize:" + threadPoolExecutor.getMaximumPoolSize() + ",");
                        sb.append("keepAliveTime:"
                                  + threadPoolExecutor.getKeepAliveTime(TimeUnit.MILLISECONDS)
                                  + "\n");
                        if (logger.isInfoEnabled()) {
                            logger.info(sb.toString());
                        }
                        monitor = new Thread() {
                            public void run() {
                                while (active) {
                                    try {
                                        if (logger.isInfoEnabled()) {
                                            StringBuilder sb = new StringBuilder();
                                            int blockQueueSize = threadPoolExecutor.getQueue()
                                                .size();
                                            int activeSize = threadPoolExecutor.getActiveCount();
                                            int poolSize = threadPoolExecutor.getPoolSize();
                                            sb.append("blockQueue:" + blockQueueSize + ", ");
                                            sb.append("active:" + activeSize + ", ");
                                            sb.append("idle:" + (poolSize - activeSize) + ", ");
                                            sb.append("poolSize:" + poolSize);
                                            logger.info(sb.toString());
                                        }
                                    } catch (Throwable throwable) {
                                        logger.error("Thread pool monitor error", throwable);
                                    }

                                    try {
                                        sleep(sleepTimeMS);
                                    } catch (InterruptedException e) {
                                        logger
                                            .error("Error happened when the thread pool monitor is sleeping");
                                    }
                                }
                            }
                        };
                        monitor.setDaemon(true);
                        monitor.setName("RPC-RES-MONITOR");
                        monitor.start();
                    } else {
                        throw new RuntimeException("rpc started event has been consumed");
                    }
                } else {
                    throw new RuntimeException("rpc started event has been consumed");
                }
            } else {
                throw new RuntimeException("the rpc thread pool is null");
            }
        }
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public void stop() {
        synchronized (this) {
            this.active = false;
            if (this.monitor != null) {
                this.monitor.interrupt();
                this.monitor = null;
            }
            this.threadPoolExecutor = null;
            this.startTimes.set(0);
        }
    }

    @VisibleForTesting
    public Thread getMonitor() {
        return monitor;
    }
}
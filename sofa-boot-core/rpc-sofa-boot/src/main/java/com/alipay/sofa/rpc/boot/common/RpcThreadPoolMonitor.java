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

import com.alipay.sofa.rpc.boot.log.SofaBootRpcLoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * 线程池监测器
 *
 * @author <a href="mailto:caojie.cj@antfin.com">CaoJie</a>
 */
public class RpcThreadPoolMonitor {

    private static final Logger LOGGER     = SofaBootRpcLoggerFactory.getLogger("RPC-BOLT-THREADPOOL");

    /**
     * 线程池
     */
    private ThreadPoolExecutor  threadPoolExecutor;

    /**
     * 开启标志
     */
    private AtomicInteger       startTimes = new AtomicInteger(0);

    public RpcThreadPoolMonitor(final ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    /**
     * 开启线程池监测
     */
    public void start() {
        if (threadPoolExecutor != null) {
            if (startTimes.intValue() == 0) {
                if (startTimes.incrementAndGet() == 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("coreSize:" + threadPoolExecutor.getCorePoolSize() + ",");
                    sb.append("maxPoolSize:" + threadPoolExecutor.getMaximumPoolSize() + ",");
                    sb.append("keepAliveTime:" + threadPoolExecutor.getKeepAliveTime(TimeUnit.MILLISECONDS) + "\n");
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info(sb.toString());
                    }
                    Thread monitor = new Thread() {
                        public void run() {
                            while (true) {

                                try {
                                    if (LOGGER.isInfoEnabled()) {
                                        StringBuilder sb = new StringBuilder();
                                        int blockQueueSize = threadPoolExecutor.getQueue().size();
                                        int activeSize = threadPoolExecutor.getActiveCount();
                                        int poolSize = threadPoolExecutor.getPoolSize();
                                        sb.append("blockQueue:" + blockQueueSize + ", ");
                                        sb.append("active:" + activeSize + ", ");
                                        sb.append("idle:" + (poolSize - activeSize) + ", ");
                                        sb.append("poolSize:" + poolSize);
                                        LOGGER.info(sb.toString());
                                    }
                                } catch (Throwable throwable) {
                                    LOGGER.error("Thread pool monitor error", throwable);
                                }

                                try {
                                    sleep(30000);
                                } catch (InterruptedException e) {
                                    LOGGER.error("Error happen the thread pool watch sleep ");
                                }
                            }
                        }
                    };
                    monitor.setDaemon(true);
                    monitor.setName("RPC-RES-MONITOR");
                    monitor.start();
                } else {
                    throw new RuntimeException("rpc started event has been consumeed");
                }
            } else {
                throw new RuntimeException("rpc started event has been consumeed");
            }
        } else {
            throw new RuntimeException("the rpc threadpool is null");
        }
    }

}
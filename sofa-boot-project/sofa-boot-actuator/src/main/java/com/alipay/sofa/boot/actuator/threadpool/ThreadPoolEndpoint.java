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
package com.alipay.sofa.boot.actuator.threadpool;

import com.alipay.sofa.common.thread.ThreadPoolConfig;
import com.alipay.sofa.common.thread.ThreadPoolGovernor;
import com.alipay.sofa.common.thread.ThreadPoolMonitorWrapper;
import org.springframework.boot.actuate.endpoint.OperationResponseBody;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * {@link Endpoint @Endpoint} to expose details of sofa thread pools registered in {@link ThreadPoolGovernor}.
 *
 * @author huzijie
 * @version ThreadPoolEndpoint.java, v 0.1 2024年03月22日 11:41 huzijie Exp $
 */
@Endpoint(id = "threadpool")
public class ThreadPoolEndpoint {

    private final ThreadPoolGovernor threadPoolGovernor;

    public ThreadPoolEndpoint(ThreadPoolGovernor threadPoolGovernor) {
        this.threadPoolGovernor = threadPoolGovernor;
    }

    @ReadOperation
    public ThreadPoolsDescriptor threadPools () {
        Collection<ThreadPoolMonitorWrapper> threadPoolWrappers = threadPoolGovernor.getAllThreadPoolWrappers();

        List<ThreadPoolInfo> threadPoolInfoList = threadPoolWrappers.stream().map(this::convertToThreadPoolInfo).toList();
        return new ThreadPoolsDescriptor(threadPoolInfoList);
    }

    private ThreadPoolInfo convertToThreadPoolInfo(ThreadPoolMonitorWrapper wrapper) {
        ThreadPoolConfig threadPoolConfig = wrapper.getThreadPoolConfig();
        String threadPoolName = threadPoolConfig.getThreadPoolName();
        String spaceName = threadPoolConfig.getSpaceName();
        long period = threadPoolConfig.getPeriod();
        long taskTimeout = threadPoolConfig.getTaskTimeoutMilli();

        ThreadPoolExecutor threadPoolExecutor = wrapper.getThreadPoolExecutor();
        String threadPoolClassName = threadPoolExecutor.getClass().getName();
        int coreSize = threadPoolExecutor.getCorePoolSize();
        int maxSize = threadPoolExecutor.getMaximumPoolSize();
        int queueSize = threadPoolExecutor.getQueue().size();
        int queueRemainingCapacity = threadPoolExecutor.getQueue().remainingCapacity();
        String queueClassName = threadPoolExecutor.getQueue().getClass().getName();

        return new ThreadPoolInfo(threadPoolName, spaceName, threadPoolClassName, coreSize, maxSize, queueClassName, queueSize, queueRemainingCapacity, period, taskTimeout);
    }

    public record ThreadPoolsDescriptor(List<ThreadPoolInfo> threadPoolInfoList) implements OperationResponseBody {}

    public record ThreadPoolInfo(String threadPoolName,
                                 String spaceName,

                                 String threadPoolClassName,
                                 int coreSize,
                                 int maxSize,
                                 String queueClassName,
                                 int queueSize,
                                 int queueRemainingCapacity,
                                 long monitorPeriod,
                                 long taskTimeout) {}
}

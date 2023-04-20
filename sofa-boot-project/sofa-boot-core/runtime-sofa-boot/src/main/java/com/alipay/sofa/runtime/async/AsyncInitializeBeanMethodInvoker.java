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

import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

/**
 * Implementation of {@link MethodInterceptor} to async invoke init method.
 *
 * @author huzijie
 * @version AsyncInitializeBeanMethodInvoker.java, v 0.1 2023年01月17日 11:53 AM huzijie Exp $
 */
public class AsyncInitializeBeanMethodInvoker implements MethodInterceptor {

    private static final Logger          LOGGER             = SofaBootLoggerFactory
                                                                .getLogger(AsyncInitializeBeanMethodInvoker.class);

    private final AsyncInitMethodManager asyncInitMethodManager;

    private final Object                 targetObject;

    private final String                 asyncMethodName;

    private final String                 beanName;

    private final CountDownLatch         initCountDownLatch = new CountDownLatch(1);

    /**
     * mark async-init method is during first invocation.
     */
    private volatile boolean             isAsyncCalling     = false;

    /**
     * mark init-method is called.
     */
    private volatile boolean             isAsyncCalled      = false;

    public AsyncInitializeBeanMethodInvoker(AsyncInitMethodManager asyncInitMethodManager,
                                            Object targetObject, String beanName, String methodName) {
        this.asyncInitMethodManager = asyncInitMethodManager;
        this.targetObject = targetObject;
        this.beanName = beanName;
        this.asyncMethodName = methodName;
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        // if the spring refreshing is finished
        if (asyncInitMethodManager.isStartUpFinish()) {
            return invocation.getMethod().invoke(targetObject, invocation.getArguments());
        }

        Method method = invocation.getMethod();
        final String methodName = method.getName();
        if (!isAsyncCalled && methodName.equals(asyncMethodName)) {
            isAsyncCalled = true;
            isAsyncCalling = true;
            asyncInitMethodManager.submitTask(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    invocation.getMethod().invoke(targetObject, invocation.getArguments());
                    LOGGER.info("{}({}) {} method execute {}dms.", targetObject
                                    .getClass().getName(), beanName, methodName, (System
                                    .currentTimeMillis() - startTime));
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                } finally {
                    asyncMethodFinish();
                }
            });
            return null;
        }

        if (isAsyncCalling) {
            long startTime = System.currentTimeMillis();
            initCountDownLatch.await();
            LOGGER.info("{}({}) {} method wait {}ms.",
                    targetObject.getClass().getName(), beanName, methodName,
                    (System.currentTimeMillis() - startTime));
        }
        return invocation.getMethod().invoke(targetObject, invocation.getArguments());
    }

    void asyncMethodFinish() {
        this.initCountDownLatch.countDown();
        this.isAsyncCalling = false;
    }
}

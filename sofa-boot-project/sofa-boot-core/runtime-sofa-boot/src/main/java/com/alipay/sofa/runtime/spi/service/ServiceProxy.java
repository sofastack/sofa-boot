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
package com.alipay.sofa.runtime.spi.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.StringUtils;

/**
 * Implementation of {@link MethodInterceptor} to invoke method use service.
 *
 * @author xuanbei 18/2/28
 */
public abstract class ServiceProxy implements MethodInterceptor {

    protected ClassLoader serviceClassLoader;

    public ServiceProxy(ClassLoader serviceClassLoader) {
        this.serviceClassLoader = serviceClassLoader;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        long startTime = System.currentTimeMillis();
        try {
            Thread.currentThread().setContextClassLoader(serviceClassLoader);
            return doInvoke(invocation);
        } catch (Throwable e) {
            doCatch(invocation, e, startTime);
            throw e;
        } finally {
            doFinally(invocation, startTime);
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    protected void pushThreadContextClassLoader(ClassLoader newContextClassLoader) {
        if (newContextClassLoader != null) {
            Thread.currentThread().setContextClassLoader(newContextClassLoader);
        }
    }

    protected void popThreadContextClassLoader(ClassLoader tcl) {
        Thread.currentThread().setContextClassLoader(tcl);
    }

    protected String getCommonInvocationLog(String start, MethodInvocation invocation,
                                            long startTime) {
        String appStart = "";

        if (StringUtils.hasText(appStart)) {
            appStart = "-" + start;
        }

        long endTime = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder("SOFA-Reference" + appStart + "(");

        sb.append(invocation.getMethod().getName()).append(",");
        for (Object o : invocation.getArguments()) {
            sb.append(o);
            sb.append(",");
        }
        sb.append((endTime - startTime)).append("ms").append(")");

        return sb.toString();
    }

    public ClassLoader getServiceClassLoader() {
        return serviceClassLoader;
    }

    /**
     * Actually do invoke.
     *
     * @param invocation invocation
     * @return invoke result
     * @throws Throwable invoke exception
     */
    protected abstract Object doInvoke(MethodInvocation invocation) throws Throwable;

    /**
     * Hook to handle exception.
     *
     * @param invocation invocation
     * @param e exception
     * @param startTime start time
     */
    protected abstract void doCatch(MethodInvocation invocation, Throwable e, long startTime);

    /**
     * Hook to handle after invocatino.
     *
     * @param invocation invocation
     * @param startTime start time
     */
    protected abstract void doFinally(MethodInvocation invocation, long startTime);
}

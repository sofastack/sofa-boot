/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
package com.alipay.sofa.runtime.spi.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author xuanbei 18/2/28
 */
public abstract class ServiceProxy implements MethodInterceptor {
    private ClassLoader serviceClassLoader;

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
            do_catch(invocation, e, startTime);
            throw e;
        } finally {
            do_finally(invocation, startTime);
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

        if (appStart != null && appStart.length() > 0) {
            appStart = "-" + start;
        }

        long endTime = System.currentTimeMillis();

        StringBuffer sb = new StringBuffer("SOFA-Reference" + appStart + "(");

        sb.append(invocation.getMethod().getName());
        sb.append(",");
        for (Object o : invocation.getArguments()) {
            sb.append(o);
            sb.append(",");
        }
        sb.append((endTime - startTime) + "ms");
        sb.append(")");

        return sb.toString();
    }

    public ClassLoader getServiceClassLoader() {
        return serviceClassLoader;
    }

    protected abstract Object doInvoke(MethodInvocation invocation) throws Throwable;

    protected abstract void do_catch(MethodInvocation invocation, Throwable e, long startTime);

    protected abstract void do_finally(MethodInvocation invocation, long startTime);
}
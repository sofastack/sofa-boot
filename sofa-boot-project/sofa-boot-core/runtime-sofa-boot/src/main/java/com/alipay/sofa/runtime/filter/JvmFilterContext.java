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
package com.alipay.sofa.runtime.filter;

import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/8/18
 */
public class JvmFilterContext {
    // Jvm invocation result
    // null when in <code>before</code> invoking except some filter sets it explicitly
    private Object                       invokeResult;

    // Jvm invocation AOP, could be used to fetch method name and args
    private transient MethodInvocation   methodInvocation;

    // In normal SOFABoot application, this is always current application's SOFABoot runtime context
    // When in Ark environment, this could be Ark Biz's SOFABoot runtime context
    private transient SofaRuntimeContext sofaRuntimeContext;

    // Thrown exception when do Jvm service invoking
    // null when in <code>before</code> invoking except some filter sets it explicitly
    private Throwable                    exception;

    public JvmFilterContext() {
    }

    public JvmFilterContext(MethodInvocation methodInvocation) {
        this.methodInvocation = methodInvocation;
    }

    public Object getInvokeResult() {
        return invokeResult;
    }

    public void setInvokeResult(Object invokeResult) {
        this.invokeResult = invokeResult;
    }

    public SofaRuntimeContext getSofaRuntimeContext() {
        return sofaRuntimeContext;
    }

    public void setSofaRuntimeContext(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    public MethodInvocation getMethodInvocation() {
        return methodInvocation;
    }

    public void setMethodInvocation(MethodInvocation methodInvocation) {
        this.methodInvocation = methodInvocation;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable e) {
        this.exception = e;
    }
}

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
package com.alipay.sofa.runtime.service.binding;

import com.alipay.sofa.boot.log.ErrorCode;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.runtime.filter.JvmFilterContext;
import com.alipay.sofa.runtime.filter.JvmFilterHolder;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.ServiceProxy;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * Extensions for {@link ServiceProxy} to invoke jvm service.
 *
 * @author huzijie
 * @since 3.1.2
 */
public class JvmServiceInvoker extends ServiceProxy {

    private static final Logger      LOGGER = SofaBootLoggerFactory
                                                .getLogger(JvmServiceInvoker.class);

    private final Contract           contract;

    private final JvmBinding         binding;

    private final SofaRuntimeContext sofaRuntimeContext;

    private Object                   target;

    public JvmServiceInvoker(Contract contract, JvmBinding binding,
                             SofaRuntimeContext sofaRuntimeContext) {
        super(sofaRuntimeContext.getAppClassLoader());
        this.binding = binding;
        this.sofaRuntimeContext = sofaRuntimeContext;
        this.contract = contract;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (!sofaRuntimeContext.getProperties().isJvmFilterEnable()) {
            // Jvm filtering is not enabled
            return super.invoke(invocation);
        }

        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        JvmFilterContext context = new JvmFilterContext(invocation);
        Object rtn;

        if (getTarget() == null) {
            ServiceComponent serviceComponent = sofaRuntimeContext.getServiceProxyManager()
                .getDynamicServiceComponent(contract, sofaRuntimeContext.getAppClassLoader());
            if (serviceComponent == null) {
                // Jvm service is not found in normal or Ark environment
                // We're actually invoking an RPC service, skip Jvm filtering
                return super.invoke(invocation);
            }
            context.setSofaRuntimeContext(serviceComponent.getContext());
        } else {
            context.setSofaRuntimeContext(sofaRuntimeContext);
        }

        long startTime = System.currentTimeMillis();
        try {
            Thread.currentThread().setContextClassLoader(serviceClassLoader);
            // Do Jvm filter <code>before</code> invoking
            // if some filter returns false, skip remaining filters and actual Jvm invoking
            if (JvmFilterHolder.beforeInvoking(context)) {
                rtn = doInvoke(invocation);
                context.setInvokeResult(rtn);
            }
        } catch (Throwable e) {
            // Exception occurs, set <code>e</code> in Jvm context
            context.setException(e);
            doCatch(invocation, e, startTime);
            throw e;
        } finally {
            // Do Jvm Filter <code>after</code> invoking regardless of the fact whether exception happens or not
            JvmFilterHolder.afterInvoking(context);
            rtn = context.getInvokeResult();
            doFinally(invocation, startTime);
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
        return rtn;
    }

    @Override
    public Object doInvoke(MethodInvocation invocation) throws Throwable {
        if (binding.isDestroyed()) {
            throw new IllegalStateException("Can not call destroyed reference! JVM Reference["
                                            + getInterfaceName() + "#" + getUniqueId()
                                            + "] has already been destroyed.");
        }

        LOGGER.atDebug().log(">> Start in JVM service invoke, the service interface is  - {}.",
            getInterfaceName());

        Object retVal;
        Object targetObj = this.getTarget();

        // invoke internal dynamic-biz jvm service
        if (targetObj == null) {
            ServiceProxy serviceProxy = sofaRuntimeContext.getServiceProxyManager()
                .getDynamicServiceProxy(contract, sofaRuntimeContext.getAppClassLoader());
            if (serviceProxy != null) {
                try {
                    return serviceProxy.invoke(invocation);
                } finally {
                    LOGGER.atDebug().log(
                        "<< Finish Cross App JVM service invoke, the service is  - {}.",
                        getInterfaceName() + "#" + getUniqueId());
                }
            }
        }

        if (targetObj == null || ((targetObj instanceof Proxy) && binding.hasBackupProxy())) {
            targetObj = binding.getBackupProxy();
            LOGGER.atDebug().log("<<{}.{} backup proxy invoke.", getInterfaceName().getName(),
                invocation.getMethod().getName());
        }

        if (targetObj == null) {
            throw new IllegalStateException(ErrorCode.convert("01-00400", getInterfaceName(),
                getUniqueId()));
        }

        ClassLoader tcl = Thread.currentThread().getContextClassLoader();
        try {
            pushThreadContextClassLoader(sofaRuntimeContext.getAppClassLoader());
            retVal = invocation.getMethod().invoke(targetObj, invocation.getArguments());
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        } finally {
            LOGGER.atDebug().log(
                "<< Finish JVM service invoke, the service implementation is  - {}]",
                (this.target == null ? "null" : this.target.getClass().getName()));

            popThreadContextClassLoader(tcl);
        }

        return retVal;
    }

    @Override
    protected void doCatch(MethodInvocation invocation, Throwable e, long startTime) {
        LOGGER.atDebug().log(() -> getCommonInvocationLog("Exception", invocation, startTime));
    }

    @Override
    protected void doFinally(MethodInvocation invocation, long startTime) {
        LOGGER.atDebug().log(() -> getCommonInvocationLog("Finally", invocation, startTime));
    }

    protected Object getTarget() {
        if (this.target == null) {
            ServiceComponent serviceComponent = JvmServiceSupport.foundServiceComponent(
                sofaRuntimeContext.getComponentManager(), contract);

            if (serviceComponent != null) {
                this.target = serviceComponent.getImplementation().getTarget();
            }
        }
        return target;
    }

    protected Class<?> getInterfaceName() {
        return contract.getInterfaceType();
    }

    protected String getUniqueId() {
        return contract.getUniqueId();
    }
}

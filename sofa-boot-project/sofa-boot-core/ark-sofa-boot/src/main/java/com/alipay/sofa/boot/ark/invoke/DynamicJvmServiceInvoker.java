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
package com.alipay.sofa.boot.ark.invoke;

import com.alipay.sofa.ark.spi.replay.ReplayContext;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.service.ServiceProxy;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Subclass for {@link ServiceProxy} to transmit between ark biz.
 *
 * @author huzijie
 * @version DynamicJvmServiceInvoker.java, v 0.1 2023年01月16日 5:10 PM huzijie Exp $
 */
public class DynamicJvmServiceInvoker extends ServiceProxy {

    private static final Logger            LOGGER            = SofaBootLoggerFactory
                                                                 .getLogger(DynamicJvmServiceInvoker.class);

    private final Contract                 contract;
    private final Object                   targetService;
    private final String                   bizIdentity;
    private final ThreadLocal<ClassLoader> clientClassloader = new ThreadLocal<>();
    private final boolean                  serialize;

    static protected final String          TOSTRING_METHOD   = "toString";
    static protected final String          EQUALS_METHOD     = "equals";
    static protected final String          HASHCODE_METHOD   = "hashCode";

    public DynamicJvmServiceInvoker(ClassLoader clientClassloader, ClassLoader serviceClassLoader,
                                    Object targetService, Contract contract, String bizIdentity,
                                    boolean serialize) {
        super(serviceClassLoader);
        this.clientClassloader.set(clientClassloader);
        this.targetService = targetService;
        this.contract = contract;
        this.bizIdentity = bizIdentity;
        this.serialize = serialize;
    }

    @Override
    protected Object doInvoke(MethodInvocation invocation) throws Throwable {
        try {

            LOGGER.atDebug().log(() -> ">> Start in Cross App JVM service invoke, the service interface is  - "
                    + getInterfaceType());

            if (DynamicJvmServiceProxyFinder.getInstance().getBizManagerService() != null) {
                ReplayContext.setPlaceHolder();
            }

            Method targetMethod = invocation.getMethod();
            Object[] targetArguments = invocation.getArguments();

            if (TOSTRING_METHOD.equalsIgnoreCase(targetMethod.getName())
                && targetMethod.getParameterTypes().length == 0) {
                return targetService.toString();
            } else if (EQUALS_METHOD.equalsIgnoreCase(targetMethod.getName())
                       && targetMethod.getParameterTypes().length == 1) {
                return targetService.equals(targetArguments[0]);
            } else if (HASHCODE_METHOD.equalsIgnoreCase(targetMethod.getName())
                       && targetMethod.getParameterTypes().length == 0) {
                return targetService.hashCode();
            }

            Class[] oldArgumentTypes = targetMethod.getParameterTypes();
            Method transformMethod;
            // check whether skip serialize or not
            if (!serialize) {
                ClassLoader tcl = Thread.currentThread().getContextClassLoader();
                try {
                    pushThreadContextClassLoader(getServiceClassLoader());
                    transformMethod = getTargetMethod(targetMethod, oldArgumentTypes);
                    return transformMethod.invoke(targetService, targetArguments);
                } finally {
                    pushThreadContextClassLoader(tcl);
                }
            } else {
                Object[] arguments = (Object[]) hessianTransport(targetArguments, null);
                Class[] argumentTypes = (Class[]) hessianTransport(oldArgumentTypes, null);
                transformMethod = getTargetMethod(targetMethod, argumentTypes);
                Object retVal = transformMethod.invoke(targetService, arguments);
                return hessianTransport(retVal, getClientClassloader());
            }
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        } finally {
            if (DynamicJvmServiceProxyFinder.getInstance().getBizManagerService() != null) {
                ReplayContext.clearPlaceHolder();
            }
            clearClientClassloader();
        }
    }

    @Override
    protected void doCatch(MethodInvocation invocation, Throwable e, long startTime) {
        LOGGER.atDebug().log(() -> getCommonInvocationLog("Exception", invocation, startTime));
    }

    @Override
    protected void doFinally(MethodInvocation invocation, long startTime) {
        LOGGER.atDebug().log(() -> getCommonInvocationLog("Finally", invocation, startTime));
    }

    public boolean isSerialize() {
        return serialize;
    }

    public Class<?> getInterfaceType() {
        return contract.getInterfaceType();
    }

    public ClassLoader getClientClassloader() {
        return clientClassloader.get();
    }

    public void setClientClassloader(ClassLoader clientClassloader) {
        this.clientClassloader.set(clientClassloader);
    }

    public void clearClientClassloader() {
        this.clientClassloader.remove();
    }

    private Method getTargetMethod(Method method, Class<?>[] argumentTypes) {
        try {
            return targetService.getClass().getMethod(method.getName(), argumentTypes);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(targetService + " in " + bizIdentity
                                            + " don't have the method " + method);
        }
    }

    private static Object hessianTransport(Object source, ClassLoader contextClassLoader) {
        Object target;
        ClassLoader currentContextClassloader = Thread.currentThread().getContextClassLoader();
        try {
            if (contextClassLoader != null) {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }
            SerializerFactory serializerFactory = new SerializerFactory();
            serializerFactory.setAllowNonSerializable(true);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Hessian2Output h2o = new Hessian2Output(bos);
            h2o.setSerializerFactory(serializerFactory);
            h2o.writeObject(source);
            h2o.flush();
            byte[] content = bos.toByteArray();

            Hessian2Input h2i = new Hessian2Input(new ByteArrayInputStream(content));
            h2i.setSerializerFactory(serializerFactory);
            target = h2i.readObject();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            Thread.currentThread().setContextClassLoader(currentContextClassloader);
        }
        return target;
    }
}

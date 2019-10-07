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
package com.alipay.sofa.runtime.spring;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.runtime.log.SofaLogger;
import com.alipay.sofa.runtime.spring.async.AsyncInitBeanHolder;
import com.alipay.sofa.runtime.spring.async.AsyncTaskExecutor;
import com.alipay.sofa.runtime.spring.parser.AsyncInitBeanDefinitionDecorator;

/**
 * @author qilong.zql
 * @author xuanbei
 * @since 2.6.0
 */
public class AsyncProxyBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware,
                                        InitializingBean {

    private ApplicationContext applicationContext;

    private String             moduleName;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
                                                                               throws BeansException {
        String methodName = AsyncInitBeanHolder.getAsyncInitMethodName(moduleName, beanName);
        if (methodName == null || methodName.length() == 0) {
            return bean;
        }

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetClass(bean.getClass());
        proxyFactory.setProxyTargetClass(true);
        AsyncInitializeBeanMethodInvoker asyncInitializeBeanMethodInvoker = new AsyncInitializeBeanMethodInvoker(
            bean, beanName, methodName);
        proxyFactory.addAdvice(asyncInitializeBeanMethodInvoker);
        return proxyFactory.getProxy();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
                                                                              throws BeansException {
        return bean;
    }

    @Override
    public void afterPropertiesSet() {
        ConfigurableBeanFactory beanFactory = ((AbstractApplicationContext) applicationContext)
            .getBeanFactory();
        if (AsyncInitBeanDefinitionDecorator.isBeanLoadCostBeanFactory(beanFactory.getClass())) {
            moduleName = AsyncInitBeanDefinitionDecorator.getModuleNameFromBeanFactory(beanFactory);
        } else {
            moduleName = SofaBootConstants.ROOT_APPLICATION_CONTEXT;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    class AsyncInitializeBeanMethodInvoker implements MethodInterceptor {
        private final Object         targetObject;
        private final String         asyncMethodName;
        private final String         beanName;
        private final CountDownLatch initCountDownLatch = new CountDownLatch(1);
        // mark async-init method is during first invocation.
        private volatile boolean     isAsyncCalling     = false;
        // mark init-method is called.
        private volatile boolean     isAsyncCalled      = false;

        AsyncInitializeBeanMethodInvoker(Object targetObject, String beanName, String methodName) {
            this.targetObject = targetObject;
            this.beanName = beanName;
            this.asyncMethodName = methodName;
        }

        @Override
        public Object invoke(final MethodInvocation invocation) throws Throwable {
            // if the spring refreshing is finished
            if (AsyncTaskExecutor.isStarted()) {
                return invocation.getMethod().invoke(targetObject, invocation.getArguments());
            }

            Method method = invocation.getMethod();
            final String methodName = method.getName();
            if (!isAsyncCalled && methodName.equals(asyncMethodName)) {
                isAsyncCalled = true;
                isAsyncCalling = true;
                AsyncTaskExecutor.submitTask(applicationContext.getEnvironment(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            long startTime = System.currentTimeMillis();
                            invocation.getMethod().invoke(targetObject, invocation.getArguments());
                            SofaLogger.info(String.format(
                                "%s(%s) %s method execute %dms, moduleName: %s.", targetObject
                                    .getClass().getName(), beanName, methodName, (System
                                    .currentTimeMillis() - startTime), moduleName));
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        } finally {
                            initCountDownLatch.countDown();
                            isAsyncCalling = false;
                        }
                    }
                });
                return null;
            }

            if (isAsyncCalling) {
                long startTime = System.currentTimeMillis();
                initCountDownLatch.await();
                SofaLogger.info(String.format("%s(%s) %s method wait %dms, moduleName: %s.",
                    targetObject.getClass().getName(), beanName, methodName,
                    (System.currentTimeMillis() - startTime), moduleName));
            }
            return invocation.getMethod().invoke(targetObject, invocation.getArguments());
        }
    }

}
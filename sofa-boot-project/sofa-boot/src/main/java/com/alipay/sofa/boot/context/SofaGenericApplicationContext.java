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
package com.alipay.sofa.boot.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Default Implementation of {@link GenericApplicationContext} in SOFABoot framework.
 *
 * <p>Support not publish event to parent context,
 * also could use {@link ContextRefreshInterceptor} hook to aware context refresh call.
 *
 * @author xuanbei 18/3/5
 * @author huzijie
 * @since 4.0.0
 */
public class SofaGenericApplicationContext extends GenericApplicationContext {

    private static final Method             GET_APPLICATION_EVENT_MULTICASTER_METHOD;

    private static final Field              EARLY_APPLICATION_EVENTS_FIELD;

    private List<ContextRefreshInterceptor> interceptors = new ArrayList<>();

    private boolean                         publishEventToParent;

    static {
        try {
            GET_APPLICATION_EVENT_MULTICASTER_METHOD = AbstractApplicationContext.class
                .getDeclaredMethod("getApplicationEventMulticaster");
            GET_APPLICATION_EVENT_MULTICASTER_METHOD.setAccessible(true);
            EARLY_APPLICATION_EVENTS_FIELD = AbstractApplicationContext.class
                .getDeclaredField("earlyApplicationEvents");
            EARLY_APPLICATION_EVENTS_FIELD.setAccessible(true);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public SofaGenericApplicationContext() {
        this(new SofaDefaultListableBeanFactory());
    }

    /**
     * Create a new SofaApplicationContext with the given DefaultListableBeanFactory.
     * @param beanFactory the DefaultListableBeanFactory instance to use for this context
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public SofaGenericApplicationContext(SofaDefaultListableBeanFactory beanFactory) {
        super(beanFactory);
    }

    @Override
    public void refresh() throws BeansException, IllegalStateException {
        if (CollectionUtils.isEmpty(interceptors)) {
            super.refresh();
            return;
        }
        Throwable throwable = null;
        try {
            applyInterceptorBeforeRefresh();
            super.refresh();
        } catch (Throwable t) {
            throwable = t;
            throw t;
        } finally {
            applyInterceptorAfterRefresh(throwable);
        }
    }

    /**
     * @see org.springframework.context.support.AbstractApplicationContext#publishEvent(java.lang.Object, org.springframework.core.ResolvableType)
     */
    @Override
    protected void publishEvent(Object event, ResolvableType eventType) {
        if (publishEventToParent) {
            super.publishEvent(event, eventType);
            return;
        }
        Assert.notNull(event, "Event must not be null");

        // Decorate event as an ApplicationEvent if necessary
        ApplicationEvent applicationEvent;
        if (event instanceof ApplicationEvent) {
            applicationEvent = (ApplicationEvent) event;
        } else {
            applicationEvent = new PayloadApplicationEvent<>(this, event);
            if (eventType == null) {
                eventType = ((PayloadApplicationEvent<?>) applicationEvent).getResolvableType();
            }
        }

        Set<ApplicationEvent> earlyApplicationEvents = getFieldValueByReflect(
            EARLY_APPLICATION_EVENTS_FIELD, this);
        if (earlyApplicationEvents != null) {
            earlyApplicationEvents.add(applicationEvent);
        } else {
            ApplicationEventMulticaster applicationEventMulticaster = getMethodValueByReflect(
                GET_APPLICATION_EVENT_MULTICASTER_METHOD, this);
            applicationEventMulticaster.multicastEvent(applicationEvent, eventType);
        }
    }

    protected void applyInterceptorBeforeRefresh() {
        interceptors.forEach(interceptor -> interceptor.beforeRefresh(this));
    }

    protected void applyInterceptorAfterRefresh(Throwable throwable) {
        interceptors.forEach(interceptor -> interceptor.afterRefresh(this, throwable));
    }

    public void setPublishEventToParent(boolean publishEventToParent) {
        this.publishEventToParent = publishEventToParent;
    }

    public void setInterceptors(List<ContextRefreshInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    @SuppressWarnings("unchecked")
    private <T> T getFieldValueByReflect(Field field, Object obj) {
        try {
            return (T) field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getMethodValueByReflect(Method method, Object obj, Object... args) {
        try {
            return (T) method.invoke(obj, args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}

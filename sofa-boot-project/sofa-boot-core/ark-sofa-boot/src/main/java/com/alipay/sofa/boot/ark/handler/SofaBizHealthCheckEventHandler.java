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
package com.alipay.sofa.boot.ark.handler;

import com.alipay.sofa.ark.spi.event.biz.AfterBizStartupEvent;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.service.PriorityOrdered;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import com.alipay.sofa.boot.ark.SofaRuntimeContainer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;

/**
 * Implementation of {@link EventHandler<AfterBizStartupEvent>} to check readiness health check result.
 *
 * @author qilong.zql
 * @author huzijie
 * @since 2.5.0
 */
public class SofaBizHealthCheckEventHandler implements EventHandler<AfterBizStartupEvent> {

    private static final String READINESS_CHECK_LISTENER_CLASS = "com.alipay.sofa.boot.actuator.health.ReadinessCheckListener";

    private static boolean      isReadinessCheckListenerClassExist;

    static {
        try {
            Class.forName(READINESS_CHECK_LISTENER_CLASS);
            isReadinessCheckListenerClassExist = true;
        } catch (ClassNotFoundException e) {
            isReadinessCheckListenerClassExist = false;
        }

    }

    @Override
    public void handleEvent(AfterBizStartupEvent event) {
        doHealthCheck(event.getSource());
    }

    private void doHealthCheck(Biz biz) {
        if (!isReadinessCheckListenerClassExist) {
            return;
        }

        ApplicationContext applicationContext = SofaRuntimeContainer.getApplicationContext(biz
            .getBizClassLoader());

        if (applicationContext == null) {
            throw new IllegalStateException("No application match classLoader");
        }

        ObjectProvider<ReadinessCheckListener> provider = applicationContext
            .getBeanProvider(ReadinessCheckListener.class);
        ReadinessCheckListener readinessCheckListener = provider.getIfUnique();

        if (readinessCheckListener != null) {
            if (!readinessCheckListener.aggregateReadinessHealth().getStatus().equals(Status.UP)) {
                throw new RuntimeException("Readiness health check failed.");
            }
        }
    }

    @Override
    public int getPriority() {
        return PriorityOrdered.DEFAULT_PRECEDENCE;
    }
}

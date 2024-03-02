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

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link SofaGenericApplicationContext}.
 *
 * @author huzijie
 * @version SofaGenericApplicationContextTests.java, v 0.1 2023年02月01日 12:22 PM huzijie Exp $
 */
public class SofaGenericApplicationContextTests {

    @Test
    public void publishEventToParent() {
        SofaGenericApplicationContext parent = new SofaGenericApplicationContext(
            new SofaDefaultListableBeanFactory());
        TestApplicationListener parentListener = new TestApplicationListener();
        parent.addApplicationListener(parentListener);
        parent.refresh();
        assertThat(parentListener.getCount()).isEqualTo(1);

        SofaGenericApplicationContext child = new SofaGenericApplicationContext(
            new SofaDefaultListableBeanFactory());
        child.setPublishEventToParent(false);
        child.setParent(parent);
        TestApplicationListener childListener1 = new TestApplicationListener();
        child.addApplicationListener(childListener1);
        child.refresh();
        assertThat(childListener1.getCount()).isEqualTo(1);
        assertThat(parentListener.getCount()).isEqualTo(1);

        child = new SofaGenericApplicationContext(new SofaDefaultListableBeanFactory());
        child.setPublishEventToParent(true);
        child.setParent(parent);
        TestApplicationListener childListener2 = new TestApplicationListener();
        child.addApplicationListener(childListener2);
        child.refresh();
        assertThat(childListener2.getCount()).isEqualTo(1);
        assertThat(parentListener.getCount()).isEqualTo(2);
    }

    @Test
    public void contextRefreshSuccessTriggerInterceptors() {
        SofaGenericApplicationContext context = new SofaGenericApplicationContext(
            new SofaDefaultListableBeanFactory());
        List<ContextRefreshInterceptor> interceptors = new ArrayList<>();
        TestContextRefreshInterceptor interceptor = new TestContextRefreshInterceptor();
        interceptors.add(interceptor);
        context.setInterceptors(interceptors);
        context.refresh();
        assertThat(interceptor.isStarted()).isTrue();
        assertThat(interceptor.isFinished()).isTrue();
        assertThat(interceptor.isFailed()).isFalse();
    }

    @Test
    public void contextRefreshFailTriggerInterceptors() {
        SofaGenericApplicationContext context = new SofaGenericApplicationContext(
            new SofaDefaultListableBeanFactory());
        List<ContextRefreshInterceptor> interceptors = new ArrayList<>();
        TestContextRefreshInterceptor interceptor = new TestContextRefreshInterceptor();
        interceptors.add(interceptor);
        context.setInterceptors(interceptors);
        context.registerBean(ExceptionBean.class);
        assertThatThrownBy(context::refresh).hasRootCauseMessage("Init failed");
        assertThat(interceptor.isStarted()).isTrue();
        assertThat(interceptor.isFinished()).isTrue();
        assertThat(interceptor.isFailed()).isTrue();
    }

    static class TestApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

        public int count;

        @Override
        public void onApplicationEvent(ContextRefreshedEvent event) {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    static class ExceptionBean {

        public ExceptionBean() {
            throw new RuntimeException("Init failed");
        }
    }

    static class TestContextRefreshInterceptor implements ContextRefreshInterceptor {

        private boolean started  = false;

        private boolean finished = false;

        private boolean failed   = false;

        @Override
        public void beforeRefresh(SofaGenericApplicationContext context) {
            started = true;
        }

        @Override
        public void afterRefresh(SofaGenericApplicationContext context, Throwable throwable) {
            finished = true;
            failed = throwable != null;
        }



        public boolean isStarted() {
            return started;
        }

        public boolean isFinished() {
            return finished;
        }

        public boolean isFailed() {
            return failed;
        }
    }
}

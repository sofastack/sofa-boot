/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.alipay.sofa.runtime.spring.async;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

/**
 * @author qilong.zql
 * @since 2.6.0
 */
public class AsyncTaskExecutionListener implements PriorityOrdered,
        ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        AsyncTaskExecutor.ensureAsyncTasksFinish();
    }

    @Override
    public int getOrder() {
        // invoked after {@literal com.alipay.sofa.isle.spring.listener.SofaModuleContextRefreshedListener}
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
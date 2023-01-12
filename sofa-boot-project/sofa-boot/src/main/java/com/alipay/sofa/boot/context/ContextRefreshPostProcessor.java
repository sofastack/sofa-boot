package com.alipay.sofa.boot.context;

/**
 * @author huzijie
 * @version ContextRefreshPostProcessor.java, v 0.1 2023年01月12日 1:58 PM huzijie Exp $
 */
public interface ContextRefreshPostProcessor {

    default void postProcessBeforeRefresh(SofaGenericApplicationContext context) {

    }

    default void postProcessAfterRefresh(SofaGenericApplicationContext context, Throwable throwable) {

    }
}

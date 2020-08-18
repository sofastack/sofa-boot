package com.alipay.sofa.runtime.ambush;

import org.springframework.core.Ordered;

/**
 * Filter for JVM service invoking.
 * Multiple filters are called in ascending order.
 *
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/8/18
 */
public interface Filter extends Ordered {
    /**
     * This method is called before the actual JVM service invoking.
     * Getting and setting of <code>invokeResult</code> of context make no sense here.
     * @param context JVM invoking context
     */
    void before(Context context);

    /**
     * This method is called after the actual JVM service invoking.
     * Filter can replace the <code>invokeResult</code> of context to do something nasty.
     * @param context JVM invoking context
     */
    void after(Context context);
}

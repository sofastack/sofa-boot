package com.alipay.sofa.boot.util;

/**
 * @author huzijie
 * @version ClassLoaderSwitchUtils.java, v 0.1 2023年01月12日 10:35 AM huzijie Exp $
 */
public class ClassLoaderContextUtils {

    public static void runAndRollbackTCCL(Runnable runnable, ClassLoader newClassloader) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(newClassloader);
        try {
            runnable.run();
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    public static void runAndRollbackClassLoader(Runnable runnable, ClassLoader oldClassLoader, ClassLoader newClassloader) {
        Thread.currentThread().setContextClassLoader(newClassloader);
        try {
            runnable.run();
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }
}

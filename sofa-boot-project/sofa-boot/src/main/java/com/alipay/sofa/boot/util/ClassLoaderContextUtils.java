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
package com.alipay.sofa.boot.util;

import java.util.concurrent.Callable;

/**
 * Utility methods that are useful for handle class loader context.
 *
 * @author huzijie
 * @version ClassLoaderContextUtils.java, v 0.1 2023年01月12日 10:35 AM huzijie Exp $
 */
public class ClassLoaderContextUtils {

    /**
     * Store thread context classloader then use new classloader to invoke runnable,
     * finally reset thread context classloader.
     * @param runnable runnable to invoke
     * @param newClassloader classloader used to invoke runnable
     */
    public static void runWithTemporaryContextClassloader(Runnable runnable,
                                                          ClassLoader newClassloader) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(newClassloader);
        try {
            runnable.run();
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    /**
     * Store thread context classloader then use new classloader to invoke callable,
     * finally reset thread context classloader.
     * @param callable callable to invoke
     * @param newClassloader classloader used to invoke callable
     */
    public static <T> T callWithTemporaryContextClassloader(Callable<T> callable,
                                                            ClassLoader newClassloader) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(newClassloader);
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke callable", e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }
}

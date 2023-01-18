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

/**
 * Hook that allows for custom interceptor invoke before or after context refresh.
 *
 * @author huzijie
 * @version ContextRefreshInterceptor.java, v 0.1 2023年01月12日 1:58 PM huzijie Exp $
 * @since 4.0.0
 */
public interface ContextRefreshInterceptor {

    /**
     * Invoke before application context refresh
     * @param context application context
     */
    default void beforeRefresh(SofaGenericApplicationContext context) {}

    /**
     * Invoke after application context refresh
     * @param context application context
     * @param throwable not null when refresh failed
     */
    default void afterRefresh(SofaGenericApplicationContext context, Throwable throwable) {}
}

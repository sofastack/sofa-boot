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
package com.alipay.sofa.boot.autoconfigure.runtime;

import com.alipay.sofa.runtime.async.AsyncInitAutoMode;

/**
 * Configuration properties for async bean initialization.
 *
 * @author OpenAI
 */
public class AsyncInitProperties {

    /**
     * Whether async initialization is enabled.
     */
    private boolean           enabled       = true;

    /**
     * Core async init executor size.
     */
    private int               corePoolSize  = Runtime.getRuntime().availableProcessors();

    /**
     * Max async init executor size.
     */
    private int               maxPoolSize   = corePoolSize * 2;

    /**
     * Async init executor queue capacity.
     */
    private int               queueCapacity = 100;

    /**
     * Timeout in milliseconds to wait for async init tasks.
     */
    private long              timeoutMillis = 30000;

    /**
     * Automatic candidate detection mode.
     */
    private AsyncInitAutoMode autoMode      = AsyncInitAutoMode.CONSERVATIVE;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public AsyncInitAutoMode getAutoMode() {
        return autoMode;
    }

    public void setAutoMode(AsyncInitAutoMode autoMode) {
        this.autoMode = autoMode;
    }
}

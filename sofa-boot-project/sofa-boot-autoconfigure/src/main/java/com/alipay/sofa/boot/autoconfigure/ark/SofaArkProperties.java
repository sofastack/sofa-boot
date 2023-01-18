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
package com.alipay.sofa.boot.autoconfigure.ark;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties to configure sofa ark.
 *
 * @author huzijie
 * @version SofaArkProperties.java, v 0.1 2023年01月16日 7:48 PM huzijie Exp $
 */
@ConfigurationProperties("sofa.boot.ark")
public class SofaArkProperties {

    /**
     * Whether enable jvm service cache.
     */
    private boolean jvmServiceCache    = false;

    /**
     * Whether enable jvm service invoke serialize.
     */
    private boolean jvmInvokeSerialize = true;

    public boolean isJvmServiceCache() {
        return jvmServiceCache;
    }

    public void setJvmServiceCache(boolean jvmServiceCache) {
        this.jvmServiceCache = jvmServiceCache;
    }

    public boolean isJvmInvokeSerialize() {
        return jvmInvokeSerialize;
    }

    public void setJvmInvokeSerialize(boolean jvmInvokeSerialize) {
        this.jvmInvokeSerialize = jvmInvokeSerialize;
    }
}

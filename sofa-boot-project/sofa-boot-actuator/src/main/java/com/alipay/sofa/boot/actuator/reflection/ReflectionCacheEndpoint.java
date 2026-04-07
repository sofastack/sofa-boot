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
package com.alipay.sofa.boot.actuator.reflection;

import com.alipay.sofa.boot.reflection.ReflectionCache;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;

/**
 * Endpoint for viewing and clearing the reflection cache.
 *
 * @author xiaosiyuan
 * @since 4.5.0
 */
@Endpoint(id = "reflection-cache")
public class ReflectionCacheEndpoint {

    private final ReflectionCache reflectionCache;

    public ReflectionCacheEndpoint(ReflectionCache reflectionCache) {
        this.reflectionCache = reflectionCache;
    }

    @ReadOperation
    public ReflectionCache.CacheStats stats() {
        return reflectionCache.getStats();
    }

    @WriteOperation
    public ReflectionCache.CacheStats clear() {
        reflectionCache.clear();
        return reflectionCache.getStats();
    }
}

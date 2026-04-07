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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ReflectionCacheEndpoint}.
 *
 * @author xiaosiyuan
 * @since 4.5.0
 */
public class ReflectionCacheEndpointTests {

    @Test
    void statsShouldExposeReflectionCacheState() throws Exception {
        ReflectionCache reflectionCache = new ReflectionCache();
        ReflectionCacheEndpoint endpoint = new ReflectionCacheEndpoint(reflectionCache);

        reflectionCache.forName(String.class.getName());
        reflectionCache.forName(String.class.getName());

        ReflectionCache.CacheStats stats = endpoint.stats();
        assertThat(stats.isEnabled()).isTrue();
        assertThat(stats.getClassHitCount()).isEqualTo(1);
        assertThat(stats.getClassMissCount()).isEqualTo(1);
        assertThat(stats.getClassCacheSize()).isEqualTo(1);
    }

    @Test
    void clearShouldResetReflectionCacheState() throws Exception {
        ReflectionCache reflectionCache = new ReflectionCache();
        ReflectionCacheEndpoint endpoint = new ReflectionCacheEndpoint(reflectionCache);

        reflectionCache.forName(String.class.getName());
        reflectionCache.findMethod(String.class, "trim");
        reflectionCache.getDeclaredConstructor(String.class, String.class);

        ReflectionCache.CacheStats stats = endpoint.clear();
        assertThat(stats.getTotalHitCount()).isZero();
        assertThat(stats.getTotalMissCount()).isZero();
        assertThat(stats.getClassCacheSize()).isZero();
        assertThat(stats.getMethodCacheSize()).isZero();
        assertThat(stats.getConstructorCacheSize()).isZero();
    }
}

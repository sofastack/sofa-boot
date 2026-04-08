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
package com.alipay.sofa.boot.reflection;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ReflectionCache}.
 *
 * @author xiaosiyuan
 */
public class ReflectionCacheTests {

    @Test
    void cacheHitsShouldBeRecordedForSuccessfulLookups() throws Exception {
        ReflectionCache reflectionCache = new ReflectionCache();

        Class<?> type = reflectionCache.forName(SampleTarget.class.getName());
        Class<?> cachedType = reflectionCache.forName(SampleTarget.class.getName());
        Method method = reflectionCache.findMethod(SampleTarget.class, "greet", String.class);
        Method cachedMethod = reflectionCache.findMethod(SampleTarget.class, "greet", String.class);
        Constructor<SampleTarget> constructor = reflectionCache.getDeclaredConstructor(
            SampleTarget.class, String.class);
        Constructor<SampleTarget> cachedConstructor = reflectionCache.getDeclaredConstructor(
            SampleTarget.class, String.class);

        assertThat(type).isSameAs(SampleTarget.class);
        assertThat(cachedType).isSameAs(type);
        assertThat(method).isNotNull();
        assertThat(cachedMethod).isSameAs(method);
        assertThat(constructor).isNotNull();
        assertThat(cachedConstructor).isSameAs(constructor);
        assertThat(constructor.newInstance("SOFA").greet("Boot")).isEqualTo("SOFABoot");

        ReflectionCache.CacheStats stats = reflectionCache.getStats();
        assertThat(stats.isEnabled()).isTrue();
        assertThat(stats.getClassHitCount()).isEqualTo(1);
        assertThat(stats.getClassMissCount()).isEqualTo(1);
        assertThat(stats.getClassCacheSize()).isEqualTo(1);
        assertThat(stats.getMethodHitCount()).isEqualTo(1);
        assertThat(stats.getMethodMissCount()).isEqualTo(1);
        assertThat(stats.getMethodCacheSize()).isEqualTo(1);
        assertThat(stats.getConstructorHitCount()).isEqualTo(1);
        assertThat(stats.getConstructorMissCount()).isEqualTo(1);
        assertThat(stats.getConstructorCacheSize()).isEqualTo(1);
        assertThat(stats.getHitRate()).isEqualTo(0.5D);
    }

    @Test
    void cacheMissesShouldAlsoBeCached() {
        ReflectionCache reflectionCache = new ReflectionCache();

        assertThatThrownBy(
            () -> reflectionCache.forName("com.alipay.sofa.boot.reflection.DoesNotExist"))
                .isInstanceOf(ClassNotFoundException.class);
        assertThatThrownBy(
            () -> reflectionCache.forName("com.alipay.sofa.boot.reflection.DoesNotExist"))
                .isInstanceOf(ClassNotFoundException.class);
        assertThat(reflectionCache.findMethod(SampleTarget.class, "missingMethod")).isNull();
        assertThat(reflectionCache.findMethod(SampleTarget.class, "missingMethod")).isNull();
        assertThatThrownBy(
            () -> reflectionCache.getDeclaredConstructor(SampleTarget.class, Integer.class))
                .isInstanceOf(NoSuchMethodException.class);
        assertThatThrownBy(
            () -> reflectionCache.getDeclaredConstructor(SampleTarget.class, Integer.class))
                .isInstanceOf(NoSuchMethodException.class);

        ReflectionCache.CacheStats stats = reflectionCache.getStats();
        assertThat(stats.getClassHitCount()).isEqualTo(1);
        assertThat(stats.getClassMissCount()).isEqualTo(1);
        assertThat(stats.getClassMissCacheSize()).isEqualTo(1);
        assertThat(stats.getMethodHitCount()).isEqualTo(1);
        assertThat(stats.getMethodMissCount()).isEqualTo(1);
        assertThat(stats.getMethodMissCacheSize()).isEqualTo(1);
        assertThat(stats.getConstructorHitCount()).isEqualTo(1);
        assertThat(stats.getConstructorMissCount()).isEqualTo(1);
        assertThat(stats.getConstructorMissCacheSize()).isEqualTo(1);
    }

    @Test
    void disabledCacheShouldBypassStatistics() throws Exception {
        ReflectionCache reflectionCache = new ReflectionCache(false);

        assertThat(reflectionCache.isEnabled()).isFalse();
        assertThat(reflectionCache.forName(SampleTarget.class.getName())).isSameAs(
            SampleTarget.class);
        assertThat(reflectionCache.findMethod(SampleTarget.class, "greet", String.class))
            .isNotNull();
        assertThat(reflectionCache.getDeclaredConstructor(SampleTarget.class, String.class))
            .isNotNull();

        ReflectionCache.CacheStats stats = reflectionCache.getStats();
        assertThat(stats.isEnabled()).isFalse();
        assertThat(stats.getTotalHitCount()).isZero();
        assertThat(stats.getTotalMissCount()).isZero();
        assertThat(stats.getClassCacheSize()).isZero();
        assertThat(stats.getMethodCacheSize()).isZero();
        assertThat(stats.getConstructorCacheSize()).isZero();
    }

    static class SampleTarget {

        private final String prefix;

        private SampleTarget(String prefix) {
            this.prefix = prefix;
        }

        public String greet(String suffix) {
            return prefix + suffix;
        }
    }
}

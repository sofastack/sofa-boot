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

import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * Caches repeated reflection lookups.
 *
 * @author xiaosiyuan
 * @since 4.5.0
 */
public class ReflectionCache {

    private static final Class<?>[]                                    EMPTY_PARAMETER_TYPES = new Class<?>[0];

    private final LookupStore<ClassKey, ClassLookupResult>             classLookup           = new LookupStore<>();

    private final LookupStore<MethodKey, MethodLookupResult>           methodLookup          = new LookupStore<>();

    private final LookupStore<ConstructorKey, ConstructorLookupResult> constructorLookup     = new LookupStore<>();

    private volatile boolean                                           enabled;

    public ReflectionCache() {
        this(true);
    }

    public ReflectionCache(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            clear();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Class<?> forName(String className) throws ClassNotFoundException {
        return forName(className, null);
    }

    public Class<?> forName(String className, @Nullable ClassLoader classLoader)
                                                                                throws ClassNotFoundException {
        if (!enabled) {
            return doForName(className, classLoader);
        }

        ClassLookupResult result = classLookup.get(new ClassKey(className, classLoader), cacheKey -> {
            try {
                return ClassLookupResult.found(doForName(cacheKey.className, cacheKey.classLoader));
            } catch (ClassNotFoundException exception) {
                return ClassLookupResult.missed();
            }
        });
        if (result.isMiss()) {
            throw new ClassNotFoundException(className);
        }
        return result.type;
    }

    @Nullable
    public Method findMethod(Class<?> targetClass, String methodName, Class<?>... parameterTypes) {
        Class<?>[] normalizedParameterTypes = normalizeParameterTypes(parameterTypes);
        if (!enabled) {
            return doFindMethod(targetClass, methodName, normalizedParameterTypes);
        }

        MethodLookupResult result = methodLookup.get(new MethodKey(targetClass, methodName,
            normalizedParameterTypes), cacheKey -> new MethodLookupResult(
                doFindMethod(cacheKey.targetClass, cacheKey.methodName, cacheKey.parameterTypes)));
        return result.method;
    }

    public <T> Constructor<T> getDeclaredConstructor(Class<T> targetClass,
                                                     Class<?>... parameterTypes)
                                                                                throws NoSuchMethodException {
        Class<?>[] normalizedParameterTypes = normalizeParameterTypes(parameterTypes);
        if (!enabled) {
            return doGetDeclaredConstructor(targetClass, normalizedParameterTypes);
        }

        ConstructorLookupResult result = constructorLookup.get(new ConstructorKey(targetClass,
            normalizedParameterTypes), cacheKey -> {
            try {
                Constructor<?> constructor = doGetDeclaredConstructor(cacheKey.targetClass,
                    cacheKey.parameterTypes);
                return ConstructorLookupResult.found(constructor);
            } catch (NoSuchMethodException exception) {
                return ConstructorLookupResult.missed();
            }
        });
        if (result.isMiss()) {
            throw new NoSuchMethodException(buildConstructorSignature(targetClass,
                normalizedParameterTypes));
        }
        return castConstructor(result.constructor);
    }

    public CacheStats getStats() {
        LookupStats classStats = classLookup.getStats();
        LookupStats methodStats = methodLookup.getStats();
        LookupStats constructorStats = constructorLookup.getStats();
        return new CacheStats(enabled, classStats.hitCount, classStats.missCount,
            classStats.cacheSize, classStats.missCacheSize, methodStats.hitCount,
            methodStats.missCount, methodStats.cacheSize, methodStats.missCacheSize,
            constructorStats.hitCount, constructorStats.missCount, constructorStats.cacheSize,
            constructorStats.missCacheSize);
    }

    public void clear() {
        classLookup.clear();
        methodLookup.clear();
        constructorLookup.clear();
    }

    private Class<?> doForName(String className, @Nullable ClassLoader classLoader)
                                                                                   throws ClassNotFoundException {
        if (classLoader == null) {
            return Class.forName(className);
        }
        return Class.forName(className, true, classLoader);
    }

    @Nullable
    private Method doFindMethod(Class<?> targetClass, String methodName, Class<?>[] parameterTypes) {
        return ReflectionUtils.findMethod(targetClass, methodName, parameterTypes);
    }

    private <T> Constructor<T> doGetDeclaredConstructor(Class<T> targetClass,
                                                        Class<?>[] parameterTypes)
                                                                                  throws NoSuchMethodException {
        Constructor<T> constructor = targetClass.getDeclaredConstructor(parameterTypes);
        ReflectionUtils.makeAccessible(constructor);
        return constructor;
    }

    private String buildConstructorSignature(Class<?> targetClass, Class<?>[] parameterTypes) {
        String parameterTypeNames = Arrays.stream(parameterTypes).map(Class::getName).reduce(
            (left, right) -> left + "," + right).orElse("");
        return targetClass.getName() + ".<init>(" + parameterTypeNames + ")";
    }

    private Class<?>[] normalizeParameterTypes(@Nullable Class<?>[] parameterTypes) {
        if (parameterTypes == null || parameterTypes.length == 0) {
            return EMPTY_PARAMETER_TYPES;
        }
        return parameterTypes.clone();
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> castConstructor(Constructor<?> constructor) {
        return (Constructor<T>) constructor;
    }

    public static final class CacheStats {

        private final boolean enabled;

        private final long    classHitCount;

        private final long    classMissCount;

        private final int     classCacheSize;

        private final int     classMissCacheSize;

        private final long    methodHitCount;

        private final long    methodMissCount;

        private final int     methodCacheSize;

        private final int     methodMissCacheSize;

        private final long    constructorHitCount;

        private final long    constructorMissCount;

        private final int     constructorCacheSize;

        private final int     constructorMissCacheSize;

        public CacheStats(boolean enabled, long classHitCount, long classMissCount,
                          int classCacheSize, int classMissCacheSize, long methodHitCount,
                          long methodMissCount, int methodCacheSize, int methodMissCacheSize,
                          long constructorHitCount, long constructorMissCount,
                          int constructorCacheSize, int constructorMissCacheSize) {
            this.enabled = enabled;
            this.classHitCount = classHitCount;
            this.classMissCount = classMissCount;
            this.classCacheSize = classCacheSize;
            this.classMissCacheSize = classMissCacheSize;
            this.methodHitCount = methodHitCount;
            this.methodMissCount = methodMissCount;
            this.methodCacheSize = methodCacheSize;
            this.methodMissCacheSize = methodMissCacheSize;
            this.constructorHitCount = constructorHitCount;
            this.constructorMissCount = constructorMissCount;
            this.constructorCacheSize = constructorCacheSize;
            this.constructorMissCacheSize = constructorMissCacheSize;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public long getClassHitCount() {
            return classHitCount;
        }

        public long getClassMissCount() {
            return classMissCount;
        }

        public int getClassCacheSize() {
            return classCacheSize;
        }

        public int getClassMissCacheSize() {
            return classMissCacheSize;
        }

        public long getMethodHitCount() {
            return methodHitCount;
        }

        public long getMethodMissCount() {
            return methodMissCount;
        }

        public int getMethodCacheSize() {
            return methodCacheSize;
        }

        public int getMethodMissCacheSize() {
            return methodMissCacheSize;
        }

        public long getConstructorHitCount() {
            return constructorHitCount;
        }

        public long getConstructorMissCount() {
            return constructorMissCount;
        }

        public int getConstructorCacheSize() {
            return constructorCacheSize;
        }

        public int getConstructorMissCacheSize() {
            return constructorMissCacheSize;
        }

        public long getTotalHitCount() {
            return classHitCount + methodHitCount + constructorHitCount;
        }

        public long getTotalMissCount() {
            return classMissCount + methodMissCount + constructorMissCount;
        }

        public double getHitRate() {
            long total = getTotalHitCount() + getTotalMissCount();
            if (total == 0) {
                return 0D;
            }
            return (double) getTotalHitCount() / total;
        }
    }

    private interface LookupResult {

        boolean isMiss();
    }

    private static final class LookupStore<K, V extends LookupResult> {

        private final ConcurrentHashMap<K, V> cache     = new ConcurrentHashMap<>();

        private final AtomicLong              hitCount  = new AtomicLong();

        private final AtomicLong              missCount = new AtomicLong();

        private V get(K key, Function<K, V> loader) {
            AtomicBoolean cacheMiss = new AtomicBoolean(false);
            V result = cache.computeIfAbsent(key, cacheKey -> {
                cacheMiss.set(true);
                return loader.apply(cacheKey);
            });
            if (cacheMiss.get()) {
                missCount.incrementAndGet();
            } else {
                hitCount.incrementAndGet();
            }
            return result;
        }

        private LookupStats getStats() {
            return new LookupStats(hitCount.get(), missCount.get(), cache.size(),
                (int) cache.values().stream().filter(LookupResult::isMiss).count());
        }

        private void clear() {
            cache.clear();
            hitCount.set(0);
            missCount.set(0);
        }
    }

    private static final class LookupStats {

        private final long hitCount;

        private final long missCount;

        private final int  cacheSize;

        private final int  missCacheSize;

        private LookupStats(long hitCount, long missCount, int cacheSize, int missCacheSize) {
            this.hitCount = hitCount;
            this.missCount = missCount;
            this.cacheSize = cacheSize;
            this.missCacheSize = missCacheSize;
        }
    }

    private static final class ClassLookupResult implements LookupResult {

        private final Class<?> type;

        private final boolean  miss;

        private ClassLookupResult(Class<?> type, boolean miss) {
            this.type = type;
            this.miss = miss;
        }

        private static ClassLookupResult found(Class<?> type) {
            return new ClassLookupResult(type, false);
        }

        private static ClassLookupResult missed() {
            return new ClassLookupResult(null, true);
        }

        @Override
        public boolean isMiss() {
            return miss;
        }
    }

    private static final class MethodLookupResult implements LookupResult {

        private final Method method;

        private MethodLookupResult(Method method) {
            this.method = method;
        }

        @Override
        public boolean isMiss() {
            return method == null;
        }
    }

    private static final class ConstructorLookupResult implements LookupResult {

        private final Constructor<?> constructor;

        private final boolean        miss;

        private ConstructorLookupResult(Constructor<?> constructor, boolean miss) {
            this.constructor = constructor;
            this.miss = miss;
        }

        private static ConstructorLookupResult found(Constructor<?> constructor) {
            return new ConstructorLookupResult(constructor, false);
        }

        private static ConstructorLookupResult missed() {
            return new ConstructorLookupResult(null, true);
        }

        @Override
        public boolean isMiss() {
            return miss;
        }
    }

    private static final class ClassKey {

        private final String      className;

        private final ClassLoader classLoader;

        private ClassKey(String className, @Nullable ClassLoader classLoader) {
            this.className = className;
            this.classLoader = classLoader;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof ClassKey that)) {
                return false;
            }
            return Objects.equals(className, that.className) && classLoader == that.classLoader;
        }

        @Override
        public int hashCode() {
            return 31 * className.hashCode() + System.identityHashCode(classLoader);
        }
    }

    private static final class MethodKey {

        private final Class<?>   targetClass;

        private final String     methodName;

        private final Class<?>[] parameterTypes;

        private MethodKey(Class<?> targetClass, String methodName, Class<?>[] parameterTypes) {
            this.targetClass = targetClass;
            this.methodName = methodName;
            this.parameterTypes = parameterTypes;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof MethodKey that)) {
                return false;
            }
            return Objects.equals(targetClass, that.targetClass)
                   && Objects.equals(methodName, that.methodName)
                   && Arrays.equals(parameterTypes, that.parameterTypes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(targetClass, methodName, Arrays.hashCode(parameterTypes));
        }
    }

    private static final class ConstructorKey {

        private final Class<?>   targetClass;

        private final Class<?>[] parameterTypes;

        private ConstructorKey(Class<?> targetClass, Class<?>[] parameterTypes) {
            this.targetClass = targetClass;
            this.parameterTypes = parameterTypes;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (!(object instanceof ConstructorKey that)) {
                return false;
            }
            return Objects.equals(targetClass, that.targetClass)
                   && Arrays.equals(parameterTypes, that.parameterTypes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(targetClass, Arrays.hashCode(parameterTypes));
        }
    }
}

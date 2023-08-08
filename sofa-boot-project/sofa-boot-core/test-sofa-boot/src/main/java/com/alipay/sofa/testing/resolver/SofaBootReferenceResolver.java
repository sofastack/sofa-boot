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
package com.alipay.sofa.testing.resolver;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.param.ReferenceParam;
import com.alipay.sofa.runtime.service.binding.JvmServiceInvoker;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

/**
 * A helper class for resolving {@link SofaReference} objects
 *
 * @author pengym
 * @version SofaBootReferenceResolver.java, v 0.1 2023年08月07日 16:10 pengym
 */
public class SofaBootReferenceResolver {
    public static final Map<ApplicationContext, SofaBootReferenceResolver> RESOLVERS = new HashMap<>();

    private final ReferenceClient         referenceClient;
    private final Cache<CacheKey, Object> BEAN_CACHE = CacheBuilder.newBuilder()
            .maximumSize(100)
            .build();

    /**
     * Constructor
     *
     * @param clientFactory See Also: {@link ClientFactory}
     */
    public SofaBootReferenceResolver(ClientFactory clientFactory) {
        Preconditions.checkNotNull(clientFactory);
        this.referenceClient = clientFactory.getClient(ReferenceClient.class);
    }

    public static void init(TestContext testContext, ClientFactory clientFactory) {
        Preconditions.checkNotNull(testContext);
        Preconditions.checkNotNull(clientFactory, "ClientFactory is not initialized!");

        ApplicationContext context = testContext.getApplicationContext();
        RESOLVERS.computeIfAbsent(context, ctx -> new SofaBootReferenceResolver(clientFactory));
    }

    public static SofaBootReferenceResolver getInstance(@Nonnull TestContext testContext) {
        Preconditions.checkNotNull(testContext);

        ApplicationContext context = testContext.getApplicationContext();
        Preconditions.checkState(RESOLVERS.containsKey(context), new RuntimeException(
                String.format("Cannot find SofaBootReferenceResolver for ApplicationContext: %s", context)));
        return RESOLVERS.get(context);
    }

    /**
     * Retrieve a SOFA reference, this method provides internal caching mechanisms to avoid OOM
     *
     * @param interfaceType interfaceType
     * @param uniqueId      uniqueId
     * @param <T>           type parameter
     * @return SOFA reference
     */
    @SuppressWarnings("unchecked")
    public <T> T reference(@Nonnull Class<T> interfaceType, @Nullable String uniqueId) {
        Preconditions.checkNotNull(interfaceType);
        Preconditions.checkState(nonNull(referenceClient), new IllegalStateException(
                "SOFA ReferenceClient is not initialized! Please include package 'com.alipay.sofa.testing' via @ComponentScan in Spring "
                        + "test configuration."));

        CacheKey key = new CacheKey(interfaceType, uniqueId);
        try {
            return (T) BEAN_CACHE.get(key, () -> {
                ReferenceParam<T> param = new ReferenceParam<>();
                if (StringUtils.isNotBlank(uniqueId)) {
                    param.setUniqueId(uniqueId);
                }
                param.setInterfaceType(interfaceType);
                Object ref = referenceClient.reference(param);
                return extractProxiedObject(ref);
            });
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Retrieve the real implementation for a possible proxy object
     * ALERT: this is VERY hacky and relies on the internal implementation of SOFABoot
     *
     * @param obj object to be extract
     * @return Exact implementation
     */
    public static Object extractProxiedObject(Object obj) {
        if (!AopUtils.isAopProxy(obj) && !AopUtils.isJdkDynamicProxy(obj)) {
            return obj;
        }

        // AopProxy or JdkDynamicProxy
        Object result = AopProxyUtils.getSingletonTarget(obj);
        if (nonNull(result)) {
            return extractProxiedObject(result);
        }

        // JvmServiceInvoker
        try {
            Advised advised = (Advised) obj;
            Class<?> jvmServiceInvoker = JvmServiceInvoker.class;
            Object advice = jvmServiceInvoker.cast(advised.getAdvisors()[0].getAdvice());
            Method getTarget = jvmServiceInvoker.getDeclaredMethod("getTarget");
            getTarget.setAccessible(true);
            obj = getTarget.invoke(advice);

            // extract impl recursively
            return extractProxiedObject(obj);
        } catch (Throwable t) {
            throw new RuntimeException(String.format("Failed to extract implementation for SOFA reference: %s", obj), t);
        }
    }

    private static final class CacheKey {
        private final String   uniqueId;
        private final Class<?> clazz;

        public CacheKey(Class<?> clazz, String uniqueId) {
            this.uniqueId = uniqueId;
            this.clazz = clazz;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {return true;}
            if (o == null || getClass() != o.getClass()) {return false;}
            CacheKey cacheKey = (CacheKey) o;
            return new EqualsBuilder()
                    .append(uniqueId, cacheKey.uniqueId)
                    .append(clazz, cacheKey.clazz)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(uniqueId)
                    .append(clazz)
                    .toHashCode();
        }
    }
}
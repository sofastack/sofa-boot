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
package com.alipay.sofa.test.resolver;

import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A helper class for resolving beans
 *
 * @author pengym
 * @version SofaBootBeanResolver.java, v 0.1 2023年08月07日 16:58 pengym
 */
public class SofaBootBeanResolver {
    /**
     * During each execution of integration testing, multiple Spring {@link ApplicationContext}s may be generated (if cache is not hit),
     * thus requiring the storage of multiple resolvers instance.
     */
    private static final Map<ApplicationContext, SofaBootBeanResolver> RESOLVERS = Maps.newHashMap();

    /**
     * Mapping from SOFA module name to the associated {@link ApplicationContext}
     */
    private static final Map<String, ApplicationContext> SOFA_MODULE_CONTEXTS = Maps.newHashMap();

    /**
     * The root {@link ApplicationContext}
     */
    private final ApplicationContext rootApplicationContext;

    private SofaBootBeanResolver(@Nonnull TestContext testContext) {
        String name = ApplicationRuntimeModel.APPLICATION_RUNTIME_MODEL_NAME;

        this.rootApplicationContext = testContext.getApplicationContext();
        Preconditions.checkState(rootApplicationContext.containsBean(name), "ApplicationContext state is illegal!");
        ApplicationRuntimeModel applicationRuntimeModel = rootApplicationContext.getBean(name, ApplicationRuntimeModel.class);

        // iterate all installed SOFA modules and store moduleContext accordingly
        for (DeploymentDescriptor descriptor : applicationRuntimeModel.getResolvedDeployments()) {
            String moduleName = descriptor.getModuleName();
            ApplicationContext moduleContext = descriptor.getApplicationContext();
            SOFA_MODULE_CONTEXTS.put(moduleName, moduleContext);
        }
    }

    /**
     * Get the {@link SofaBootReferenceResolver} instance associated with the given `testContext`
     *
     * @param testContext the given {@link TestContext}
     * @return the SofaBootReferenceResolver
     */
    public static SofaBootBeanResolver getInstance(@Nonnull TestContext testContext) {
        Preconditions.checkNotNull(testContext);

        ApplicationContext context = testContext.getApplicationContext();
        return RESOLVERS.computeIfAbsent(context, ctx -> new SofaBootBeanResolver(testContext));
    }

    /**
     * Find the Spring beans corresponding to {@code type} and {@code qualifier}
     *
     * @param type      type of the Spring bean
     * @param context   Spring {@link ApplicationContext}
     * @param qualifier The qualifier of the Spring bean
     * @param <T>       type parameter
     * @return Spring beans, if found any
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> findBeans(@Nonnull Class<T> type, @Nullable String qualifier,
                                       @Nonnull ApplicationContext context) {
        Map<String, T> beansOfType = context.getBeansOfType(type);
        if (beansOfType.isEmpty()) {
            return new HashSet<>();
        }

        List<T> beansOrProxies;
        if (StringUtils.isNotBlank(qualifier)) {
            // match by type AND qualifier
            beansOrProxies = beansOfType.entrySet().stream()
                    .filter(entry -> entry.getKey().equalsIgnoreCase(qualifier))
                    .map(Entry::getValue)
                    .collect(Collectors.toList());
        } else {
            // match solely by qualifier
            beansOrProxies = new ArrayList<>(beansOfType.values());
        }

        return beansOrProxies.stream()
                .map(beanOrProxy -> AopUtils.isAopProxy(beanOrProxy)
                        ? (T) AopProxyUtils.getSingletonTarget(beanOrProxy)
                        : beanOrProxy)
                .collect(Collectors.toSet());
    }

    /**
     * Find the Spring bean corresponding to {@code type} and {@code qualifier} in a specific SOFA module
     *
     * @param moduleName SOFA module name
     * @param type       type of the Spring bean
     * @param qualifier  The qualifier of the Spring bean
     * @param <T>        type parameter
     * @return Spring beans, if found any
     */
    public <T> Set<T> findModuleBeans(@Nullable String moduleName, @Nonnull Class<T> type, @Nullable String qualifier) {
        Preconditions.checkNotNull(type);

        Set<T> beans = new HashSet<>();

        // If the moduleName is not provided, try to find in all modules
        if (StringUtils.isBlank(moduleName)) {

            // Root ApplicationContext
            beans.addAll(findBeans(type, qualifier, rootApplicationContext));

            // Find in all modules, CAN BE SLOW!
            for (ApplicationContext moduleContext : SOFA_MODULE_CONTEXTS.values()) {
                beans.addAll(findBeans(type, qualifier, moduleContext));
            }
        }
        // Otherwise find in a specific module
        else {
            ApplicationContext moduleContext = getModuleApplicationContext(moduleName);
            beans.addAll(findBeans(type, qualifier, moduleContext));
        }

        return beans;
    }

    private ApplicationContext getModuleApplicationContext(String moduleName) {
        // fetch root context
        if (StringUtils.isBlank(moduleName)) {
            Preconditions.checkNotNull(rootApplicationContext, "Root ApplicationContext is not initialized");
            return rootApplicationContext;
        }

        // fetch module context
        Preconditions.checkState(!SOFA_MODULE_CONTEXTS.isEmpty(), "SofaBootBeanResolver is not initialized");
        ApplicationContext moduleContext = SOFA_MODULE_CONTEXTS.get(moduleName);
        Preconditions.checkNotNull(moduleContext, String.format("Cannot find ApplicationContext for SOFA module %s", moduleName));
        return moduleContext;
    }
}
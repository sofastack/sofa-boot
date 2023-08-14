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
package com.alipay.sofa.test.model.stub;

import com.alipay.sofa.test.resolver.SofaBootBeanResolver;
import com.alipay.sofa.test.resolver.SofaBootReferenceResolver;
import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.test.context.TestContext;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author pengym
 * @version SofaStub.java, v 0.1 2023年08月07日 19:27 pengym
 */
@Data
@Builder
public class SofaStub implements Stub {
    private final Class<?> type;
    @Nullable
    private final String   moduleName;
    @Nullable
    private final String   uniqueId;
    @Nullable
    private final String   qualifier;

    @Override
    public Set<Object> resolveTargets(TestContext testContext) {
        Assert.notNull(type, "Stub target type is unknown");

        Set<Object> result = new LinkedHashSet<>();

        // 1. Only try to resolve SOFA reference
        boolean resolveRefOnly = StringUtils.isNotBlank(uniqueId);
        // 2. Only try to resolve Spring Beans
        boolean resolveBeanOnly = StringUtils.isNotBlank(moduleName) || StringUtils.isNotBlank(qualifier);

        if (!resolveRefOnly) {
            // Beans in TestContext
            result.addAll(SofaBootBeanResolver.findBeans(type, qualifier, testContext.getApplicationContext()));
            // Beans in SOFA module Context
            SofaBootBeanResolver resolver = SofaBootBeanResolver.getInstance(testContext);
            result.addAll(resolver.findModuleBeans(moduleName, type, qualifier));
        }

        if (!resolveBeanOnly) {
            init(testContext);
            // SOFA Reference
            SofaBootReferenceResolver resolver = SofaBootReferenceResolver.getInstance(testContext);
            result.add(resolver.reference(type, uniqueId));
        }

        // Filter null elements
        result = result.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Preconditions.checkState(result.size() > 0,
                String.format(
                        "Cannot find SOFA bean/reference with module=[%s], interfaceType=[%s], uniqueId=[%s], qualifier=[%s]",
                        moduleName, type, uniqueId, qualifier));

        return result;
    }

    private static AtomicBoolean initialized = new AtomicBoolean(false);
    private static void init(TestContext testContext) {
        if (initialized.getAndSet(true)) {
            return;
        }
        testContext.getApplicationContext().getBeansOfType(SofaBootBeanResolver.class);
    }
}
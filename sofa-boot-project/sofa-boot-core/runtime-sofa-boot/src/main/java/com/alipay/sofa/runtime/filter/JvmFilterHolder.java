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
package com.alipay.sofa.runtime.filter;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Container to hold jvm filter.
 *
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * @author huzijie
 */
public class JvmFilterHolder {

    private final List<JvmFilter> JVM_FILTERS    = new ArrayList<>();

    private final AtomicBoolean   FILTERS_SORTED = new AtomicBoolean(false);

    public void addJvmFilter(JvmFilter f) {
        JVM_FILTERS.add(f);
        FILTERS_SORTED.compareAndSet(true, false);
    }

    public void clearJvmFilters() {
        JVM_FILTERS.clear();
    }

    private void sortJvmFilters() {
        if (FILTERS_SORTED.compareAndSet(false, true)) {
            JVM_FILTERS.sort(AnnotationAwareOrderComparator.INSTANCE);
        }
    }

    public List<JvmFilter> getJvmFilters() {
        sortJvmFilters();
        return JVM_FILTERS;
    }

    public static boolean beforeInvoking(JvmFilterContext context) {
        List<JvmFilter> filters = Collections.unmodifiableList(context.getSofaRuntimeContext()
            .getJvmFilterHolder().getJvmFilters());
        for (JvmFilter filter : filters) {
            if (!filter.before(context)) {
                return false;
            }
        }
        return true;
    }

    public static boolean afterInvoking(JvmFilterContext context) {
        List<JvmFilter> filters = Collections.unmodifiableList(context.getSofaRuntimeContext()
            .getJvmFilterHolder().getJvmFilters());
        for (int i = filters.size() - 1; i >= 0; --i) {
            if (!filters.get(i).after(context)) {
                return false;
            }
        }
        return true;
    }
}

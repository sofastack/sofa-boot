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

import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/8/18
 */
public class JvmFilterHolder {
    private static final List<JvmFilter> JVM_FILTERS = new ArrayList<>();
    private static final AtomicBoolean filtersSorted  = new AtomicBoolean(false);

    private static final Comparator<Ordered> comparator = (f1, f2) -> {
        return Integer.compare(f1.getOrder(), f2.getOrder());
    };

    public static void addJvmFilter(JvmFilter f) {
        JVM_FILTERS.add(f);
        filtersSorted.compareAndSet(true, false);
    }

    public static void clearJvmFilters() {
        JVM_FILTERS.clear();
    }

    private static void sortJvmFilters() {
        if (filtersSorted.compareAndSet(false, true)) {
            JVM_FILTERS.sort(comparator);
        }
    }

    public static List<JvmFilter> getJvmFilters() {
        sortJvmFilters();
        return JVM_FILTERS;
    }

    public static boolean beforeInvoking(JvmFilterContext context) {
        sortJvmFilters();
        for (JvmFilter filter : JVM_FILTERS) {
            if (!filter.before(context)) {
                return false;
            }
        }
        return true;
    }

    public static boolean afterInvoking(JvmFilterContext context) {
        sortJvmFilters();
        for (JvmFilter filter : JVM_FILTERS) {
            if (!filter.after(context)) {
                return false;
            }
        }
        return true;
    }
}

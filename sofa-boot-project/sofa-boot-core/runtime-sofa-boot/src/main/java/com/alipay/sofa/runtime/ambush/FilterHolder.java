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
package com.alipay.sofa.runtime.ambush;

import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/8/18
 */
public class FilterHolder {
    private static final List<IngressFilter> ingressFilters = new ArrayList<>();
    private static final AtomicBoolean ingressSorted  = new AtomicBoolean(false);

    private static final List<EgressFilter> egressFilters = new ArrayList<>();
    private static final AtomicBoolean egressSorted = new AtomicBoolean(false);

    private static final Comparator<Ordered> comparator = (f1, f2) -> {
        return Integer.compare(f1.getOrder(), f2.getOrder());
    };

    public static void addIngressFilter(IngressFilter f) {
        ingressFilters.add(f);
        ingressSorted.compareAndSet(true, false);
    }

    private static void sortIngressFilters() {
        if (ingressSorted.compareAndSet(false, true)) {
            ingressFilters.sort(comparator);
        }
    }

    public static void addEgressFilter(EgressFilter f) {
        egressFilters.add(f);
        egressSorted.compareAndSet(true, false);
    }

    private static void sortEgressFilters() {
        if (egressSorted.compareAndSet(false, true)) {
            egressFilters.sort(comparator);
        }
    }

    public static Collection<IngressFilter> getIngressFilters() {
        sortIngressFilters();
        return ingressFilters;
    }

    public static Collection<EgressFilter> getEgressFilters() {
        sortEgressFilters();
        return egressFilters;
    }

    public static boolean beforeInvoking(Context context) {
        sortIngressFilters();
        for (IngressFilter filter : ingressFilters) {
            if (!filter.before(context)) {
                return false;
            }
        }
        return true;
    }

    public static boolean afterInvoking(Context context) {
        sortEgressFilters();
        for (EgressFilter filter : egressFilters) {
            if (!filter.after(context)) {
                return false;
            }
        }
        return true;
    }
}

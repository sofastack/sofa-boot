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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/8/18
 */
public class FilterHolder {
    private static final List<Filter>  filters = new ArrayList<>();
    private static final AtomicBoolean sorted  = new AtomicBoolean(false);

    public static void addFilter(Filter filter) {
        filters.add(filter);
        sorted.compareAndSet(true, false);
    }

    private static void sortFilters() {
        if (sorted.compareAndSet(false, true)) {
            filters.sort((o1, o2) -> {
                        if (o1.getOrder() > o2.getOrder()) {
                            return 1;
                        } else if (o1.getOrder() == o2.getOrder()) {
                            return 0;
                        }
                        return -1;
                    });
        }
    }

    public static Collection<Filter> getFilters() {
        sortFilters();
        return filters;
    }

    public static void beforeInvoking(Context context) {
        sortFilters();
        for (Filter filter : filters) {
            filter.before(context);
        }
    }

    public static void afterInvoking(Context context) {
        sortFilters();
        for (Filter filter : filters) {
            filter.after(context);
        }
    }
}

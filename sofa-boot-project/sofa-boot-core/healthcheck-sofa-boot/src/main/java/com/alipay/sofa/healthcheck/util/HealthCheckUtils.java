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
package com.alipay.sofa.healthcheck.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alipay.sofa.healthcheck.core.HealthCheckerComparatorProvider;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.OrderComparator;

/**
 * @author qilong.zql
 * @since 3.0.0
 */
public class HealthCheckUtils {
    public static <T, U> LinkedHashMap<T, U> sortMapAccordingToValue(Map<T, U> origin, BeanFactory beanFactory) {
        Comparator<Object> comparatorToUse = null;
        if (beanFactory instanceof DefaultListableBeanFactory) {
            comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
        }
        ObjectProvider<HealthCheckerComparatorProvider> objectProvider = beanFactory.getBeanProvider(HealthCheckerComparatorProvider.class);
        HealthCheckerComparatorProvider healthCheckerComparatorProvider = objectProvider.getIfUnique();
        if (healthCheckerComparatorProvider != null) {
            comparatorToUse = healthCheckerComparatorProvider.getComparator();
        }
        if (comparatorToUse == null) {
            comparatorToUse = OrderComparator.INSTANCE;
        }

        final Comparator<Object> finalComparator = comparatorToUse;
        List<Map.Entry<T, U>> entryList = new ArrayList<>(origin.entrySet());
        Collections.sort(entryList, (o1, o2)->finalComparator.compare(o1.getValue(), o2.getValue()));

        LinkedHashMap<T, U> result = new LinkedHashMap<>();
        for (Map.Entry<T, U> entry : entryList) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}

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
package com.alipay.sofa.boot.actuator.health;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Support for health check component comparator.
 *
 * @author qilong.zql
 * @author huzijie
 * @since 3.0.0
 */
public class HealthCheckComparatorSupport {

    public static Comparator<Object> getComparatorToUse(BeanFactory beanFactory) {
        Comparator<Object> comparatorToUse = null;
        if (beanFactory instanceof DefaultListableBeanFactory) {
            comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
        }
        if (beanFactory != null) {
            ObjectProvider<HealthCheckerComparatorProvider> objectProvider = beanFactory
                .getBeanProvider(HealthCheckerComparatorProvider.class);
            HealthCheckerComparatorProvider healthCheckerComparatorProvider = objectProvider
                .getIfUnique();
            if (healthCheckerComparatorProvider != null) {
                comparatorToUse = healthCheckerComparatorProvider.getComparator();
            }
        }
        if (comparatorToUse == null) {
            comparatorToUse = AnnotationAwareOrderComparator.INSTANCE;
        }
        return comparatorToUse;
    }

    public static <T, U> LinkedHashMap<T, U> sortMapAccordingToValue(Map<T, U> origin, Comparator<Object> comparatorToUse) {
        List<Map.Entry<T, U>> entryList = new ArrayList<>(origin.entrySet());
        entryList.sort((o1, o2) -> comparatorToUse.compare(o1.getValue(), o2.getValue()));

        LinkedHashMap<T, U> result = new LinkedHashMap<>();
        for (Map.Entry<T, U> entry : entryList) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}

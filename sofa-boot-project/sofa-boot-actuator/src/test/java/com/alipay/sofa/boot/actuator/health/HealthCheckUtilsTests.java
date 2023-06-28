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

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link HealthCheckComparatorSupport}.
 *
 * @author huzijie
 * @version HealthCheckUtilsTests.java, v 0.1 2022年07月05日 11:55 AM huzijie Exp $
 */
class HealthCheckUtilsTests {

    @Test
    public void sortMapAccordingToValue() {
        Map<String, OrderedBean> originMap = new LinkedHashMap<>();
        originMap.put("low", new OrderedBean(100));
        originMap.put("high", new OrderedBean(-100));
        assertThat(originMap.keySet().toArray()[0]).isEqualTo("low");
        assertThat(originMap.keySet().toArray()[1]).isEqualTo("high");
        originMap = HealthCheckComparatorSupport.sortMapAccordingToValue(originMap,
            AnnotationAwareOrderComparator.INSTANCE);
        assertThat(originMap.keySet().toArray()[0]).isEqualTo("high");
        assertThat(originMap.keySet().toArray()[1]).isEqualTo("low");
    }

    @Test
    public void getDefaultComparatorToUse() {
        assertThat(HealthCheckComparatorSupport.getComparatorToUse(null)).isEqualTo(
            AnnotationAwareOrderComparator.INSTANCE);
    }

    @Test
    public void getDependencyComparator() {
        DefaultListableBeanFactory beanFactory = Mockito.mock(DefaultListableBeanFactory.class);
        Comparator<Object> comparator = (o1, o2) -> 0;
        Mockito.doReturn(comparator).when(beanFactory).getDependencyComparator();
        Mockito.doReturn(new ObjectProvider<HealthCheckerComparatorProvider>() {
            @Override
            public HealthCheckerComparatorProvider getObject() throws BeansException {
                return null;
            }

            @Override
            public HealthCheckerComparatorProvider getObject(Object... args) throws BeansException {
                return null;
            }

            @Override
            public HealthCheckerComparatorProvider getIfAvailable() throws BeansException {
                return null;
            }

            @Override
            public HealthCheckerComparatorProvider getIfUnique() throws BeansException {
                return null;
            }
        }).when(beanFactory).getBeanProvider(HealthCheckerComparatorProvider.class);
        assertThat(HealthCheckComparatorSupport.getComparatorToUse(beanFactory)).isEqualTo(comparator);
    }

    @Test
    public void getCustomComparatorToUse() {
        DefaultListableBeanFactory beanFactory = Mockito.mock(DefaultListableBeanFactory.class);
        Comparator<Object> comparator = (o1, o2) -> 0;
        HealthCheckerComparatorProvider provider = () -> comparator;
        Mockito.doReturn(new ObjectProvider<HealthCheckerComparatorProvider>() {
            @Override
            public HealthCheckerComparatorProvider getObject() throws BeansException {
                return provider;
            }

            @Override
            public HealthCheckerComparatorProvider getObject(Object... args) throws BeansException {
                return provider;
            }

            @Override
            public HealthCheckerComparatorProvider getIfAvailable() throws BeansException {
                return provider;
            }

            @Override
            public HealthCheckerComparatorProvider getIfUnique() throws BeansException {
                return provider;
            }
        }).when(beanFactory).getBeanProvider(HealthCheckerComparatorProvider.class);

    }

    static class OrderedBean implements Ordered {

        private final int order;

        public OrderedBean(int order) {
            this.order = order;
        }

        @Override
        public int getOrder() {
            return this.order;
        }
    }
}

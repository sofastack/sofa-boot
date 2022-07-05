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
package com.alipay.sofa.healthcheck.test;

import com.alipay.sofa.healthcheck.core.HealthCheckerComparatorProvider;
import com.alipay.sofa.healthcheck.startup.ReadinessCheckCallback;
import com.alipay.sofa.healthcheck.test.bean.HighestOrderReadinessCheckCallback;
import com.alipay.sofa.healthcheck.test.bean.LowestOrderReadinessCheckCallback;
import com.alipay.sofa.healthcheck.util.HealthCheckUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author huzijie
 * @version HealthCheckUtilsTest.java, v 0.1 2022年07月05日 11:55 AM huzijie Exp $
 */
@RunWith(SpringRunner.class)
public class HealthCheckUtilsTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testCustomComparator() {
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
        Map<String, ReadinessCheckCallback> beansOfType = applicationContext
            .getBeansOfType(ReadinessCheckCallback.class);
        Map<String, ReadinessCheckCallback> orderedResult = HealthCheckUtils
            .sortMapAccordingToValue(beansOfType, beanFactory);
        List<String> healthCheckerId = new ArrayList<>(orderedResult.keySet());
        Assert.assertEquals("lowestOrderReadinessCheckCallback", healthCheckerId.get(0));
        Assert.assertEquals("highestOrderReadinessCheckCallback", healthCheckerId.get(1));
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class ReadinessCheckCallbackBreakTestConfiguration {
        @Bean
        public HealthCheckerComparatorProvider healthCheckerComparatorProvider() {
            return () -> (Comparator<Object>) (o1, o2) -> {
                if (o1 instanceof LowestOrderReadinessCheckCallback) {
                    return -1;
                } else {
                    return 1;
                }
            };
        }

        @Bean
        public HighestOrderReadinessCheckCallback highestOrderReadinessCheckCallback() {
            return new HighestOrderReadinessCheckCallback();
        }

        @Bean
        public LowestOrderReadinessCheckCallback lowestOrderReadinessCheckCallback() {
            return new LowestOrderReadinessCheckCallback();
        }
    }
}
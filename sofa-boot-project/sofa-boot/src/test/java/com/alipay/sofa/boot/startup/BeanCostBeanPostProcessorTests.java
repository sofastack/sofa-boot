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
package com.alipay.sofa.boot.startup;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link BeanCostBeanPostProcessor}.
 *
 * @author huzijie
 * @version BeanCostBeanPostProcessorTests.java, v 0.1 2023年02月01日 3:55 PM huzijie Exp $
 */
public class BeanCostBeanPostProcessorTests {

    @Test
    public void beanStat() throws InterruptedException {
        BeanCostBeanPostProcessor beanCostBeanPostProcessor = new BeanCostBeanPostProcessor();
        beanCostBeanPostProcessor.postProcessBeforeInitialization(new Object(), "testBean");
        Thread.sleep(10);
        beanCostBeanPostProcessor.postProcessAfterInitialization(new Object(), "testBean");

        List<BeanStat> beanStats = beanCostBeanPostProcessor.getBeanStatList();
        assertThat(beanStats).hasSize(1);
        BeanStat beanStat = beanStats.get(0);
        assertThat(beanStat.getName()).isEqualTo("testBean");
        assertThat(beanStat.getBeanClassName()).isEqualTo("java.lang.Object (testBean)");
        assertThat(beanStat.getCost() >= 10).isTrue();
    }
}

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
package com.alipay.sofa.runtime.proxy;

import com.alipay.sofa.runtime.sample.SampleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaProxyFactoryBean}.
 *
 * @author huzijie
 * @version SofaProxyFactoryBeanTests.java, v 0.1 2023年02月02日 12:10 PM huzijie Exp $
 */
public class SofaProxyFactoryBeanTests {

    @Test
    public void createSofaProxyFactoryBean() {
        BeanFactory beanFactory = new DefaultListableBeanFactory();
        Class<?>[] classes = new Class<?>[] { SampleService.class };
        SofaProxyFactoryBean factoryBean = new SofaProxyFactoryBean(classes, "test",
            SampleService.class, beanFactory);

        assertThat(factoryBean.getTargetClass()).isEqualTo(SampleService.class);
        assertThat(factoryBean.getProxiedInterfaces()).contains(SampleService.class);
    }
}

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
package com.alipay.sofa.boot.context;

import com.alipay.sofa.boot.startup.BeanStat;
import com.alipay.sofa.boot.startup.BeanStatCustomizer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaDefaultListableBeanFactory}.
 *
 * @author huzijie
 * @version SofaDefaultListableBeanFactoryTests.java, v 0.1 2023年02月01日 12:22 PM huzijie Exp $
 */
public class SofaDefaultListableBeanFactoryTests {

    @Test
    public void countBeanInitTime() {
        SofaDefaultListableBeanFactory beanFactory = new SofaDefaultListableBeanFactory();
        beanFactory.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        SofaGenericApplicationContext context = new SofaGenericApplicationContext(beanFactory);
        context.refresh();
        List<BeanStat> beanStats = beanFactory.getBeanStats();
        assertThat(beanStats.size() == 1).isTrue();
        BeanStat beanStat = beanStats.get(0);
        assertThat(beanStat.getBeanClassName()).isEqualTo(TestBean.class.getName());
        assertThat(beanStat.getInitMethodTime() >= 10).isTrue();
        assertThat(beanStat.getCost() >= beanStat.getInitMethodTime()).isTrue();
    }

    @Test
    public void countBeanClassCustomizer() {
        BeanStatCustomizer beanStatCustomizer = new TestBeanStatCustomizer();
        SofaDefaultListableBeanFactory beanFactory = new SofaDefaultListableBeanFactory();
        beanFactory.addBeanStatCustomizer(beanStatCustomizer);
        beanFactory.registerBeanDefinition("testBean", new RootBeanDefinition(TestBean.class));
        SofaGenericApplicationContext context = new SofaGenericApplicationContext(beanFactory);
        context.refresh();
        List<BeanStat> beanStats = beanFactory.getBeanStats();
        assertThat(beanStats.size() == 1).isTrue();
        BeanStat beanStat = beanStats.get(0);
        assertThat(beanStat.getName()).isEqualTo("test");
    }

    static class TestBean implements InitializingBean {

        @Override
        public void afterPropertiesSet() throws Exception {
            Thread.sleep(10);
        }
    }

    static class TestBeanStatCustomizer implements BeanStatCustomizer {

        @Override
        public BeanStat customize(String beanName, Object bean, BeanStat beanStat) {
            beanStat.setName("test");
            return beanStat;
        }
    }
}

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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaSpringContextSupport}.
 *
 * @author huzijie
 * @version SofaSpringContextSupportTests.java, v 0.1 2023年02月01日 12:22 PM huzijie Exp $
 */
public class SofaSpringContextSupportTests {

    private final ClassLoader classLoader = this.getClass().getClassLoader();

    @Test
    public void createBeanFactory() {
        SofaDefaultListableBeanFactory beanFactory =
                SofaSpringContextSupport.createBeanFactory(classLoader, SofaDefaultListableBeanFactory::new);
        assertThat(beanFactory).isNotNull();
        assertThat(beanFactory.getAutowireCandidateResolver()).isInstanceOf(QualifierAnnotationAutowireCandidateResolver.class);
        assertThat(beanFactory.getBeanClassLoader()).isEqualTo(classLoader);
        assertThat(beanFactory.getParameterNameDiscoverer()).isInstanceOf(DefaultParameterNameDiscoverer.class);
        assertThat(beanFactory.getPropertyEditorRegistrars().size() >= 1).isTrue();
    }

    @Test
    public void createApplicationContext() {
        SofaDefaultListableBeanFactory beanFactory =
                SofaSpringContextSupport.createBeanFactory(classLoader, SofaDefaultListableBeanFactory::new);
        SofaGenericApplicationContext applicationContext =
                SofaSpringContextSupport.createApplicationContext(beanFactory, SofaGenericApplicationContext::new);
        assertThat(applicationContext.getClassLoader()).isEqualTo(classLoader);
    }
}

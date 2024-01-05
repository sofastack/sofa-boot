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
package com.alipay.sofa.boot.aop.framework.autoproxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ExcludeBeanNameAutoProxyCreator}.
 *
 * @author huzijie
 * @version ExcludeBeanNameAutoProxyCreatorTests.java, v 0.1 2024年01月04日 4:36 PM huzijie Exp $
 */
public class ExcludeBeanNameAutoProxyCreatorTests {

    @Test
    public void excludeBeanNames() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
            ExcludeBeanNameAutoProxyCreatorTestConfiguration.class);
        SampleInterface sampleA = context.getBean("sampleA", SampleInterface.class);
        SampleInterface sampleB = context.getBean("sampleBeanB", SampleInterface.class);
        SampleInterface sampleC = context.getBean("sampleBeanC", SampleInterface.class);
        assertThat(sampleA.hello()).isEqualTo("hello");
        assertThat(sampleB.hello()).isEqualTo("aop");
        assertThat(sampleC.hello()).isEqualTo("hello");
    }

    @Configuration
    static class ExcludeBeanNameAutoProxyCreatorTestConfiguration {

        @Bean
        public SampleInterface sampleA() {
            return new SampleInterfaceImpl();
        }

        @Bean
        public SampleInterface sampleBeanB() {
            return new SampleInterfaceImpl();
        }

        @Bean
        public SampleInterface sampleBeanC() {
            return new SampleInterfaceImpl();
        }

        @Bean
        public ExcludeBeanNameAutoProxyCreator excludeBeanNameAutoProxyCreator() {
            ExcludeBeanNameAutoProxyCreator autoProxyCreator = new ExcludeBeanNameAutoProxyCreator();
            autoProxyCreator.setBeanNames("sampleBean*");
            autoProxyCreator.setExcludeBeanNames("sampleBeanC");
            autoProxyCreator.setInterceptorNames("sampleAdvisor");
            return autoProxyCreator;
        }

        @Bean
        public MethodInterceptor sampleAdvisor() {
            return new MethodInterceptor() {
                @Nullable
                @Override
                public Object invoke(@Nonnull MethodInvocation invocation) {
                    return "aop";
                }
            };
        }
    }

    interface SampleInterface {

        String hello();
    }

    static class SampleInterfaceImpl implements SampleInterface {

        @Override
        public String hello() {
            return "hello";
        }
    }
}

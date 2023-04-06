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
package com.alipay.sofa.boot.ark.invoke;

import com.alipay.sofa.boot.ark.sample.Pojo;
import com.alipay.sofa.boot.ark.sample.SampleService;
import com.alipay.sofa.boot.ark.sample.SampleServiceImpl;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.boot.test.context.FilteredClassLoader;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DynamicJvmServiceInvoker}.
 *
 * @author huzijie
 * @version DynamicJvmServiceInvokerTests.java, v 0.1 2023年04月06日 3:59 PM huzijie Exp $
 */
public class DynamicJvmServiceInvokerTests {

    private final ClassLoader       clientClassloader = new FilteredClassLoader("client");

    private final ClassLoader       serverClassloader = new FilteredClassLoader("server");

    private final ReferenceImpl     contract          = new ReferenceImpl("test",
                                                          SampleService.class, InterfaceMode.api,
                                                          true);

    private final SampleServiceImpl sampleServiceImpl = new SampleServiceImpl();

    @Test
    public void invokeNoSerialize() {
        DynamicJvmServiceInvoker dynamicJvmServiceInvoker = createDynamicJvmServiceInvoker(false);
        ProxyFactory factory = new ProxyFactory();
        factory.addInterface(SampleService.class);
        factory.addAdvice(dynamicJvmServiceInvoker);
        SampleService sampleService = (SampleService) factory.getProxy(this.getClass()
            .getClassLoader());

        Pojo pojo = new Pojo(1, "test");

        Pojo result = sampleService.transform(pojo);
        assertThat(pojo == result).isTrue();
    }

    @Test
    public void invokeWithSerialize() {
        DynamicJvmServiceInvoker dynamicJvmServiceInvoker = createDynamicJvmServiceInvoker(true);
        ProxyFactory factory = new ProxyFactory();
        factory.addInterface(SampleService.class);
        factory.addAdvice(dynamicJvmServiceInvoker);
        SampleService sampleService = (SampleService) factory.getProxy(this.getClass()
            .getClassLoader());

        Pojo pojo = new Pojo(1, "test");

        Pojo result = sampleService.transform(pojo);
        assertThat(pojo == result).isFalse();
        assertThat(pojo).isEqualTo(result);
    }

    @Test
    public void objectMethods() {
        DynamicJvmServiceInvoker dynamicJvmServiceInvoker = createDynamicJvmServiceInvoker(true);
        ProxyFactory factory = new ProxyFactory();
        factory.addInterface(SampleService.class);
        factory.addAdvice(dynamicJvmServiceInvoker);
        SampleService sampleService = (SampleService) factory.getProxy(this.getClass()
            .getClassLoader());

        assertThat(sampleService.toString()).isEqualTo(sampleServiceImpl.toString());

        // spring aop proxy handle equals and hashcode specific
        assertThat(sampleService.equals(sampleServiceImpl)).isFalse();
        assertThat(sampleService.hashCode()).isNotEqualTo(sampleServiceImpl.hashCode());
    }

    private DynamicJvmServiceInvoker createDynamicJvmServiceInvoker(boolean serialize) {
        return new DynamicJvmServiceInvoker(clientClassloader, serverClassloader,
            sampleServiceImpl, contract, "biz", serialize);
    }
}

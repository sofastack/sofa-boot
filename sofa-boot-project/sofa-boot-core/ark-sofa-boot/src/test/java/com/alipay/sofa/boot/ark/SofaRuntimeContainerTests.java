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
package com.alipay.sofa.boot.ark;

import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.context.support.GenericApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaRuntimeContainer}.
 *
 * @author huzijie
 * @version SofaRuntimeContainerTests.java, v 0.1 2023年04月06日 10:43 AM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class SofaRuntimeContainerTests {

    @Mock
    private SofaRuntimeManager        sofaRuntimeManagerA;
    @Mock
    private SofaRuntimeManager        sofaRuntimeManagerB;

    private ClassLoader               currentThreadClassLoader;

    private final FilteredClassLoader classLoaderA = new FilteredClassLoader("a");
    private final FilteredClassLoader classLoaderB = new FilteredClassLoader("b");

    @BeforeEach
    public void storeClassLoader() {
        currentThreadClassLoader = Thread.currentThread().getContextClassLoader();
    }

    @AfterEach
    public void reStoreClassLoader() {
        SofaRuntimeContainer.clear();
        Thread.currentThread().setContextClassLoader(currentThreadClassLoader);
    }

    @Test
    public void getManagerAndContextByClassLoader() {
        Thread.currentThread().setContextClassLoader(classLoaderA);
        SofaRuntimeContainer sofaRuntimeContainerA = new SofaRuntimeContainer(sofaRuntimeManagerA);
        GenericApplicationContext genericApplicationContextA = new GenericApplicationContext();
        sofaRuntimeContainerA.setApplicationContext(genericApplicationContextA);

        Thread.currentThread().setContextClassLoader(classLoaderB);
        SofaRuntimeContainer sofaRuntimeContainerB = new SofaRuntimeContainer(sofaRuntimeManagerB);
        GenericApplicationContext genericApplicationContextB = new GenericApplicationContext();
        sofaRuntimeContainerB.setApplicationContext(genericApplicationContextB);

        assertThat(SofaRuntimeContainer.getSofaRuntimeManager(classLoaderA)).isEqualTo(
            sofaRuntimeManagerA);
        assertThat(SofaRuntimeContainer.getSofaRuntimeManager(classLoaderB)).isEqualTo(
            sofaRuntimeManagerB);

        assertThat(SofaRuntimeContainer.getApplicationContext(classLoaderA)).isEqualTo(
            genericApplicationContextA);
        assertThat(SofaRuntimeContainer.getApplicationContext(classLoaderB)).isEqualTo(
            genericApplicationContextB);

        assertThat(SofaRuntimeContainer.sofaRuntimeManagerSet()).contains(sofaRuntimeManagerA);
        assertThat(SofaRuntimeContainer.sofaRuntimeManagerSet()).contains(sofaRuntimeManagerB);
    }

    @Test
    public void destroy() throws Exception {
        Thread.currentThread().setContextClassLoader(classLoaderA);
        SofaRuntimeContainer sofaRuntimeContainer = new SofaRuntimeContainer(sofaRuntimeManagerA);
        GenericApplicationContext genericApplicationContext = new GenericApplicationContext();
        sofaRuntimeContainer.setApplicationContext(genericApplicationContext);

        assertThat(SofaRuntimeContainer.getApplicationContext(classLoaderA)).isEqualTo(
            genericApplicationContext);
        sofaRuntimeContainer.destroy();
        assertThat(SofaRuntimeContainer.getApplicationContext(classLoaderA)).isNull();
    }

    @Test
    public void clear() {
        Thread.currentThread().setContextClassLoader(classLoaderA);
        SofaRuntimeContainer sofaRuntimeContainer = new SofaRuntimeContainer(sofaRuntimeManagerA);
        GenericApplicationContext genericApplicationContext = new GenericApplicationContext();
        sofaRuntimeContainer.setApplicationContext(genericApplicationContext);

        assertThat(SofaRuntimeContainer.getApplicationContext(classLoaderA)).isEqualTo(
            genericApplicationContext);
        SofaRuntimeContainer.clear();
        assertThat(SofaRuntimeContainer.getApplicationContext(classLoaderA)).isNull();
    }

    @Test
    public void updateProperties() {
        Thread.currentThread().setContextClassLoader(classLoaderA);
        SofaRuntimeContainer sofaRuntimeContainer = new SofaRuntimeContainer(sofaRuntimeManagerA);
        GenericApplicationContext genericApplicationContext = new GenericApplicationContext();
        sofaRuntimeContainer.setApplicationContext(genericApplicationContext);

        assertThat(SofaRuntimeContainer.isJvmInvokeSerialize(classLoaderA)).isTrue();
        assertThat(SofaRuntimeContainer.isJvmServiceCache(classLoaderA)).isFalse();

        sofaRuntimeContainer.setJvmInvokeSerialize(false);
        sofaRuntimeContainer.setJvmServiceCache(true);

        assertThat(SofaRuntimeContainer.isJvmInvokeSerialize(classLoaderA)).isFalse();
        assertThat(SofaRuntimeContainer.isJvmServiceCache(classLoaderA)).isTrue();
    }

    @Test
    public void updatePropertiesAfterStaticSet() {
        Thread.currentThread().setContextClassLoader(classLoaderA);
        SofaRuntimeContainer sofaRuntimeContainer = new SofaRuntimeContainer(sofaRuntimeManagerA);
        GenericApplicationContext genericApplicationContext = new GenericApplicationContext();
        sofaRuntimeContainer.setApplicationContext(genericApplicationContext);

        assertThat(SofaRuntimeContainer.isJvmInvokeSerialize(classLoaderA)).isTrue();
        assertThat(SofaRuntimeContainer.isJvmServiceCache(classLoaderA)).isFalse();

        SofaRuntimeContainer.updateJvmInvokeSerialize(classLoaderA, false);
        SofaRuntimeContainer.updateJvmServiceCache(classLoaderA, true);

        assertThat(SofaRuntimeContainer.isJvmInvokeSerialize(classLoaderA)).isFalse();
        assertThat(SofaRuntimeContainer.isJvmServiceCache(classLoaderA)).isTrue();

        sofaRuntimeContainer.setJvmInvokeSerialize(true);
        sofaRuntimeContainer.setJvmServiceCache(false);

        assertThat(SofaRuntimeContainer.isJvmInvokeSerialize(classLoaderA)).isFalse();
        assertThat(SofaRuntimeContainer.isJvmServiceCache(classLoaderA)).isTrue();
    }
}

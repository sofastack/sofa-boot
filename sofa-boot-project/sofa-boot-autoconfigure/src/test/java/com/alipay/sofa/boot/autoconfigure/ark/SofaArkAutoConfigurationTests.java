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
package com.alipay.sofa.boot.autoconfigure.ark;

import com.alipay.sofa.boot.ark.SofaRuntimeContainer;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.runtime.spi.service.DynamicServiceProxyManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaArkAutoConfiguration}.
 *
 * @author huzijie
 * @version SofaArkAutoConfigurationTests.java, v 0.1 2023年02月01日 5:10 PM huzijie Exp $
 */
public class SofaArkAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(SofaArkAutoConfiguration.class,
                                                                     SofaRuntimeAutoConfiguration.class));

    @Test
    public void registerArkBeans() {
        this.contextRunner
                .run((context) -> assertThat(context)
                        .hasSingleBean(SofaRuntimeContainer.class)
                        .hasSingleBean(DynamicServiceProxyManager.class));
    }

    @Test
    public void arkBeansWhenSofaRuntimeContainerClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(SofaRuntimeContainer.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(SofaRuntimeContainer.class)
                        .doesNotHaveBean(DynamicServiceProxyManager.class));
    }

    @Test
    void customSofaRuntimeContainer() {
        this.contextRunner
                .withPropertyValues("sofa.boot.ark.jvmServiceCache=true")
                .withPropertyValues("sofa.boot.ark.jvmInvokeSerialize=false")
                .run((context) -> {
                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                    assertThat(SofaRuntimeContainer.isJvmServiceCache(classLoader)).isTrue();
                    assertThat(SofaRuntimeContainer.isJvmInvokeSerialize(classLoader)).isFalse();
                });
    }
}

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
package com.alipay.sofa.boot.autoconfigure.reflection;

import com.alipay.sofa.boot.reflection.ReflectionCache;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ReflectionCacheAutoConfiguration}.
 *
 * @author xiaosiyuan
 * @since 4.5.0
 */
public class ReflectionCacheAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(ReflectionCacheAutoConfiguration.class));

    @Test
    void registerReflectionCacheByDefault() {
        this.contextRunner.run((context) -> {
            assertThat(context).hasSingleBean(ReflectionCache.class);
            assertThat(context.getBean(ReflectionCache.class).isEnabled()).isTrue();
        });
    }

    @Test
    void disabledPropertyShouldCreateDisabledReflectionCache() {
        this.contextRunner.withPropertyValues("sofa.boot.reflection.cache.enabled=false")
                .run((context) -> assertThat(context.getBean(ReflectionCache.class).isEnabled()).isFalse());
    }

    @Test
    void backOffWhenCustomReflectionCacheExists() {
        ReflectionCache reflectionCache = new ReflectionCache(false);
        this.contextRunner.withBean(ReflectionCache.class, () -> reflectionCache)
                .run((context) -> assertThat(context.getBean(ReflectionCache.class))
                    .isSameAs(reflectionCache));
    }
}

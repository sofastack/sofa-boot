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
package com.alipay.sofa.runtime.integration.base;

import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.runtime.integration.features.AwareTest;
import com.alipay.sofa.runtime.integration.invoke.DynamicJvmServiceProxyFinder;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qilong.zql
 * @since 2.3.1
 */
public abstract class AbstractTestBase {

    public AwareTest                          awareTest;

    public AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

    @Mocked
    public Biz                                biz;

    @Before
    public void before() {
        new MockUp<DynamicJvmServiceProxyFinder>() {
            @Mock
            public Biz getBiz(SofaRuntimeManager sofaRuntimeManager) {
                return biz;
            }
        };

        new NonStrictExpectations() {
            {
                biz.getIdentity();
                result = "MockName:MockVersion";
            }
        };

        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.application.name", "runtime-test");
        initApplicationContext(properties, IntegrationTestConfiguration.class);
        awareTest = applicationContext.getBean(AwareTest.class);
    }

    protected void initApplicationContext(Map<String, Object> properties,
                                          Class<?>... annotatedClasses) {
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            EnvironmentTestUtils.addEnvironment(this.applicationContext,
                buildProperty(property.getKey(), property.getValue()));
        }

        this.applicationContext.register(annotatedClasses);
        this.applicationContext.refresh();
    }

    private String buildProperty(String key, Object value) {
        return key + ":" + value;
    }

    @Configuration
    @EnableAutoConfiguration
    @ImportResource({ "classpath*:META-INF/spring/*.xml" })
    @ComponentScan({ "com.alipay.sofa.runtime.integration.features" })
    static class IntegrationTestConfiguration {
    }
}
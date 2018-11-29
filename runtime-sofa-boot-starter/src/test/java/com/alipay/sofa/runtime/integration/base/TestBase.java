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

import static org.mockito.Mockito.when;

import java.util.Map;

import org.mockito.Mockito;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.alipay.sofa.runtime.spring.listener.SofaRuntimeApplicationListener;

/**
 * @author qilong.zql
 * @since 3.1.0
 */
public abstract class TestBase {
    public AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

    protected void initApplicationContext(Map<String, Object> properties,
                                          Class<?>... annotatedClasses) {
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            EnvironmentTestUtils.addEnvironment(this.applicationContext,
                buildProperty(property.getKey(), property.getValue()));
        }

        ApplicationPreparedEvent applicationPreparedEvent = Mockito
            .mock(ApplicationPreparedEvent.class);
        when(applicationPreparedEvent.getApplicationContext()).thenReturn(applicationContext);
        new SofaRuntimeApplicationListener().onApplicationEvent(applicationPreparedEvent);

        this.applicationContext.register(annotatedClasses);
        this.applicationContext.refresh();
    }

    private String buildProperty(String key, Object value) {
        return key + ":" + value;
    }
}
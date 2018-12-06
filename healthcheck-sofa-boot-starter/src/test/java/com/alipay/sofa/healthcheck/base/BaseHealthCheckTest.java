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
package com.alipay.sofa.healthcheck.base;

import org.springframework.boot.actuate.autoconfigure.health.HealthEndpointAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

/**
 * init an annotation application context with properties and classes
 *
 * @author scienjus
 */
public class BaseHealthCheckTest {
    protected final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

    private static String buildProperty(String key, Object value) {
        return key + ":" + value;
    }

    protected void initApplicationContext(Map<String, Object> properties,
                                          Class<?>... annotatedClasses) {
        TestPropertyValues
                .of(properties.entrySet().stream()
                        .map(entry -> buildProperty(entry.getKey(), entry.getValue())))
                .applyTo(applicationContext);
        this.applicationContext.register(HealthEndpointAutoConfiguration.class);
        this.applicationContext.register(annotatedClasses);
        this.applicationContext.refresh();
    }
}

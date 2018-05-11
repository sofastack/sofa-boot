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
package com.alipay.sofa.healthcheck.util;

import com.alipay.sofa.healthcheck.core.HealthCheckManager;
import com.alipay.sofa.healthcheck.startup.StartUpHealthCheckStatus;
import org.junit.After;
import org.junit.Before;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

/**
 * init an annotation application context with properties and classes
 * @author scienjus
 */
public class BaseHealthCheckTest {
    protected final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

    @Before
    public void init() {
        HealthCheckManager.init(this.applicationContext);
    }

    @After
    public void closeContext() {
        HealthCheckManager.init(null);
        StartUpHealthCheckStatus.clean();
        this.applicationContext.close();
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

    private static String buildProperty(String key, Object value) {
        return key + ":" + value;
    }

}

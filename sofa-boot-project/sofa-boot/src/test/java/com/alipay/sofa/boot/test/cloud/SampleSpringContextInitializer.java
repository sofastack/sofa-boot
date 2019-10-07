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
package com.alipay.sofa.boot.test.cloud;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import com.alipay.sofa.boot.util.SofaBootEnvUtils;

/**
 * @author qilong.zql
 * @author 2.5.0
 */
public class SampleSpringContextInitializer
                                           implements
                                           ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static AtomicLong              bootstrapContext   = new AtomicLong(0L);

    public static AtomicLong              applicationContext = new AtomicLong(0L);

    public static ConfigurableEnvironment bootstrapEnvironment;

    public static ConfigurableEnvironment applicationEnvironment;

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        if (SofaBootEnvUtils.isSpringCloudBootstrapEnvironment(configurableApplicationContext
            .getEnvironment())) {
            bootstrapEnvironment = configurableApplicationContext.getEnvironment();
            bootstrapContext.incrementAndGet();
            return;
        }
        applicationEnvironment = configurableApplicationContext.getEnvironment();
        applicationContext.incrementAndGet();
    }
}
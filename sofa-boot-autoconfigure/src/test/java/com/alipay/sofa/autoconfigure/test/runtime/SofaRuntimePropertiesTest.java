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
package com.alipay.sofa.autoconfigure.test.runtime;

import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeConfigurationProperties;
import com.alipay.sofa.runtime.SofaRuntimeProperties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
@SpringBootTest(classes = EmptyConfiguration.class)
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "com.alipay.sofa.boot.disableJvmFirst=true",
                                  "com.alipay.sofa.boot.skipJvmReferenceHealthCheck=true" })
public class SofaRuntimePropertiesTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void testDisableJvmFirstProperty() {
        SofaRuntimeConfigurationProperties configurationProperties = ctx
            .getBean(SofaRuntimeConfigurationProperties.class);

        Assert.assertTrue(SofaRuntimeProperties.isDisableJvmFirst(ctx.getClassLoader()));
        Assert.assertTrue(configurationProperties.isDisableJvmFirst());
    }

    @Test
    public void testSkipJvmReferenceHealthCheckProperty() {
        SofaRuntimeConfigurationProperties configurationProperties = ctx
            .getBean(SofaRuntimeConfigurationProperties.class);

        Assert
            .assertTrue(SofaRuntimeProperties.isSkipJvmReferenceHealthCheck(ctx.getClassLoader()));
        Assert.assertTrue(configurationProperties.isSkipJvmReferenceHealthCheck());
    }
}
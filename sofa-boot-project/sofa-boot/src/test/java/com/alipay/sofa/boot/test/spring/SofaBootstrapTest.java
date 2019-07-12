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
package com.alipay.sofa.boot.test.spring;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.boot.constant.SofaBootConstants;

/**
 * Fix https://github.com/alipay/sofa-boot/issues/371
 *
 * @author qilong.zql
 * @since  2.5.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SofaBootstrapTest {

    @Autowired
    private Environment environment;

    @Test
    public void commandLineArgsTest() {
        Throwable throwable = null;
        try {
            SpringApplication springApplication = new SpringApplication(
                SofaBootstrapTestConfiguration.class);
            String[] args = { "-A=B" };
            springApplication.run(args);
        } catch (Throwable t) {
            throwable = t;
        }
        Assert.assertNull(throwable);
    }

    @Test
    public void environmentCustomizeTest() {
        MutablePropertySources propertySources = ((StandardEnvironment) environment)
            .getPropertySources();
        Assert.assertNotNull(propertySources.get(SofaBootConstants.SOFA_DEFAULT_PROPERTY_SOURCE));
    }

    @Configuration
    static class SofaBootstrapTestConfiguration {

    }
}
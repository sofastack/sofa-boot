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
package com.alipay.sofa.startup.test;

import com.alipay.sofa.startup.SofaStartupContext;
import com.alipay.sofa.startup.test.spring.SofaStartupAutoConfiguration;
import com.alipay.sofa.startup.webserver.StartupTomcatServletWebServerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: Zhijie
 * @since: 2020/7/13
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebServerCostTest {
    @Autowired
    private SofaStartupContext sofaStartupContext;

    @Test
    public void testWebServerCost() {
        Assert.assertTrue(sofaStartupContext.getWebServerInitCost() >= 0);
    }

    @SpringBootApplication
    @Import({ SofaStartupAutoConfiguration.class })
    static class SofaStartupContextWebServerCostTestConfiguration {
        @Bean
        public StartupTomcatServletWebServerFactory startupTomcatServletWebServerFactory() {
            return new StartupTomcatServletWebServerFactory();
        }
    }
}

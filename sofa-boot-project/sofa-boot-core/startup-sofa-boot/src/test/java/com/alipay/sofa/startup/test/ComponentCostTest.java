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
import com.alipay.sofa.startup.test.spring.RuntimeConfiguration;
import com.alipay.sofa.startup.test.spring.SofaStartupAutoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: Zhijie
 * @since: 2020/7/13
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RunWith(SpringRunner.class)
public class ComponentCostTest {
    @Autowired
    private SofaStartupContext sofaStartupContext;

    @Test
    public void testComponentCost() {
        Assert.assertTrue(sofaStartupContext.getComponentCost() >= 0);
        Assert.assertEquals(2, sofaStartupContext.getComponentDetail().size());
    }

    @Configuration
    @Import({ SofaStartupAutoConfiguration.class, RuntimeConfiguration.class })
    @ImportResource(locations = { "classpath*:META-INF/service/test-service.xml" })
    static class SofaStartupContextComponentCostTestConfiguration {
    }
}

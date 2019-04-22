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
package com.alipay.sofa.runtime.integration.component;

import com.alipay.sofa.runtime.beans.service.LifeCycleService;
import com.alipay.sofa.runtime.integration.bootstrap.SofaBootTestApplication;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author ruoshan
 * @since 2.6.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SofaBootTestApplication.class)
public class ComponentLifeCycleTest {

    @Autowired
    private LifeCycleService   lifeCycleService;

    @Autowired
    private SofaRuntimeContext sofaRuntimeContext;

    @Test
    public void testActivated() {
        Assert.assertTrue(lifeCycleService.isActivated());
    }

    @Test
    @DirtiesContext
    public void testDeactivated() {
        Assert.assertFalse(lifeCycleService.isDeactivated());
        sofaRuntimeContext.getComponentManager().shutdown();
        Assert.assertTrue(lifeCycleService.isDeactivated());
    }

}
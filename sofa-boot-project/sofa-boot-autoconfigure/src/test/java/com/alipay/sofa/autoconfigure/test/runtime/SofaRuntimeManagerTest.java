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

import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">guaner.zzx</a>
 * Created on 2020/1/18
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SofaRuntimeManagerTest {
    @Autowired
    private SofaRuntimeManager sofaRuntimeManager;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testSofaRuntimeManagerHasCtx() {
        Assert.assertNotNull(sofaRuntimeManager.getSofaRuntimeContext());
        System.out.println(applicationContext.getBean("sofaRuntimeContext"));
        Assert.assertNotNull(applicationContext.getBean("sofaRuntimeContext"));
    }

    @Configuration
    @EnableAutoConfiguration
    static class SofaRuntimeManagerTestConfiguration {
    }
}

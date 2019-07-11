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
package com.alipay.sofa.rpc.boot.test.config;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.rpc.boot.config.SofaBootRpcProperties;
import com.alipay.sofa.rpc.module.Module;
import com.alipay.sofa.rpc.module.ModuleFactory;

/**
 * @author khotyn
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = { SofaBootRpcProperties.PREFIX + ".defaultTracer=" })
public class DisableTracingTest {

    @Test
    public void testDisableTracing() throws NoSuchFieldException, IllegalAccessException {
        Field installedModulesField = ModuleFactory.class.getDeclaredField("INSTALLED_MODULES");
        installedModulesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, Module> modules = (ConcurrentHashMap<String, Module>) installedModulesField
            .get(ModuleFactory.class);
        Assert.assertNull(modules.get("sofaTracer"));
    }
}

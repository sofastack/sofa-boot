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
package com.alipay.sofa.runtime.component.impl;

import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author xuanbei 18/4/3
 */
public class ComponentManagerImplTest {
    private ComponentType type = new ComponentType("testType");
    private ComponentName name = new ComponentName(type, "object");

    @Test
    public void testRegister(@Mocked final ClientFactoryInternal mockClientFactoryInternal,
                             @Mocked final ComponentInfo mockComponentInfo) {
        new Expectations() {
            {
                mockComponentInfo.getName();
                result = name;
                mockComponentInfo.register();
                mockComponentInfo.getName();
                result = name;
                mockComponentInfo.getName();
                result = name;
                mockComponentInfo.resolve();
                result = true;
                mockComponentInfo.getName();
                result = name;
                mockComponentInfo.activate();
            }
        };
        ComponentManagerImpl componentManager = new ComponentManagerImpl(mockClientFactoryInternal);
        componentManager.register(mockComponentInfo);
        Assert.assertTrue(componentManager.registry.containsKey(name));
        Assert.assertTrue(componentManager.resolvedRegistry.get(type).containsValue(
            mockComponentInfo));
    }

}

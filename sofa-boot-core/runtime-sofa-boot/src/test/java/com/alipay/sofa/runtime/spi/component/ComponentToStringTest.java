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
package com.alipay.sofa.runtime.spi.component;

import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author xuanbei 18/5/18
 */
public class ComponentToStringTest {
    @Test
    public void test() throws Exception {

        ServiceImpl service = new ServiceImpl("uniqueId", ComponentToStringTest.class, new Object());
        Assert.assertEquals("com.alipay.sofa.runtime.spi.component.ComponentToStringTest:uniqueId",
            service.toString());

        service = new ServiceImpl("", ComponentToStringTest.class, new Object());
        Assert.assertEquals("com.alipay.sofa.runtime.spi.component.ComponentToStringTest",
            service.toString());

        ReferenceImpl reference = new ReferenceImpl("uniqueId", ComponentToStringTest.class,
            InterfaceMode.api, true);
        Assert.assertEquals("com.alipay.sofa.runtime.spi.component.ComponentToStringTest:uniqueId",
            reference.toString());

        reference = new ReferenceImpl(null, ComponentToStringTest.class, InterfaceMode.api, true);
        Assert.assertEquals("com.alipay.sofa.runtime.spi.component.ComponentToStringTest",
            reference.toString());
    }
}

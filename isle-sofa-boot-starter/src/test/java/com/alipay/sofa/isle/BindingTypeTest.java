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
package com.alipay.sofa.isle;

import com.alipay.sofa.runtime.api.binding.BindingType;
import org.junit.Assert;
import org.junit.Test;
import java.util.HashMap;

/**
 * @author xuanbei 18/5/15
 */
public class BindingTypeTest {
    @Test
    public void doTest() {
        Assert.assertTrue(new BindingType("jvm").equals(new BindingType("jvm")));
        Assert.assertFalse(new BindingType("jvm").equals(new BindingType("bolt")));
        Assert.assertFalse(new BindingType("jvm").equals(null));
        Assert.assertFalse(new BindingType("jvm").equals("jvm"));

        HashMap<BindingType, String> map = new HashMap<>();
        map.put(new BindingType("jvm"), "jvm");
        map.put(new BindingType("bolt"), "bolt");
        map.put(new BindingType("rest"), "rest");

        Assert.assertEquals(map.get(new BindingType("jvm")), "jvm");
        Assert.assertEquals(map.get(new BindingType("bolt")), "bolt");
        Assert.assertEquals(map.get(new BindingType("rest")), "rest");
        Assert.assertEquals(map.get("jvm"), null);
        Assert.assertEquals(map.get(null), null);
    }
}

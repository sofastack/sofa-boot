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
package com.alipay.sofa.rpc.boot.test;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author bystander
 * @version $Id: StringUtilTest.java, v 0.1 2018年10月23日 8:21 PM bystander Exp $
 */
public class StringUtilTest {

    @Test
    public void testSplit() {
        String key = "a";
        String[] result = key.split(",");
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.length);
    }

    @Test
    public void testSplit2() {
        String key = "a,b,c";
        String[] result = key.split(",");
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.length);
    }
}
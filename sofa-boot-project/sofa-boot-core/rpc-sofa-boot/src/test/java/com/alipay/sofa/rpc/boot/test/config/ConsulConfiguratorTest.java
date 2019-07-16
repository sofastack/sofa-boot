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

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.alipay.sofa.rpc.boot.config.ConsulConfigurator;

/**
 * @author <a href="mailto:zhiyuan.lzy@antfin.com">zhiyuan.lzy</a>
 */
public class ConsulConfiguratorTest {

    @Test
    public void test() {
        ConsulConfigurator consulConfigurator = new ConsulConfigurator();
        String config = "consul://127.0.0.1:8500?aaa=111&rrr=666";
        String address = consulConfigurator.parseAddress(config);

        Map<String, String> map = consulConfigurator.parseParam(config);
        Assert.assertEquals("111", map.get("aaa"));
        Assert.assertEquals("666", map.get("rrr"));
        Assert.assertEquals("127.0.0.1:8500", address);
    }
}
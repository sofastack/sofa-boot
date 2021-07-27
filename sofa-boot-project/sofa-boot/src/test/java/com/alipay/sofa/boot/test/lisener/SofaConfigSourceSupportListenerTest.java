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
package com.alipay.sofa.boot.test.lisener;

import com.alipay.sofa.common.config.ConfigKey;
import com.alipay.sofa.common.config.SofaConfigs;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author huzijie
 * @version SofaConfigSourceListener.java, v 0.1 2020年12月22日 7:53 下午 huzijie Exp $
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SofaConfigSourceSupportListenerTest {
    private final ConfigKey<String> key1 = ConfigKey.build("sofa.test.key1", "defaultValue1",
                                             false, "test key1", new String[] { "sofa_test_key1" });
    private final ConfigKey<String> key2 = ConfigKey.build("sofa.test.key2", "defaultValue2",
                                             false, "test key2", new String[] { "sofa_test_key2" });
    private final ConfigKey<String> key3 = ConfigKey.build("sofa.test.key3", "defaultValue3",
                                             false, "test key3", new String[] { "sofa_test_key3" });
    private final ConfigKey<String> key4 = ConfigKey.build("sofa.test.key4", "defaultValue4",
                                             false, "test key4", new String[] { "sofa_test_key4" });

    @Test
    public void test() {
        Assert.assertEquals("value1", SofaConfigs.getOrDefault(key1));
        Assert.assertEquals("value2", SofaConfigs.getOrDefault(key2));
        Assert.assertEquals("defaultValue3", SofaConfigs.getOrDefault(key3));
        System.setProperty("sofa.test.key4", "systemValue");
        Assert.assertEquals("systemValue", SofaConfigs.getOrDefault(key4));
    }

    @Configuration(proxyBeanMethods = false)
    static class SofaBootTestConfiguration {

    }
}

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
package com.alipay.sofa.smoke.tests.ark;

import com.alipay.sofa.ark.spi.service.ArkInject;
import com.alipay.sofa.ark.spi.service.biz.BizManagerService;
import com.alipay.sofa.ark.spi.service.event.EventAdminService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Ark environment base test.
 *
 * @author huzijie
 * @version ArkTestBase.java, v 0.1 2023年04月23日 4:16 PM huzijie Exp $
 */
@SpringBootTest(classes = ArkSofaBootApplication.class)
public class ArkTestBase {

    @ArkInject
    protected BizManagerService bizManagerService;

    @ArkInject
    protected EventAdminService adminService;

    @BeforeAll
    public static void init() {
        // if work in ide, open this code
        //        String classpath = System.getProperty("java.class.path");
        //        URL arkPluginUrl = ArkHandlerTests.class.getClassLoader().getResource(
        //            "sofa-boot-ark-plugin.jar");
        //        classpath += ":" + arkPluginUrl.getPath();
        //        System.setProperty("java.class.path", classpath);
        System.setProperty("sofa.ark.embed.enable", "true");
    }

    @AfterAll
    public static void clear() {
        System.clearProperty("sofa.ark.embed.enable");
    }
}

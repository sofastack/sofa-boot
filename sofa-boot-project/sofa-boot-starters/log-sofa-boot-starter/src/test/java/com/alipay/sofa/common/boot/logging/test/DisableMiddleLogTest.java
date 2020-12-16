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
package com.alipay.sofa.common.boot.logging.test;

import com.alipay.sofa.common.log.Constants;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/12/15
 */
public class DisableMiddleLogTest extends LogTestBase {
    @BeforeClass
    public static void before() {
        System.setProperty(Constants.SOFA_MIDDLEWARE_LOG_DISABLE_PROP_KEY, "true");
    }

    /**
     * test sofa.middleware.log.disable
     */
    @Test
    public void test() {
        SpringApplication springApplication = new SpringApplication(EmptyConfig.class);
        springApplication.run();
        logger = getLogger();
        logger.info("global space console");
        logger.debug("global space console debug");
        Assert.assertTrue(outContent.toString().contains("global space console"));
        Assert.assertFalse(outContent.toString().contains("global space console debug"));
    }
}

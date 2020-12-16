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
import org.junit.Test;
import org.springframework.boot.SpringApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/12/15
 */
public class GlobalConsoleLevelConfigTest extends LogTestBase {
    /**
     * test sofa.middleware.log.console.level config
     */
    @Test
    public void testGlobalConsoleLevelConfig() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH, "true");
        properties.put(Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_LEVEL, "debug");
        SpringApplication springApplication = new SpringApplication(EmptyConfig.class);
        springApplication.setDefaultProperties(properties);
        springApplication.run();
        logger = getLogger();
        logger.info("global space console");
        logger.debug("global space console debug");
        Assert.assertTrue(outContent.toString().contains("global space console"));
        Assert.assertTrue(outContent.toString().contains("global space console debug"));
    }
}

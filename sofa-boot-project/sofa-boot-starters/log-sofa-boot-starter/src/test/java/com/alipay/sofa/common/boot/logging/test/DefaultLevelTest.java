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
import com.alipay.sofa.common.utils.StringUtil;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/12/16
 */
public class DefaultLevelTest extends LogTestBase {
    @Test
    public void testDefaultLevel() throws IOException {
        SpringApplication springApplication = new SpringApplication(EmptyConfig.class);
        ConfigurableApplicationContext applicationContext = springApplication.run();
        Environment environment = applicationContext.getEnvironment();
        logger = getLogger();
        File logFile = getLogbackDefaultFile(environment);
        FileUtils.write(logFile, StringUtil.EMPTY_STRING,
            environment.getProperty(Constants.LOG_ENCODING_PROP_KEY));
        logger.info("info level");
        logger.debug("debug level");
        List<String> contents = FileUtils.readLines(logFile,
            environment.getProperty(Constants.LOG_ENCODING_PROP_KEY));
        Assert.assertEquals(1, contents.size());
        Assert.assertTrue(contents.get(0).contains("info level"));
    }
}

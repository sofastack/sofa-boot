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

import com.alipay.sofa.common.log.CommonLoggingConfigurations;
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.log.LoggerSpaceManager;
import com.alipay.sofa.common.utils.StringUtil;
import org.junit.Before;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/12/15
 */
public abstract class LogTestBase {
    protected static final String   TEST_SPACE  = "test.space";
    protected static final String   TEST_LOGGER = TEST_SPACE + ".logger";

    protected ByteArrayOutputStream outContent;

    protected Logger                logger;

    @Before
    public void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        CommonLoggingConfigurations.appendConsoleLoggerName(TEST_LOGGER);
    }

    protected Logger getLogger() {
        return LoggerSpaceManager.getLoggerBySpace(TEST_LOGGER, TEST_SPACE);
    }

    protected File getLogbackDefaultFile(Environment environment) {
        String loggingRoot = environment.getProperty(Constants.LOG_PATH_PREFIX + TEST_SPACE);
        if (StringUtil.isBlank(loggingRoot)) {
            loggingRoot = environment.getProperty(Constants.LOG_PATH);
        }
        return new File(loggingRoot + File.separator + "test-space" + File.separator
                        + "logback-common-default.log");
    }

    @EnableAutoConfiguration
    @Configuration(proxyBeanMethods = false)
    protected static class EmptyConfig {
    }
}

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;

import com.alipay.sofa.boot.logging.CommonLoggingApplicationListener;
import com.alipay.sofa.common.log.LoggerSpaceManager;
import com.alipay.sofa.common.log.MultiAppLoggerSpaceManager;
import com.alipay.sofa.common.log.SpaceId;

/**
 * @author qilong.zql
 * @since 1.0.19
 */
public abstract class BaseLogIntegrationTest {
    public static final String           TEST_SPACE  = "test.space";

    protected static Map<Object, Object> SPACES_MAP;
    protected static Logger              logger;
    protected ByteArrayOutputStream      outContent;
    protected ByteArrayOutputStream      errContent;
    protected final PrintStream          originalOut = System.out;
    protected final PrintStream          originalErr = System.err;

    static {
        try {
            Field spacesMapField = MultiAppLoggerSpaceManager.class.getDeclaredField("SPACES_MAP");
            spacesMapField.setAccessible(true);
            SPACES_MAP = (Map<Object, Object>) spacesMapField.get(MultiAppLoggerSpaceManager.class);
        } catch (Throwable throwable) {
            // ignore
        }
    }

    @Before
    public void setUpStreams() {
        logger = LoggerSpaceManager.getLoggerBySpace(
            LogbackIntegrationTest.class.getCanonicalName(), TEST_SPACE);
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() throws IOException {
        System.setOut(originalOut);
        System.setErr(originalErr);
        outContent.close();
        errContent.close();
        SPACES_MAP.remove(new SpaceId(TEST_SPACE));
        new CommonLoggingApplicationListener().setReInitialize(false);
    }
}
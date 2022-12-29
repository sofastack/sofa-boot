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
package com.alipay.sofa.boot.test.logging;

import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.utils.ReportUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author qilong.zql
 * @since 1.0.15
 */
public class LogbackIntegrationTest extends LogTestBase {
    /**
     * test sofa.middleware.log.internal.level config
     */
    @Test
    public void testInternalLogLevel() {
        ReportUtil.reportDebug("debug");
        Assert.assertFalse(outContent.toString().contains("debug"));
        System.setProperty(Constants.SOFA_MIDDLEWARE_LOG_INTERNAL_LEVEL, "debug");
        ReportUtil.reportDebug("debug");
        Assert.assertTrue(outContent.toString().contains("debug"));
    }
}

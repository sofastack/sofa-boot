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
package com.alipay.sofa.tracer.boot.flexible;

import com.alibaba.fastjson.JSONObject;
import com.alipay.common.tracer.core.configuration.SofaTracerConfiguration;
import com.alipay.sofa.tracer.boot.TestUtil;
import com.alipay.sofa.tracer.boot.base.AbstractTestBase;
import com.alipay.sofa.tracer.plugin.flexible.FlexibleLogEnum;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/8/3 10:59 AM
 * @since:
 **/
@ActiveProfiles("json")
public class FlexibleTracerTest extends AbstractTestBase {

    @Value("${spring.application.name}")
    private String appName;

    @Before
    public void before() {
        SofaTracerConfiguration.setProperty(SofaTracerConfiguration.STAT_LOG_INTERVAL, "1");
    }

    @After
    public void after() {
        SofaTracerConfiguration.setProperty(SofaTracerConfiguration.STAT_LOG_INTERVAL, "");
    }

    @Test
    public void testFlexibleTracer() throws Exception {
        assertNotNull(testRestTemplate);
        String testUrl1 = urlHttpPrefix + "/tracer";
        String testUrl2 = urlHttpPrefix + "/tracerException";
        ResponseEntity<String> response = testRestTemplate.getForEntity(testUrl1, String.class);
        String result = response.getBody();
        assertTrue(result.equalsIgnoreCase("tracer"));

        TestUtil.waitForAsyncLog();
        //wait for async output
        List<String> contents = FileUtils.readLines(customFileLog(FlexibleLogEnum.FLEXIBLE_DIGEST
            .getDefaultLogName()));
        assertTrue(contents.size() == 2);
        JSONObject item = JSONObject.parseObject(contents.get(0));
        assertEquals(appName, item.get("local.app"));

        ResponseEntity<String> responseException = testRestTemplate.getForEntity(testUrl2,
            String.class);

        String resultException = responseException.getBody();
        assertTrue(resultException.equalsIgnoreCase("exception"));

        TestUtil.waitForAsyncLog();
        //wait for async output
        List<String> contentsException = FileUtils
            .readLines(customFileLog(FlexibleLogEnum.FLEXIBLE_DIGEST.getDefaultLogName()));

        assertTrue(contentsException.size() == 3);
        JSONObject itemException = JSONObject.parseObject(contentsException.get(2));

        assertTrue(itemException.get("error").equals("testException"));
    }
}

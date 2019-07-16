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
package com.alipay.sofa.tracer.boot.springmvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.common.tracer.core.reporter.stat.manager.SofaTracerStatisticReporterCycleTimesManager;
import com.alipay.common.tracer.core.reporter.stat.manager.SofaTracerStatisticReporterManager;
import com.alipay.sofa.tracer.boot.TestUtil;
import com.alipay.sofa.tracer.boot.base.AbstractTestBase;
import com.alipay.sofa.tracer.boot.base.controller.SampleRestController;
import com.alipay.sofa.tracer.plugins.springmvc.SpringMvcLogEnum;

/**
 * SpringMvcFilterTest
 *
 * @author yangguanchao
 * @since 2018/05/01
 */
@ActiveProfiles("json")
public class SpringMvcFilterJsonOutputTest extends AbstractTestBase {

    @Test
    public void testSofaRestGet() throws Exception {
        String restUrl = urlHttpPrefix + "/greeting";
        int countTimes = 5;
        for (int i = 0; i < countTimes; i++) {
            ResponseEntity<SampleRestController.Greeting> response = testRestTemplate.getForEntity(
                restUrl, SampleRestController.Greeting.class);
            SampleRestController.Greeting greetingResponse = response.getBody();
            assertTrue(greetingResponse.isSuccess());
            // http://docs.spring.io/spring-boot/docs/1.4.2.RELEASE/reference/htmlsingle/#boot-features-testing
            assertTrue(greetingResponse.getId() >= 0);
        }

        TestUtil.waitForAsyncLog();

        //wait for async output
        List<String> contents = FileUtils
            .readLines(customFileLog(SpringMvcLogEnum.SPRING_MVC_DIGEST.getDefaultLogName()));
        assertTrue(contents.size() == countTimes);
        for (int i = 0; i < contents.size(); i++) {
            String logValue = contents.get(i);
            Map<String, String> jsonMap = parseToMap(logValue, String.class, String.class);
            assertTrue(jsonMap.size() > 0);
        }

        // wait another 500ms to reach a cycle of stat_internal_log
        Thread.sleep(500);

        for (int i = 0; i < countTimes; i++) {
            ResponseEntity<SampleRestController.Greeting> response = testRestTemplate.getForEntity(
                restUrl, SampleRestController.Greeting.class);
            SampleRestController.Greeting greetingResponse = response.getBody();
            assertTrue(greetingResponse.isSuccess());
        }
        SofaTracerStatisticReporterManager s = SofaTracerStatisticReporterCycleTimesManager
            .getSofaTracerStatisticReporterManager(1L);
        Assert.notNull(s.getStatReporters().get(
            SpringMvcLogEnum.SPRING_MVC_STAT.getDefaultLogName()));

        //stat log : 设置了周期 1s 输出一次
        Thread.sleep(2000);

        //wait for async output
        List<String> statContents = FileUtils
            .readLines(customFileLog(SpringMvcLogEnum.SPRING_MVC_STAT.getDefaultLogName()));
        assertEquals(2, statContents.size());
    }

    private static <K, V> Map<K, V> parseToMap(String json, Class<K> keyType, Class<V> valueType) {
        return JSON.parseObject(json, new TypeReference<Map<K, V>>(keyType, valueType) {
        });
    }
}

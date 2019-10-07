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
package com.alipay.sofa.tracer.boot.opentracing.profiles.tracer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.alipay.common.tracer.core.configuration.SofaTracerConfiguration;
import com.alipay.common.tracer.core.utils.TracerUtils;
import com.alipay.sofa.tracer.boot.base.AbstractTestBase;
import com.alipay.sofa.tracer.boot.base.controller.SampleRestController;
import com.alipay.sofa.tracer.boot.properties.SofaTracerProperties;
import com.alipay.sofa.tracer.plugins.springmvc.SpringMvcLogEnum;

/**
 * OpenTracingTest
 *
 * @author yangguanchao
 * @since 2018/05/08
 */
@ActiveProfiles("tracer")
public class OpenTracingTest extends AbstractTestBase {

    @Autowired
    private SofaTracerProperties sofaTracerProperties;

    @Test
    public void testTracerConfigBySofaTracerProperties() {
        String disableAll = sofaTracerProperties.getDisableDigestLog();
        assertTrue("false".equals(disableAll));

        Map<String, String> disableConfiguration = sofaTracerProperties.getDisableConfiguration();
        String logTypeDisable = disableConfiguration.get("logType");
        assertEquals("true", logTypeDisable);
        //tracer
        String disableAllConf = SofaTracerConfiguration
            .getProperty(SofaTracerConfiguration.DISABLE_MIDDLEWARE_DIGEST_LOG_KEY);
        assertTrue(Boolean.FALSE.toString().equalsIgnoreCase(disableAllConf));
        Map<String, String> disableConfigurationConf = SofaTracerConfiguration
            .getMapEmptyIfNull(SofaTracerConfiguration.DISABLE_DIGEST_LOG_KEY);
        assertTrue(Boolean.TRUE.toString()
            .equalsIgnoreCase(disableConfigurationConf.get("logType")));
        //
        String tracerGlobalRollingPolicy = sofaTracerProperties.getTracerGlobalRollingPolicy();
        assertEquals(".yyyy-MM-dd", tracerGlobalRollingPolicy);
        assertEquals(".yyyy-MM-dd",
            SofaTracerConfiguration.getRollingPolicy("spring_mvc_digest_rolling"));

        String tracerGlobalLogReserveDay = sofaTracerProperties.getTracerGlobalLogReserveDay();
        assertEquals("8", tracerGlobalLogReserveDay);
        assertEquals("8", SofaTracerConfiguration.getLogReserveConfig("component-digest.log"));

        String statLogInterval = sofaTracerProperties.getStatLogInterval();
        assertEquals("70", statLogInterval);
        assertEquals("70",
            SofaTracerConfiguration.getProperty(SofaTracerConfiguration.STAT_LOG_INTERVAL));

        String baggageMaxLength = sofaTracerProperties.getBaggageMaxLength();
        assertEquals("2048", baggageMaxLength);
        assertEquals(2048, TracerUtils.getBaggageMaxLength());
        assertEquals(2048, TracerUtils.getSysBaggageMaxLength());
    }

    @Test
    public void testNoDigestLog() {
        String restUrl = urlHttpPrefix + "/noDigestLog";
        ResponseEntity<SampleRestController.Greeting> response = testRestTemplate.getForEntity(
            restUrl, SampleRestController.Greeting.class);
        SampleRestController.Greeting greetingResponse = response.getBody();
        assertTrue(greetingResponse.isSuccess());
        // http://docs.spring.io/spring-boot/docs/1.4.2.RELEASE/reference/htmlsingle/#boot-features-testing
        assertTrue(greetingResponse.getId() >= 0);

        //wait for async output
        assertTrue(!customFileLog(SpringMvcLogEnum.SPRING_MVC_DIGEST.getDefaultLogName()).exists());
    }
}

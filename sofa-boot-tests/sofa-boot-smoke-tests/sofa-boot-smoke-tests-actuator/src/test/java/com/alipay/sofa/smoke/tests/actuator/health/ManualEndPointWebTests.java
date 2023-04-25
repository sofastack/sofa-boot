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
package com.alipay.sofa.smoke.tests.actuator.health;

import com.alipay.sofa.boot.actuator.health.ManualReadinessCallbackEndpoint;
import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import com.alipay.sofa.smoke.tests.actuator.ActuatorSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ManualReadinessCallbackEndpoint} web response.
 *
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/11/18
 */
@SpringBootTest(classes = ActuatorSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
                                                                                                                                       "management.endpoints.web.exposure.include=readiness,triggerReadinessCallback",
                                                                                                                                       "sofa.boot.actuator.health.manualReadinessCallback=true" })
public class ManualEndPointWebTests {

    @Autowired
    private TestRestTemplate       restTemplate;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ReadinessCheckListener readinessCheckListener;

    @Test
    public void trigger() {
        // 健康检查通过
        ResponseEntity<ReadinessCheckListener.ManualReadinessCallbackResult> response = restTemplate
            .getForEntity("/actuator/triggerReadinessCallback",
                ReadinessCheckListener.ManualReadinessCallbackResult.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        assertThat(response.getBody().getDetails()).contains("invoked successfully");

        // 重复触发
        response = restTemplate.getForEntity("/actuator/triggerReadinessCallback",
            ReadinessCheckListener.ManualReadinessCallbackResult.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getDetails()).contains("already triggered");

        // 健康检查失败
        Field field = ReflectionUtils
            .findField(ReadinessCheckListener.class, "healthCheckerStatus");
        assertThat(field).isNotNull();
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, readinessCheckListener, false);
        response = restTemplate.getForEntity("/actuator/triggerReadinessCallback",
            ReadinessCheckListener.ManualReadinessCallbackResult.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isFalse();
        assertThat(response.getBody().getDetails()).contains("indicator failed");
    }
}

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
package com.alipay.sofa.smoke.tests.actuator.rpc;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.smoke.tests.actuator.ActuatorSofaBootApplication;
import com.alipay.sofa.smoke.tests.actuator.sample.beans.DefaultSampleService;
import com.alipay.sofa.smoke.tests.actuator.sample.beans.SampleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link RpcEndpointWebTests} web response.
 *
 * @author huzijie
 * @version RpcEndpointWebTests.java, v 0.1 2023年04月24日 11:21 AM huzijie Exp $
 */
@SpringBootTest(classes = ActuatorSofaBootApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = { "management.endpoints.web.exposure.include=rpc" })
@Import(RpcEndpointWebTests.RpcConfigurations.class)
public class RpcEndpointWebTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void componentsActuator() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/rpc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
            .contains(
                "{\"interfaceId\":\"com.alipay.sofa.smoke.tests.actuator.sample.beans.SampleService\",\"uniqueId\":\"http\",\"protocols\":[\"http\"],\"registries\":[\"local\"],\"serialization\":\"hessian2\",\"register\":false,\"targetClassName\":\"com.alipay.sofa.smoke.tests.actuator.sample.beans.DefaultSampleService\",\"extraInfos\":{}}")
            .contains(
                "\"interfaceId\":\"com.alipay.sofa.smoke.tests.actuator.sample.beans.SampleService\",\"uniqueId\":\"\",\"protocols\":[\"bolt\"],\"registries\":[\"local\"],\"serialization\":\"hessian2\",\"register\":false,\"targetClassName\":\"com.alipay.sofa.smoke.tests.actuator.sample.beans.DefaultSampleService\",\"extraInfos\":{}")
            .contains(
                "{\"interfaceId\":\"com.alipay.sofa.smoke.tests.actuator.sample.beans.SampleService\",\"uniqueId\":\"consumer\",\"protocol\":\"bolt\",\"registries\":[\"local\"],\"serialization\":\"json\",\"invokeType\":\"sync\",\"subscribe\":true,\"timeout\":1500,\"retries\":5,\"extraInfos\":{}")
            .contains("{\"protocol\":\"local\",\"index\":\"\"}");
    }

    @Configuration
    static class RpcConfigurations {

        @SofaReference(uniqueId = "consumer", binding = @SofaReferenceBinding(bindingType = "bolt", timeout = 1500, retries = 5, serializeType = "json"))
        private SampleService sampleServiceConsumer;

        @Bean
        @SofaService(bindings = { @SofaServiceBinding(bindingType = "bolt", registry = "nacos") })
        public SampleService sampleServiceA() {
            return new DefaultSampleService();
        }

        @Bean
        @SofaService(uniqueId = "http", bindings = { @SofaServiceBinding(bindingType = "http") })
        public SampleService sampleServiceB() {
            return new DefaultSampleService();
        }
    }
}

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
package com.alipay.sofa.tracer.boot.springcloud;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alipay.sofa.tracer.boot.base.AbstractTestCloudBase;

@ActiveProfiles("feign")
@Import({ FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class })
@EnableFeignClients
public class FeignClientTest extends AbstractTestCloudBase {

    @FeignClient(value = "feign-client", url = "http://localhost:8085")
    public interface TestFeignClient {
        @RequestMapping("feign")
        String feign();
    }

    @Autowired
    private TestFeignClient testFeignClient;

    @Test
    public void getUser() throws IOException, InterruptedException {
        String feign = testFeignClient.feign();
        Assert.assertTrue(feign.equals("feign"));
    }
}
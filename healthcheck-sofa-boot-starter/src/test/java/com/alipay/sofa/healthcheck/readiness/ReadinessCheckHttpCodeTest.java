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
package com.alipay.sofa.healthcheck.readiness;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@SpringBootApplication
public class ReadinessCheckHttpCodeTest {

    @Test
    public void testReadinessCheckFailedHttpCode() throws IOException {
        HttpURLConnection huc = (HttpURLConnection) (new URL(
            "http://localhost:8080/health/readiness").openConnection());
        huc.setRequestMethod("HEAD");
        huc.connect();
        int respCode = huc.getResponseCode();
        System.out.println(huc.getResponseMessage());
        Assert.assertEquals(503, respCode);
    }

    public static void main(String[] args) {
        SpringApplication.run(ReadinessCheckHttpCodeTest.class, args);
    }
}

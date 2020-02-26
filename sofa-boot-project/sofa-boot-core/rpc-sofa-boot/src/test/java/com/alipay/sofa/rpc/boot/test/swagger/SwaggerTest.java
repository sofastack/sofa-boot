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
package com.alipay.sofa.rpc.boot.test.swagger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @author <a href=mailto:orezsilence@163.com>zhangchengxi</a>
 */
@SpringBootApplication
@SpringBootTest(properties = "com.alipay.sofa.rpc.enable-swagger=true")
@RunWith(SpringRunner.class)
@ImportResource("classpath*:spring/bolt_swagger.xml")
public class SwaggerTest {

    @Test
    public void testBoltSwagger() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpUriRequest request = new HttpGet("http://localhost:8341/swagger/bolt/api");
        HttpResponse response = httpClient.execute(request);
        Assert.assertEquals(200, response.getStatusLine().getStatusCode());
        Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains(
            "/com.alipay.sofa.rpc.boot.test.bean.invoke.HelloSyncService/saySync"));
    }
}

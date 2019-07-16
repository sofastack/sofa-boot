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
package com.alipay.sofa.tracer.examples.httpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alipay.sofa.tracer.examples.httpclient.instance.HttpAsyncClientInstance;
import com.alipay.sofa.tracer.examples.httpclient.instance.HttpClientInstance;

/**
 * HttpClientDemoApplication
 *
 * @author yangguanchao
 * @since 2018/09/27
 */
@SpringBootApplication
public class HttpClientDemoApplication {

    private static Logger logger = LoggerFactory.getLogger(HttpClientDemoApplication.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(HttpClientDemoApplication.class, args);
        HttpClientInstance httpClientInstance = new HttpClientInstance(10 * 1000);
        String httpGetUrl = "http://localhost:8080/httpclient";
        //sync
        String responseStr = httpClientInstance.executeGet(httpGetUrl);
        logger.info("Response is {}", responseStr);
        //async
        String asyncResponseStr = new HttpAsyncClientInstance().executeGet(httpGetUrl);
        logger.info("Async Response is {}", asyncResponseStr);
    }
}

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
package com.alipay.sofa.rpc.samples.invoke;

import com.alipay.sofa.rpc.api.future.SofaResponseFuture;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

/**
 * @author <a href="mailto:leizhiyuan@gmail.com">leizhiyuan</a>
 */
@ImportResource({ "classpath:invoke-client-example.xml" })
@SpringBootApplication
public class InvokeClientApplication {

    public static void main(String[] args) {

        //change port to run in local machine
        System.setProperty("server.port", "8081");

        SpringApplication springApplication = new SpringApplication(InvokeClientApplication.class);
        ApplicationContext applicationContext = springApplication.run(args);

        HelloSyncService helloSyncServiceReference = (HelloSyncService) applicationContext
            .getBean("helloSyncServiceReference");
        HelloFutureService helloFutureServiceReference = (HelloFutureService) applicationContext
            .getBean("helloFutureServiceReference");
        HelloCallbackService helloCallbackServiceReference = (HelloCallbackService) applicationContext
            .getBean("helloCallbackServiceReference");

        String result = helloSyncServiceReference.saySync("sync");
        System.out.println(result);

        helloFutureServiceReference.sayFuture("future");
        try {
            System.out.println(SofaResponseFuture.getResponse(1000, true));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        helloCallbackServiceReference.sayCallback("callback");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

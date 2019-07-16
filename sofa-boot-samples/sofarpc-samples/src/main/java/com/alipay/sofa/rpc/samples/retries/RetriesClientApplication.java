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
package com.alipay.sofa.rpc.samples.retries;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

/**
 * lazy invoke
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei.Liangen</a>
 */
@ImportResource({ "classpath:retries-client-example.xml" })
@SpringBootApplication
public class RetriesClientApplication {

    public static void main(String[] args) throws InterruptedException {

        //change port to run in local machine
        System.setProperty("server.port", "8081");

        SpringApplication springApplication = new SpringApplication(RetriesClientApplication.class);
        ApplicationContext applicationContext = springApplication.run(args);

        RetriesService retriesServiceReferenceBolt = (RetriesService) applicationContext
            .getBean("retriesServiceReferenceBolt");
        RetriesService retriesServiceReferenceDubbo = (RetriesService) applicationContext
            .getBean("retriesServiceReferenceDubbo");

        String resultBolt = retriesServiceReferenceBolt.sayRetry("retries_bolt");
        String resultDubbo = retriesServiceReferenceDubbo.sayRetry("retries_dubbo");

        System.out.println(resultBolt);
        System.out.println(resultDubbo);
        System.out.println(retriesServiceReferenceBolt.getCount());
        System.out.println(retriesServiceReferenceDubbo.getCount());

        if (resultBolt.equalsIgnoreCase("retries_bolt")) {
            System.out.println("retries invoke success");
        } else {
            System.out.println("retries invoke fail");
        }
    }
}
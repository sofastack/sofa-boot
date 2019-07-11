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
package com.alipay.sofa.rpc.samples.lazy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

/**
 * lazy invoke
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei.Liangen</a>
 * @version $Id: LazySample.java, v 0.1 2018年04月28日 上午1:51 LiWei.Liangen Exp $
 */
@ImportResource({ "classpath:lazy-client-example.xml" })
@SpringBootApplication
public class LazyClientApplication {

    public static void main(String[] args) {

        //change port to run in local machine
        System.setProperty("server.port", "8081");

        SpringApplication springApplication = new SpringApplication(LazyClientApplication.class);
        ApplicationContext applicationContext = springApplication.run(args);

        LazyService lazyServiceReferenceBolt = (LazyService) applicationContext.getBean("lazyServiceReferenceBolt");
        LazyService lazyServiceReferenceDubbo = (LazyService) applicationContext.getBean("lazyServiceReferenceDubbo");

        String resultBolt = lazyServiceReferenceBolt.sayLazy("lazy_bolt");
        String resultDubbo = lazyServiceReferenceDubbo.sayLazy("lazy_dubbo");

        System.out.println(resultBolt);
        System.out.println(resultDubbo);

        if (resultBolt.equalsIgnoreCase("lazy_bolt")) {
            System.out.println("lazy invoke success");
        } else {
            System.out.println("lazy invoke fail");
        }
    }
}
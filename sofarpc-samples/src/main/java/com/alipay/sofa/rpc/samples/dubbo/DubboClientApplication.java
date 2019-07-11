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
package com.alipay.sofa.rpc.samples.dubbo;

import com.alibaba.dubbo.rpc.service.GenericService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

/**
 * @author <a href="mailto:leizhiyuan@gmail.com">leizhiyuan</a>
 */
@ImportResource({ "classpath:dubbo-client-example.xml" })
@SpringBootApplication
public class DubboClientApplication {

    public static void main(String[] args) {

        //change port to run in local machine
        System.setProperty("server.port", "8081");

        SpringApplication springApplication = new SpringApplication(DubboClientApplication.class);
        ApplicationContext applicationContext = springApplication.run(args);

        DubboService directService = (DubboService) applicationContext.getBean("dubboServiceReference");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String result = directService.sayDubbo("dubbo");
        System.out.println("invoke result:" + result);
        if ("dubbo".equalsIgnoreCase(result)) {
            System.out.println("dubbo invoke success");
        } else {
            System.out.println("dubbo invoke fail");
        }

        GenericService genericService = (GenericService) applicationContext.getBean("dubboServiceReference1");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        result = (String) genericService.$invoke("sayDubbo", new String[] { "java.lang.String" },
            new Object[] { "dubbo" });
        System.out.println("invoke result:" + result);
        if ("dubbo".equalsIgnoreCase(result)) {
            System.out.println("dubbo invoke success");
        } else {
            System.out.println("dubbo invoke fail");
        }
    }
}

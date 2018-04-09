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
package com.alipay.sofa.boot.examples.demo.rpc;

import com.alipay.sofa.boot.examples.demo.rpc.bean.Person;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({ "classpath*:rpc-starter-example.xml" })
public class SofaBootRpcDemoApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(SofaBootRpcDemoApplication.class, args);

        Person personBolt = (Person) applicationContext.getBean("personBolt");
        Person personRest = (Person) applicationContext.getBean("personRest");
        Person personDubbo = (Person) applicationContext.getBean("personDubbo");

        System.out.println(personBolt.sayName("bolt"));
        System.out.println(personRest.sayName("rest"));
        System.out.println(personDubbo.sayName("dubbo"));
    }
}

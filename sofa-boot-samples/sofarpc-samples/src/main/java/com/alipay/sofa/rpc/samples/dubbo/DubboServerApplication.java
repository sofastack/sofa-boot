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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

/**
 * you should set a valid zk address first
 * @author <a href="mailto:leizhiyuan@gmail.com">leizhiyuan</a>
 */
@ImportResource({ "classpath:dubbo-server-example.xml" })
@SpringBootApplication
public class DubboServerApplication {

    public static void main(String[] args) {

        SpringApplication springApplication = new SpringApplication(DubboServerApplication.class);
        ApplicationContext applicationContext = springApplication.run(args);
    }
}

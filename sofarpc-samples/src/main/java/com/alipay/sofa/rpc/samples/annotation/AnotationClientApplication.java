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
package com.alipay.sofa.rpc.samples.annotation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @author <a href="mailto:leizhiyuan@gmail.com">leizhiyuan</a>
 */
@SpringBootApplication
public class AnotationClientApplication {

    public static void main(String[] args) {

        //change port to run in local machine
        System.setProperty("server.port", "8081");

        SpringApplication springApplication = new SpringApplication(AnotationClientApplication.class);

        ApplicationContext applicationContext = springApplication.run(args);

        AnnotationClientImpl annotationService = applicationContext.getBean(AnnotationClientImpl.class);

        String result = annotationService.sayClientAnnotation("annotation");
        System.out.println("invoke result:" + result);

        if ("annotation".equalsIgnoreCase(result)) {
            System.out.println("annotation invoke success");
        } else {
            System.out.println("annotation invoke fail");
        }
    }
}

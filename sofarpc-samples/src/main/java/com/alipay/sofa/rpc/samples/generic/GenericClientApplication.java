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
package com.alipay.sofa.rpc.samples.generic;

import com.alipay.hessian.generic.model.GenericObject;
import com.alipay.sofa.rpc.api.GenericService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

/**
 * @author <a href="mailto:leizhiyuan@gmail.com">leizhiyuan</a>
 */
@ImportResource({ "classpath:generic-client-example.xml" })
@SpringBootApplication
public class GenericClientApplication {

    public static void main(String[] args) {

        //change port to run in local machine
        System.setProperty("server.port", "8081");

        SpringApplication springApplication = new SpringApplication(GenericClientApplication.class);
        ApplicationContext applicationContext = springApplication.run(args);

        GenericService sampleGenericServiceReference = (GenericService) applicationContext
            .getBean("sampleGenericServiceReference");

        GenericObject genericObject = new GenericObject(
            "com.alipay.sofa.rpc.samples.generic.SampleGenericParamModel");
        genericObject.putField("name", "Bible");

        GenericObject genericResult = (GenericObject) sampleGenericServiceReference.$genericInvoke("sayGeneric",
            new String[] { "com.alipay.sofa.rpc.samples.generic.SampleGenericParamModel" },
            new Object[] { genericObject });

        System.out.println(genericResult.getType());
        System.out.println(genericResult.getField("name"));
        System.out.println(genericResult.getField("value"));

        String result = (String) genericResult.getField("value");

        System.out.println("invoke result:" + result);

        if ("sample generic value".equalsIgnoreCase(result)) {
            System.out.println("generic invoke success");
        } else {
            System.out.println("generic invoke fail");
        }

    }
}

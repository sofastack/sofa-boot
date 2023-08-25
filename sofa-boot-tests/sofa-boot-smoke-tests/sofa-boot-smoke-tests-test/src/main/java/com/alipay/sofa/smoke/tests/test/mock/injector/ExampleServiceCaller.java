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
package com.alipay.sofa.smoke.tests.test.mock.injector;

/**
 * @author huzijie
 * @version ExampleServiceCaller.java, v 0.1 2023年08月21日 3:16 PM huzijie Exp $
 */
public class ExampleServiceCaller implements ExampleServiceCallerInterface {

    private ExampleService service;

    public ExampleService getService() {
        return this.service;
    }

    public void setService(ExampleService service) {
        this.service = service;
    }

    @Override
    public String sayGreeting() {
        return this.service.greeting();
    }

    @Override
    public String sayHello() {
        return service.hello();
    }

}

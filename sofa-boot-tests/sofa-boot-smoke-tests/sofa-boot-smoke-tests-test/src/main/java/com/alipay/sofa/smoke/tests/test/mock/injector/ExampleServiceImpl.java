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

import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author pengym
 * @version ExampleServiceA.java, v 0.1 2023年08月08日 15:53 pengym
 */
@Service
public class ExampleServiceImpl implements ExampleService {

    private ExternalServiceClient        clientA;

    private AnotherExternalServiceClient clientB;

    @Override
    public String execute(String target, Object... args) {
        if (Objects.equals(target, "A")) {
            return clientA.invoke(args);
        } else if (Objects.equals(target, "B")) {
            return clientB.invoke(args);
        } else {
            throw new IllegalArgumentException(String.format("UNKNOWN target %s", target));
        }
    }

    @Override
    public Object getDependency(String name) {
        if (Objects.equals(name, "A")) {
            return clientA;
        } else if (Objects.equals(name, "B")) {
            return clientB;
        } else {
            return null;
        }
    }
}

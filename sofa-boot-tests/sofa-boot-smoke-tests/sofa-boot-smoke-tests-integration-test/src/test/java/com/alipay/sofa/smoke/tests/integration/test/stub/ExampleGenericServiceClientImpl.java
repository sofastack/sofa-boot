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
package com.alipay.sofa.smoke.tests.integration.test.stub;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.smoke.tests.integration.test.ExampleGenericService;
import com.alipay.sofa.smoke.tests.integration.test.GenericExternalServiceClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author pengym
 * @version ExampleGenericServiceClientImpl.java, v 0.1 2023年08月08日 20:03 pengym
 */
@SofaService
@Service
public class ExampleGenericServiceClientImpl implements ExampleGenericService {
    @Autowired
    private GenericExternalServiceClient<Integer> clientA;

    @Autowired
    private GenericExternalServiceClient<String> clientB;

    @Override
    public String execute(String target) {
        if (StringUtils.equals(target, "A")) {
            return clientA.invoke(1, 2, 3, 4);
        } else if (StringUtils.equals(target, "B")) {
            return clientB.invoke("1", "2", "3", "4");
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Object getDependency(String target) {
        if (StringUtils.equals(target, "A")) {
            return clientA;
        } else if (StringUtils.equals(target, "B")) {
            return clientB;
        } else {
            throw new IllegalArgumentException(String.format("Unknown target %s", target));
        }
    }
}
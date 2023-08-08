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
package com.alipay.sofa.smoke.tests.integration.test.base;

import com.alipay.sofa.smoke.tests.integration.test.GenericExternalServiceClient;
import com.alipay.sofa.smoke.tests.integration.test.base.SofaIntegrationTestBaseCase.BaseConfiguration;
import com.alipay.sofa.smoke.tests.integration.test.stub.GenericServiceClientImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author pengym
 * @version SofaIntegrationTestBaseCase.java, v 0.1 2023年08月08日 11:28 pengym
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SofaBootTestApplication.class)
@Import(BaseConfiguration.class)
public class SofaIntegrationTestBaseCase {
    @Autowired
    protected ApplicationContext applicationContext;

    @TestConfiguration
    public static class BaseConfiguration {
        @Bean("GenericExternalServiceClientA")
        GenericExternalServiceClient<Integer> integerGenericExternalServiceClient() {
            return new GenericServiceClientImpl<>();
        }

        @Bean("GenericExternalServiceClientB")
        GenericExternalServiceClient<String> stringGenericExternalServiceClient() {
            return new GenericServiceClientImpl<>();
        }
    }
}
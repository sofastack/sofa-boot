/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
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
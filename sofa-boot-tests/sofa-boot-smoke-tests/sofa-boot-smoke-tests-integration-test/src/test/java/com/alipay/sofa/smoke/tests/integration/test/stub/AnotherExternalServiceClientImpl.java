/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alipay.sofa.smoke.tests.integration.test.stub;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.smoke.tests.integration.test.AnotherExternalServiceClient;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

/**
 * @author pengym
 * @version ExternalServiceClientB.java, v 0.1 2023年08月08日 15:53 pengym
 */
@SofaService
@Service
public class AnotherExternalServiceClientImpl implements AnotherExternalServiceClient {
    @PostConstruct
    public void postConstruct() {
        System.out.println("ExternalServiceClientB has been constructed");
    }

    @Override
    public String invoke(Object... args) {
        return "B";
    }
}
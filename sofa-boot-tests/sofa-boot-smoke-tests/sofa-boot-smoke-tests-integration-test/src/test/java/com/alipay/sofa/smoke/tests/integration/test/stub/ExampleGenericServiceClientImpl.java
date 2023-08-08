/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
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
/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alipay.sofa.smoke.tests.integration.test.stub;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.smoke.tests.integration.test.AnotherExternalServiceClient;
import com.alipay.sofa.smoke.tests.integration.test.ExampleService;
import com.alipay.sofa.smoke.tests.integration.test.ExternalServiceClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author pengym
 * @version ExampleServiceA.java, v 0.1 2023年08月08日 15:53 pengym
 */
@SofaService
@Service
public class ExampleServiceImpl implements ExampleService {
    @SofaReference
    private ExternalServiceClient                 clientA;
    @SofaReference
    private AnotherExternalServiceClient          clientB;

    @Override
    public String execute(String target, Object... args) {
        if (StringUtils.equals(target, "A")) {
            return clientA.invoke(args);
        } else if (StringUtils.equals(target, "B")) {
            return clientB.invoke(args);
        } else {
            throw new IllegalArgumentException(String.format("UNKNOWN target %s", target));
        }
    }

    @Override
    public Object getDependency(String name) {
        if (StringUtils.equals(name, "A")) {
            return clientA;
        } else if (StringUtils.equals(name, "B")) {
            return clientB;
        } else {
            return null;
        }
    }
}
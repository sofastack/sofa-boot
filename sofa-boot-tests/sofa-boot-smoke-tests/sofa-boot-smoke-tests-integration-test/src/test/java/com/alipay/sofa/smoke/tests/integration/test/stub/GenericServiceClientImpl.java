/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alipay.sofa.smoke.tests.integration.test.stub;

import com.alipay.sofa.smoke.tests.integration.test.GenericExternalServiceClient;

/**
 * @author pengym
 * @version GenericServiceClientImpl.java, v 0.1 2023年08月08日 20:36 pengym
 */
public class GenericServiceClientImpl<T> implements GenericExternalServiceClient<T> {
    @Override
    public String invoke(T... input) {
        throw new UnsupportedOperationException();
    }
}
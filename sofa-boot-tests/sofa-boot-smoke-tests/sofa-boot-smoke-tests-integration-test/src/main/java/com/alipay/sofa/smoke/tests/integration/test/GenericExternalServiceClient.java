/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alipay.sofa.smoke.tests.integration.test;

/**
 * @author pengym
 * @version GenericExternalServiceClient.java, v 0.1 2023年08月08日 19:59 pengym
 */
public interface GenericExternalServiceClient<T> {
    String invoke(T... input);
}
/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alipay.sofa.smoke.tests.integration.test;

/**
 * @author pengym
 * @version ExternalServiceClient.java, v 0.1 2023年08月08日 15:29 pengym
 */
public interface ExternalServiceClient {
    String invoke(Object... args);
}
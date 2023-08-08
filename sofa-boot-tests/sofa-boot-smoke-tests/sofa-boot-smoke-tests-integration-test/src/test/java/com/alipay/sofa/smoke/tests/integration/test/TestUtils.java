/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alipay.sofa.smoke.tests.integration.test;

import org.mockito.MockingDetails;
import org.mockito.Mockito;

/**
 * @author pengym
 * @version TestUtils.java, v 0.1 2023年08月08日 15:57 pengym
 */
public class TestUtils {
    public static boolean isMock(Object object) {
        final MockingDetails mockingDetails = Mockito.mockingDetails(object);
        return mockingDetails.isMock() && !mockingDetails.isSpy();
    }

    public static boolean isSpy(Object object) {
        return Mockito.mockingDetails(object).isSpy();
    }
}
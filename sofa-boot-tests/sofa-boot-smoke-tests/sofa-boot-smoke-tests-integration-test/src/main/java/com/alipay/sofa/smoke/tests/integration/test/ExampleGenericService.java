/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alipay.sofa.smoke.tests.integration.test;

/**
 * @author pengym
 * @version ExampleGenericService.java, v 0.1 2023年08月08日 20:02 pengym
 */
public interface ExampleGenericService {
    String execute(String target);

    Object getDependency(String name);
}
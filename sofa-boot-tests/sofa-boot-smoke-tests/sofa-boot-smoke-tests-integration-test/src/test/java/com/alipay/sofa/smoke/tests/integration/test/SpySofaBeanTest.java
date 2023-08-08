/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alipay.sofa.smoke.tests.integration.test;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.smoke.tests.integration.test.base.SofaIntegrationTestBaseCase;
import com.alipay.sofa.testing.api.annotation.SofaSpyBeanFor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author pengym
 * @version SofaSpyBeanTest.java, v 0.1 2023年08月08日 16:09 pengym
 */
public class SpySofaBeanTest extends SofaIntegrationTestBaseCase {
    @SofaReference
    private ExampleService exampleService;

    @SofaReference
    private ExternalServiceClient clientA;

    @SofaSpyBeanFor(target = ExampleService.class)
    private AnotherExternalServiceClient clientB;

    @Test
    public void test_no_spy() {
        assertThat(exampleService).isNotNull();
        assertThatThrownBy(() -> exampleService.execute("A", new ArrayList<>()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void test_spy_external_service() {
        assertThat(clientB).isNotNull();
        assertThat(TestUtils.isSpy(clientB)).isTrue();

        assertThat(exampleService).isNotNull();

        String result = exampleService.execute("B", new ArrayList<>());
        assertThat(result).isEqualTo("B");

        // Change spy object's behavior
        String fakeResult = "FAKE MSG FROM SPY";
        when(clientB.invoke(any())).thenReturn(fakeResult);

        // The spy has been injected
        assertThat(exampleService.getDependency("B")).isEqualTo(clientB);

        // The behavior has been changed
        assertThat(clientB.invoke("ANYTHING")).isEqualTo(fakeResult);
        assertThat(exampleService.execute("B", new ArrayList<>())).isEqualTo(fakeResult);
    }
}
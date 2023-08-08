package com.alipay.sofa.smoke.tests.integration.test;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.smoke.tests.integration.test.base.SofaIntegrationTestBaseCase;
import com.alipay.sofa.testing.api.annotation.SofaMockBeanFor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

public class MockSofaBeanTest extends SofaIntegrationTestBaseCase {
    @SofaReference
    private ExampleService exampleService;

    @SofaMockBeanFor(target = ExampleService.class, field = "clientA")
    private ExternalServiceClient mockClient;

    @Test
    public void test_mock_external_service() {
        assertThat(mockClient).isNotNull();
        assertThat(TestUtils.isMock(mockClient)).isTrue();

        assertThat(exampleService).isNotNull();
        assertThat(exampleService.getDependency("A")).isEqualTo(mockClient);

        given(mockClient.invoke(any()))
                .willReturn("SUCCESS");

        String result = exampleService.execute("A", "HELLO WORLD");
        assertThat(result).isEqualTo("SUCCESS");
    }
}
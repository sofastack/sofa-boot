package com.alipay.sofa.boot.isle.deployment;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DeploymentDescriptorConfiguration}.
 *
 * @author huzijie
 * @version DeploymentDescriptorConfigurationTests.java, v 0.1 2023年04月06日 8:35 PM huzijie Exp $
 */
public class DeploymentDescriptorConfigurationTests {

    @Test
    public void getModuleNameIdentities() {
        List<String> expectedModuleNames = List.of("module1", "module2");
        List<String> requireModuleNames = List.of("module3", "module4");
        DeploymentDescriptorConfiguration config = new DeploymentDescriptorConfiguration(expectedModuleNames, requireModuleNames);
        assertThat(config.getModuleNameIdentities()).isEqualTo(expectedModuleNames);
    }

    @Test
    public void getRequireModuleIdentities() {
        List<String> moduleNames = List.of("module1", "module2");
        List<String> expectedRequireModuleNames = List.of("module3", "module4");
        DeploymentDescriptorConfiguration config = new DeploymentDescriptorConfiguration(moduleNames, expectedRequireModuleNames);
        assertThat(config.getRequireModuleIdentities()).isEqualTo(expectedRequireModuleNames);
    }
}

package com.alipay.sofa.boot.isle.deployment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link DefaultModuleDeploymentValidator}.
 *
 * @author huzijie
 * @version DefaultModuleDeploymentValidatorTests.java, v 0.1 2023年04月06日 8:47 PM huzijie Exp $
 */
public class DefaultModuleDeploymentValidatorTests {

    @Test
    public void isModuleDeploymentReturnsTrueForValidDeploymentDescriptor() {
        DeploymentDescriptor deploymentDescriptor = mock(DeploymentDescriptor.class);
        when(deploymentDescriptor.getModuleName()).thenReturn("sample-module");
        when(deploymentDescriptor.isSpringPowered()).thenReturn(true);

        DefaultModuleDeploymentValidator validator = new DefaultModuleDeploymentValidator();
        boolean result = validator.isModuleDeployment(deploymentDescriptor);

        assertThat(result).isTrue();
    }

    @Test
    public void isModuleDeploymentReturnsFalseForMissingModuleName() {
        DeploymentDescriptor deploymentDescriptor = mock(DeploymentDescriptor.class);
        when(deploymentDescriptor.getModuleName()).thenReturn("");
        when(deploymentDescriptor.isSpringPowered()).thenReturn(true);

        DefaultModuleDeploymentValidator validator = new DefaultModuleDeploymentValidator();
        boolean result = validator.isModuleDeployment(deploymentDescriptor);

        assertThat(result).isFalse();
    }

    @Test
    public void testIsModuleDeploymentReturnsFalseForNonSpringPoweredModule() {
        DeploymentDescriptor deploymentDescriptor = mock(DeploymentDescriptor.class);
        when(deploymentDescriptor.getModuleName()).thenReturn("sample-module");
        when(deploymentDescriptor.isSpringPowered()).thenReturn(false);

        DefaultModuleDeploymentValidator validator = new DefaultModuleDeploymentValidator();
        boolean result = validator.isModuleDeployment(deploymentDescriptor);

        assertThat(result).isFalse();
    }

}

package com.alipay.sofa.boot.isle.deployment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link DeploymentDescriptorFactory}.
 *
 * @author huzijie
 * @version DeploymentDescriptorFactoryTests.java, v 0.1 2023年04月06日 8:37 PM huzijie Exp $
 */
public class DeploymentDescriptorFactoryTests {

    private DeploymentDescriptorFactory factory;

    @BeforeEach
    public void setup() {
        factory = new DeploymentDescriptorFactory();
    }

    @Test
    public void buildCreatesJarDeploymentDescriptorForJarURL() throws Exception {
        URL url = new URL("jar:file:/path/to/sofa-module.jar!/META-INF/sofa/sofa-module.properties");
        Properties props = mock(Properties.class);
        String modulePropertyName = "sofa-module.properties";

        DeploymentDescriptor expectedDescriptor = mock(DeploymentDescriptor.class);
        when(factory.createJarDeploymentDescriptor(url, props, any(), any())).thenReturn(expectedDescriptor);

        DeploymentDescriptor actualDescriptor = factory.build(url, props, any(), any(), modulePropertyName);

        assertThat(actualDescriptor).isEqualTo(expectedDescriptor);
    }

    @Test
    public void buildCreatesFileDeploymentDescriptorForNonJarURL() throws Exception {
        URL url = new URL("file:/path/to/sofa-module.properties");
        Properties props = mock(Properties.class);
        DeploymentDescriptorConfiguration config = mock(DeploymentDescriptorConfiguration.class);
        ClassLoader classLoader = mock(ClassLoader.class);
        String modulePropertyName = "sofa-module.properties";

        DeploymentDescriptor expectedDescriptor = mock(DeploymentDescriptor.class);
        when(factory.createFileDeploymentDescriptor(url, props, config, classLoader, modulePropertyName)).thenReturn(expectedDescriptor);

        DeploymentDescriptor actualDescriptor = factory.build(url, props, config, classLoader, modulePropertyName);

        assertThat(actualDescriptor).isEqualTo(expectedDescriptor);
    }
}

package com.alipay.sofa.boot.actuator.startup;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link StartupEndPoint}.
 *
 * @author huzijie
 * @version StartupEndpointTests.java, v 0.1 2023年01月04日 11:51 AM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class StartupEndpointTests {

    @InjectMocks
    private StartupEndPoint startupEndPoint;

    @Mock
    private StartupReporter startupReporter;

    @Test
    public void testStartup() {
        StartupReporter.StartupStaticsModel staticsModel = new StartupReporter.StartupStaticsModel();
        staticsModel.setAppName("StartupEndpointTests");
        Mockito.doReturn(staticsModel).when(startupReporter).report();
        assertThat(startupEndPoint.startup().getAppName()).isEqualTo("StartupEndpointTests");
    }

    @Test
    public void startupForSpringBoot() {
        assertThatThrownBy(() -> startupEndPoint.startupForSpringBoot())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Please use GET method instead");
    }
}

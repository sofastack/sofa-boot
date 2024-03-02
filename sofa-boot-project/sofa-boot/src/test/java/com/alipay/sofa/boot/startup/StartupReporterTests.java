package com.alipay.sofa.boot.startup;

import com.alipay.sofa.boot.env.SofaBootEnvironmentPostProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link StartupReporter}.
 *
 * @author JPSINH27
 * @version StartupReporterTests.java, v 0.1 2024年01月03日 10:19 PM
 */
public class StartupReporterTests {

    @Mock
    ConfigurableApplicationContext mockContext;

    @Mock
    ConfigurableEnvironment mockEnvironment;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testApplicationBootFinish() {
        StartupReporter startupReporter = new StartupReporter();
        assertDoesNotThrow(startupReporter::applicationBootFinish);
    }

    @Test
    public void testAddCommonStartupStat() {
        StartupReporter startupReporter = new StartupReporter();
        BaseStat baseStat = new BaseStat();
        assertDoesNotThrow(() -> {
            startupReporter.addCommonStartupStat(baseStat);
        });
    }


    @Test
    public void testDrainStartupStaticsModel() {
        StartupReporter startupReporter = new StartupReporter();
        assertNotNull(startupReporter.drainStartupStaticsModel());
    }



}
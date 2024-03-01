package com.alipay.sofa.boot.startup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
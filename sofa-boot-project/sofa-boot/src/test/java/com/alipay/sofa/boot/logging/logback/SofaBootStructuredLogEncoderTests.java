/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.boot.logging.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

public class SofaBootStructuredLogEncoderTests {

    private static final String FORMAT_PROPERTY = "FILE_LOG_STRUCTURED_FORMAT";

    @AfterEach
    void clearStructuredFormatProperty() {
        System.clearProperty(FORMAT_PROPERTY);
    }

    @Test
    void usesPatternOutputWhenStructuredLoggingIsDisabled() {
        LoggerContext context = new LoggerContext();
        SofaBootStructuredLogEncoder encoder = createEncoder(context);

        encoder.start();

        assertThat(encoder.headerBytes()).isEmpty();
        assertThat(encoder.footerBytes()).isEmpty();
        assertThat(new String(encoder.encode(createEvent(context)), StandardCharsets.UTF_8))
            .isEqualTo("hello structured logging\n");

        encoder.stop();
        context.stop();
    }

    @Test
    void usesSpringBootStructuredEncoderWhenFormatPropertyIsSet() {
        System.setProperty(FORMAT_PROPERTY, "logstash");
        LoggerContext context = new LoggerContext();
        context.putObject(Environment.class.getName(),
            new MockEnvironment().withProperty("spring.application.name", "test-app"));
        SofaBootStructuredLogEncoder encoder = createEncoder(context);

        encoder.start();

        String output = new String(encoder.encode(createEvent(context)), StandardCharsets.UTF_8);
        assertThat(output).startsWith("{").contains("\"message\":\"hello structured logging\"")
            .contains("\"logger_name\":\"test.logger\"").contains("\"level\":\"INFO\"");

        encoder.stop();
        context.stop();
    }

    @Test
    void fallsBackToPatternOutputWhenStructuredEncoderCannotBeInitialized() {
        System.setProperty(FORMAT_PROPERTY, "logstash");
        LoggerContext context = new LoggerContext();
        SofaBootStructuredLogEncoder encoder = createEncoder(context);

        encoder.start();

        assertThat(new String(encoder.encode(createEvent(context)), StandardCharsets.UTF_8))
            .isEqualTo("hello structured logging\n");

        encoder.stop();
        context.stop();
    }

    @Test
    void requiresPatternBeforeStart() {
        SofaBootStructuredLogEncoder encoder = new SofaBootStructuredLogEncoder();
        encoder.setContext(new LoggerContext());
        encoder.setFormatProperty(FORMAT_PROPERTY);

        assertThatIllegalStateException().isThrownBy(encoder::start)
            .withMessage("Pattern has not been set");
    }

    private SofaBootStructuredLogEncoder createEncoder(LoggerContext context) {
        SofaBootStructuredLogEncoder encoder = new SofaBootStructuredLogEncoder();
        encoder.setContext(context);
        encoder.setPattern("%msg%n");
        encoder.setFormatProperty(FORMAT_PROPERTY);
        encoder.setCharset(StandardCharsets.UTF_8);
        return encoder;
    }

    private LoggingEvent createEvent() {
        return createEvent(new LoggerContext());
    }

    private LoggingEvent createEvent(LoggerContext context) {
        LoggingEvent event = new LoggingEvent();
        event.setLoggerContext(context);
        event.setLoggerName("test.logger");
        event.setLevel(Level.INFO);
        event.setMessage("hello structured logging");
        event.setThreadName("main");
        event.setTimeStamp(Instant.parse("2024-08-23T10:15:30Z").toEpochMilli());
        event.setMDCPropertyMap(Collections.emptyMap());
        return event;
    }
}

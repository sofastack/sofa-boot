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
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.alipay.common.tracer.core.SofaTracer;
import com.alipay.common.tracer.core.context.span.SofaTracerSpanContext;
import com.alipay.common.tracer.core.holder.SofaTraceContextHolder;
import com.alipay.common.tracer.core.span.SofaTracerSpan;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link SofaStructuredLogEncoder}.
 */
public class SofaStructuredLogEncoderTests {

    @AfterEach
    void clearTraceContext() {
        SofaTraceContextHolder.getSofaTraceContext().clear();
    }

    @Test
    void shouldFallbackToPatternWhenStructuredFormatIsNotConfigured() {
        LoggerContext context = new LoggerContext();
        SofaStructuredLogEncoder encoder = new SofaStructuredLogEncoder();
        encoder.setContext(context);
        encoder.setPattern("%msg%n");
        encoder.start();

        String output = new String(encoder.encode(createEvent(context, "plain-message")),
            StandardCharsets.UTF_8);

        assertThat(output).isEqualTo("plain-message" + System.lineSeparator());
    }

    @Test
    void shouldFallbackToPatternWhenFormatContainsPlaceholder() {
        LoggerContext context = new LoggerContext();
        SofaStructuredLogEncoder encoder = new SofaStructuredLogEncoder();
        encoder.setContext(context);
        encoder.setFormat("${FILE_LOG_STRUCTURED_FORMAT}");
        encoder.setPattern("%msg%n");
        encoder.start();

        String output = new String(encoder.encode(createEvent(context, "placeholder-message")),
            StandardCharsets.UTF_8);

        assertThat(output).isEqualTo("placeholder-message" + System.lineSeparator());
    }

    @Test
    void shouldFallbackToPatternWhenFormatIsUndefinedMarker() {
        LoggerContext context = new LoggerContext();
        SofaStructuredLogEncoder encoder = new SofaStructuredLogEncoder();
        encoder.setContext(context);
        encoder.setFormat("FILE_LOG_STRUCTURED_FORMAT_IS_UNDEFINED");
        encoder.setPattern("%msg%n");
        encoder.start();

        String output = new String(encoder.encode(createEvent(context, "undefined-message")),
            StandardCharsets.UTF_8);

        assertThat(output).isEqualTo("undefined-message" + System.lineSeparator());
    }

    @Test
    void shouldFailFastWhenPatternIsMissingForPatternFallback() {
        LoggerContext context = new LoggerContext();
        SofaStructuredLogEncoder encoder = new SofaStructuredLogEncoder();
        encoder.setContext(context);
        encoder.setFormat("${FILE_LOG_STRUCTURED_FORMAT}");

        assertThatIllegalStateException().isThrownBy(encoder::start)
            .withMessage("Pattern has not been set");
    }

    @Test
    void shouldOutputEcsStructuredLogWhenFormatConfigured() {
        LoggerContext context = new LoggerContext();
        context.putProperty("logging.structured.ecs.service.name", "structured-test");

        SofaStructuredLogEncoder encoder = new SofaStructuredLogEncoder();
        encoder.setContext(context);
        encoder.setFormat("ecs");
        encoder.setPattern("%msg%n");
        encoder.start();

        String output = new String(encoder.encode(createEvent(context, "structured-message")),
            StandardCharsets.UTF_8);

        assertThat(output).contains("\"message\":\"structured-message\"");
        assertThat(output).contains("\"service\":{\"name\":\"structured-test\"");
    }

    @Test
    void shouldOutputLogstashStructuredLogWhenFormatContainsWhitespace() {
        LoggerContext context = new LoggerContext();

        SofaStructuredLogEncoder encoder = new SofaStructuredLogEncoder();
        encoder.setContext(context);
        encoder.setCharset(null);
        encoder.setFormat(" logstash ");
        encoder.setPattern("%msg%n");
        encoder.start();

        String output = new String(encoder.encode(createEvent(context, "logstash-message")),
            StandardCharsets.UTF_8);

        assertThat(output).contains("\"@timestamp\":");
        assertThat(output).contains("\"message\":\"logstash-message\"");
    }

    @Test
    void shouldInjectTracerContextWhenStructuredTracerLoggingIsEnabled() {
        LoggerContext context = new LoggerContext();
        context.putProperty("logging.structured.ecs.service.name", "structured-test");
        context.putProperty("logging.sofa.tracer.output-structured", "true");

        SofaTracerSpanContext spanContext = new SofaTracerSpanContext("trace-1", "1.2", "1.1");
        SofaTracerSpan span = new SofaTracerSpan(Mockito.mock(SofaTracer.class),
            System.currentTimeMillis(), "testOperation", spanContext, Collections.emptyMap());
        SofaTraceContextHolder.getSofaTraceContext().push(span);

        SofaStructuredLogEncoder encoder = new SofaStructuredLogEncoder();
        encoder.setContext(context);
        encoder.setFormat("ecs");
        encoder.setPattern("%msg%n");
        encoder.start();

        String output = new String(encoder.encode(createEvent(context, "trace-message")),
            StandardCharsets.UTF_8);

        assertThat(output).contains("\"trace\":{\"id\":\"trace-1\"}");
        assertThat(output).contains("\"span\":{\"id\":\"1.2\"}");
        assertThat(output).contains("\"parent\":{\"id\":\"1.1\"}");
    }

    @Test
    void shouldInjectTracerContextWhenAliasPropertyIsEnabled() {
        LoggerContext context = new LoggerContext();
        context.putProperty("logging.structured.ecs.service.name", "structured-test");
        context.putProperty("sofa.boot.tracer.output-structured", "true");

        SofaTracerSpanContext spanContext = new SofaTracerSpanContext("trace-2", "2.2", "2.1");
        SofaTracerSpan span = new SofaTracerSpan(Mockito.mock(SofaTracer.class),
            System.currentTimeMillis(), "testOperation", spanContext, Collections.emptyMap());
        SofaTraceContextHolder.getSofaTraceContext().push(span);

        SofaStructuredLogEncoder encoder = new SofaStructuredLogEncoder();
        encoder.setContext(context);
        encoder.setFormat("ecs");
        encoder.setPattern("%msg%n");
        encoder.start();

        String output = new String(encoder.encode(createEvent(context, "alias-trace-message")),
            StandardCharsets.UTF_8);

        assertThat(output).contains("\"trace\":{\"id\":\"trace-2\"}");
        assertThat(output).contains("\"span\":{\"id\":\"2.2\"}");
        assertThat(output).contains("\"parent\":{\"id\":\"2.1\"}");
    }

    @Test
    void shouldSkipTracerFieldsWhenNoCurrentSpanExists() {
        LoggerContext context = new LoggerContext();
        context.putProperty("logging.structured.ecs.service.name", "structured-test");
        context.putProperty("logging.sofa.tracer.output-structured", "true");

        SofaStructuredLogEncoder encoder = new SofaStructuredLogEncoder();
        encoder.setContext(context);
        encoder.setFormat("ecs");
        encoder.setPattern("%msg%n");
        encoder.start();

        String output = new String(encoder.encode(createEvent(context, "no-trace-message")),
            StandardCharsets.UTF_8);

        assertThat(output).contains("\"message\":\"no-trace-message\"");
        assertThat(output).doesNotContain("\"trace\"");
        assertThat(output).doesNotContain("\"span\"");
        assertThat(output).doesNotContain("\"parent\"");
    }

    @Test
    void shouldReturnNullHeaderAndFooterAndStopCleanly() {
        LoggerContext context = new LoggerContext();
        SofaStructuredLogEncoder encoder = new SofaStructuredLogEncoder();
        encoder.setContext(context);
        encoder.setPattern("%msg%n");
        encoder.start();

        assertThat(encoder.headerBytes()).isNull();
        assertThat(encoder.footerBytes()).isNull();
        assertThat(encoder.isStarted()).isTrue();

        encoder.stop();

        assertThat(encoder.isStarted()).isFalse();
    }

    @Test
    void shouldExposeCurrentTracerSnapshot() {
        assertThat(SofaStructuredLogEncoder.SofaTracerContextAccessor.current()).isNull();

        SofaTracerSpanContext spanContext = new SofaTracerSpanContext("trace-3", "3.2", "3.1");
        SofaTracerSpan span = new SofaTracerSpan(Mockito.mock(SofaTracer.class),
            System.currentTimeMillis(), "testOperation", spanContext, Collections.emptyMap());
        SofaTraceContextHolder.getSofaTraceContext().push(span);

        SofaStructuredLogEncoder.SofaTracerContextSnapshot snapshot = SofaStructuredLogEncoder.SofaTracerContextAccessor
            .current();

        assertThat(snapshot).isNotNull();
        assertThat(snapshot.getTraceId()).isEqualTo("trace-3");
        assertThat(snapshot.getSpanId()).isEqualTo("3.2");
        assertThat(snapshot.getParentId()).isEqualTo("3.1");
    }

    @Test
    void shouldAllowSnapshotWithNullParentId() {
        SofaStructuredLogEncoder.SofaTracerContextSnapshot snapshot = new SofaStructuredLogEncoder.SofaTracerContextSnapshot(
            "trace-4", "4.2", null);

        assertThat(snapshot.getTraceId()).isEqualTo("trace-4");
        assertThat(snapshot.getSpanId()).isEqualTo("4.2");
        assertThat(snapshot.getParentId()).isNull();
    }

    private LoggingEvent createEvent(LoggerContext context, String message) {
        Logger logger = context.getLogger("structured.logger");
        LoggingEvent event = new LoggingEvent(Logger.class.getName(), logger, Level.INFO, message,
            null, null);
        event.setLoggerContext(context);
        event.setTimeStamp(System.currentTimeMillis());
        event.setMDCPropertyMap(Collections.emptyMap());
        return event;
    }
}

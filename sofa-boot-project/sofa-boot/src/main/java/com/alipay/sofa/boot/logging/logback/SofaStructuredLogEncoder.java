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

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.encoder.EncoderBase;
import org.springframework.boot.logging.StackTracePrinter;
import org.springframework.boot.logging.structured.CommonStructuredLogFormat;
import org.springframework.boot.logging.structured.ContextPairs;
import org.springframework.boot.logging.structured.StructuredLogFormatter;
import org.springframework.boot.logging.structured.StructuredLogFormatterFactory;
import org.springframework.boot.logging.structured.StructuredLoggingJsonMembersCustomizer;
import org.springframework.boot.util.Instantiator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Logback encoder that keeps the existing pattern output as the fallback behavior and
 * switches to Spring Boot's structured logging formatters when a structured format is
 * configured for SOFA logger spaces.
 *
 * @author OpenAI
 * @since 4.6.0
 */
public class SofaStructuredLogEncoder extends EncoderBase<ILoggingEvent> {

    private final ThrowableProxyConverter         throwableProxyConverter = new ThrowableProxyConverter();

    private Charset                               charset                 = StandardCharsets.UTF_8;

    private String                                format;

    private String                                pattern;

    private StructuredLogFormatter<ILoggingEvent> formatter;

    private PatternLayoutEncoder                  patternEncoder;

    public void setFormat(String format) {
        this.format = format;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void start() {
        this.throwableProxyConverter.setContext(getContext());
        this.throwableProxyConverter.start();

        String configuredFormat = resolveFormat(this.format);
        if (StringUtils.hasText(configuredFormat)) {
            this.formatter = createFormatter(configuredFormat);
        } else {
            this.patternEncoder = createPatternEncoder();
        }
        super.start();
    }

    @Override
    public void stop() {
        this.throwableProxyConverter.stop();
        if (this.patternEncoder != null) {
            this.patternEncoder.stop();
        }
        super.stop();
    }

    @Override
    public byte[] headerBytes() {
        return null;
    }

    @Override
    public byte[] encode(ILoggingEvent event) {
        Charset targetCharset = (this.charset != null) ? this.charset : StandardCharsets.UTF_8;
        if (this.formatter != null) {
            return this.formatter.formatAsBytes(event, targetCharset);
        }
        return this.patternEncoder.encode(event);
    }

    @Override
    public byte[] footerBytes() {
        return null;
    }

    private PatternLayoutEncoder createPatternEncoder() {
        Assert.state(StringUtils.hasText(this.pattern), "Pattern has not been set");
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(getContext());
        encoder.setPattern(this.pattern);
        encoder.setCharset(this.charset);
        encoder.start();
        return encoder;
    }

    private StructuredLogFormatter<ILoggingEvent> createFormatter(String configuredFormat) {
        Environment environment = createEnvironment();
        StructuredLogFormatterFactory<ILoggingEvent> factory = new StructuredLogFormatterFactory<>(
            ILoggingEvent.class, environment, this::addAvailableParameters,
            (commonFormats) -> addCommonFormatters(commonFormats, environment));
        return factory.get(configuredFormat);
    }

    private Environment createEnvironment() {
        StandardEnvironment environment = new StandardEnvironment();
        Map<String, Object> properties = new LinkedHashMap<>();
        Context context = getContext();
        if (context != null) {
            properties.putAll(context.getCopyOfPropertyMap());
        }
        if (StringUtils.hasText(resolveFormat(this.format))) {
            properties.putIfAbsent("logging.structured.format.file", resolveFormat(this.format));
        }
        environment.getPropertySources().addFirst(
            new MapPropertySource("sofa-structured-logging", properties));
        return environment;
    }

    private void addAvailableParameters(Instantiator.AvailableParameters parameters) {
        parameters.add(ThrowableProxyConverter.class, this.throwableProxyConverter);
    }

    private void addCommonFormatters(
                                   StructuredLogFormatterFactory.CommonFormatters<ILoggingEvent> commonFormats,
                                   Environment environment) {
        commonFormats.add(CommonStructuredLogFormat.ELASTIC_COMMON_SCHEMA,
            (instantiator) -> instantiateEcsFormatter(instantiator, environment));
        commonFormats.add(CommonStructuredLogFormat.LOGSTASH,
            (instantiator) -> instantiateLogstashFormatter(instantiator, environment));
    }

    @SuppressWarnings("unchecked")
    private StructuredLogFormatter<ILoggingEvent> instantiateEcsFormatter(Instantiator<?> instantiator,
                                                                          Environment environment) {
        try {
            Class<?> formatterClass = ClassUtils
                .forName(
                    "org.springframework.boot.logging.logback.ElasticCommonSchemaStructuredLogFormatter",
                    null);
            Constructor<?> constructor = formatterClass.getDeclaredConstructor(Environment.class,
                StackTracePrinter.class, ContextPairs.class, ThrowableProxyConverter.class,
                StructuredLoggingJsonMembersCustomizer.Builder.class);
            ReflectionUtils.makeAccessible(constructor);
            return (StructuredLogFormatter<ILoggingEvent>) constructor.newInstance(
                instantiator.getArg(Environment.class),
                instantiator.getArg(StackTracePrinter.class),
                instantiator.getArg(ContextPairs.class),
                instantiator.getArg(ThrowableProxyConverter.class),
                new CompositeJsonMembersCustomizerBuilder<>(instantiator
                    .getArg(StructuredLoggingJsonMembersCustomizer.Builder.class),
                    createTraceContextCustomizer(environment)));
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to create ECS structured log formatter", ex);
        }
    }

    @SuppressWarnings("unchecked")
    private StructuredLogFormatter<ILoggingEvent> instantiateLogstashFormatter(Instantiator<?> instantiator,
                                                                               Environment environment) {
        try {
            Class<?> formatterClass = ClassUtils.forName(
                "org.springframework.boot.logging.logback.LogstashStructuredLogFormatter", null);
            Constructor<?> constructor = formatterClass.getDeclaredConstructor(
                StackTracePrinter.class, ContextPairs.class, ThrowableProxyConverter.class,
                StructuredLoggingJsonMembersCustomizer.class);
            ReflectionUtils.makeAccessible(constructor);
            return (StructuredLogFormatter<ILoggingEvent>) constructor.newInstance(
                instantiator.getArg(StackTracePrinter.class),
                instantiator.getArg(ContextPairs.class),
                instantiator.getArg(ThrowableProxyConverter.class),
                new CompositeStructuredLoggingJsonMembersCustomizer<>(instantiator
                    .getArg(StructuredLoggingJsonMembersCustomizer.class),
                    createTraceContextCustomizer(environment)));
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Failed to create Logstash structured log formatter",
                ex);
        }
    }

    private StructuredLoggingJsonMembersCustomizer<ILoggingEvent> createTraceContextCustomizer(
                                                                                               Environment environment) {
        boolean tracerStructured = environment.getProperty("logging.sofa.tracer.output-structured",
            Boolean.class, environment.getProperty("sofa.boot.tracer.output-structured",
                Boolean.class, false));
        if (!tracerStructured) {
            return (members) -> {
            };
        }
        return (members) -> {
            members.add("trace", (event) -> SofaTracerContextAccessor.current()).whenNotNull()
                .usingMembers((trace) -> trace.add("id", SofaTracerContextSnapshot::getTraceId)
                    .whenHasLength());
            members.add("span", (event) -> SofaTracerContextAccessor.current()).whenNotNull()
                .usingMembers((span) -> span.add("id", SofaTracerContextSnapshot::getSpanId)
                    .whenHasLength());
            members.add("parent", (event) -> SofaTracerContextAccessor.current()).whenNotNull()
                .usingMembers((parent) -> parent.add("id",
                    SofaTracerContextSnapshot::getParentId).whenHasLength());
        };
    }

    private String resolveFormat(String candidate) {
        if (!StringUtils.hasText(candidate)) {
            return null;
        }
        if (candidate.contains("${") || candidate.endsWith("_IS_UNDEFINED")) {
            return null;
        }
        return candidate.trim();
    }

    private static final class CompositeStructuredLoggingJsonMembersCustomizer<T>
                                                                                  implements
                                                                                  StructuredLoggingJsonMembersCustomizer<T> {

        private final List<StructuredLoggingJsonMembersCustomizer<T>> delegates;

        @SafeVarargs
        private CompositeStructuredLoggingJsonMembersCustomizer(StructuredLoggingJsonMembersCustomizer<T>... delegates) {
            this.delegates = new ArrayList<>();
            for (StructuredLoggingJsonMembersCustomizer<T> delegate : delegates) {
                if (delegate != null) {
                    this.delegates.add(delegate);
                }
            }
        }

        @Override
        public void customize(org.springframework.boot.json.JsonWriter.Members<T> members) {
            for (StructuredLoggingJsonMembersCustomizer<T> delegate : this.delegates) {
                delegate.customize(members);
            }
        }
    }

    private static final class CompositeJsonMembersCustomizerBuilder<T>
                                                                        implements
                                                                        StructuredLoggingJsonMembersCustomizer.Builder<T> {

        private final StructuredLoggingJsonMembersCustomizer.Builder<T> delegate;

        private final StructuredLoggingJsonMembersCustomizer<T>         extraCustomizer;

        private CompositeJsonMembersCustomizerBuilder(StructuredLoggingJsonMembersCustomizer.Builder<T> delegate,
                                                      StructuredLoggingJsonMembersCustomizer<T> extraCustomizer) {
            this.delegate = delegate;
            this.extraCustomizer = extraCustomizer;
        }

        @Override
        public StructuredLoggingJsonMembersCustomizer.Builder<T> nested(boolean nested) {
            return new CompositeJsonMembersCustomizerBuilder<>(this.delegate.nested(nested),
                this.extraCustomizer);
        }

        @Override
        public StructuredLoggingJsonMembersCustomizer<T> build() {
            return new CompositeStructuredLoggingJsonMembersCustomizer<>(this.delegate.build(),
                this.extraCustomizer);
        }
    }

    static final class SofaTracerContextSnapshot {

        private final String traceId;

        private final String spanId;

        private final String parentId;

        SofaTracerContextSnapshot(String traceId, String spanId, String parentId) {
            this.traceId = traceId;
            this.spanId = spanId;
            this.parentId = parentId;
        }

        String getTraceId() {
            return this.traceId;
        }

        String getSpanId() {
            return this.spanId;
        }

        String getParentId() {
            return this.parentId;
        }
    }

    static final class SofaTracerContextAccessor {

        private static final Method HOLDER_METHOD;

        private static final Method GET_CURRENT_SPAN_METHOD;

        private static final Method GET_SPAN_CONTEXT_METHOD;

        private static final Method GET_TRACE_ID_METHOD;

        private static final Method GET_SPAN_ID_METHOD;

        private static final Method GET_PARENT_ID_METHOD;

        static {
            Method holderMethod = null;
            Method getCurrentSpanMethod = null;
            Method getSpanContextMethod = null;
            Method getTraceIdMethod = null;
            Method getSpanIdMethod = null;
            Method getParentIdMethod = null;
            try {
                Class<?> holderClass = ClassUtils.forName(
                    "com.alipay.common.tracer.core.holder.SofaTraceContextHolder", null);
                Class<?> traceContextClass = ClassUtils.forName(
                    "com.alipay.common.tracer.core.context.trace.SofaTraceContext", null);
                Class<?> spanClass = ClassUtils.forName(
                    "com.alipay.common.tracer.core.span.SofaTracerSpan", null);
                Class<?> spanContextClass = ClassUtils.forName(
                    "com.alipay.common.tracer.core.context.span.SofaTracerSpanContext", null);
                holderMethod = holderClass.getMethod("getSofaTraceContext");
                getCurrentSpanMethod = traceContextClass.getMethod("getCurrentSpan");
                getSpanContextMethod = spanClass.getMethod("getSofaTracerSpanContext");
                getTraceIdMethod = spanContextClass.getMethod("getTraceId");
                getSpanIdMethod = spanContextClass.getMethod("getSpanId");
                getParentIdMethod = spanContextClass.getMethod("getParentId");
            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                holderMethod = null;
                getCurrentSpanMethod = null;
                getSpanContextMethod = null;
                getTraceIdMethod = null;
                getSpanIdMethod = null;
                getParentIdMethod = null;
            }
            HOLDER_METHOD = holderMethod;
            GET_CURRENT_SPAN_METHOD = getCurrentSpanMethod;
            GET_SPAN_CONTEXT_METHOD = getSpanContextMethod;
            GET_TRACE_ID_METHOD = getTraceIdMethod;
            GET_SPAN_ID_METHOD = getSpanIdMethod;
            GET_PARENT_ID_METHOD = getParentIdMethod;
        }

        private SofaTracerContextAccessor() {
        }

        static SofaTracerContextSnapshot current() {
            if (HOLDER_METHOD == null) {
                return null;
            }
            try {
                Object traceContext = HOLDER_METHOD.invoke(null);
                Object span = GET_CURRENT_SPAN_METHOD.invoke(traceContext);
                if (span == null) {
                    return null;
                }
                Object spanContext = GET_SPAN_CONTEXT_METHOD.invoke(span);
                if (spanContext == null) {
                    return null;
                }
                return new SofaTracerContextSnapshot(
                    (String) GET_TRACE_ID_METHOD.invoke(spanContext),
                    (String) GET_SPAN_ID_METHOD.invoke(spanContext),
                    (String) GET_PARENT_ID_METHOD.invoke(spanContext));
            } catch (IllegalAccessException ex) {
                return null;
            } catch (InvocationTargetException ex) {
                if (ex.getTargetException() instanceof java.util.EmptyStackException) {
                    return null;
                }
                return null;
            }
        }
    }
}

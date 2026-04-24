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
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.EncoderBase;
import org.springframework.boot.logging.logback.StructuredLogEncoder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Logback encoder that keeps SOFABoot's pattern output by default and switches to
 * Spring Boot's structured logging encoder when a structured format system property is set.
 */
public class SofaBootStructuredLogEncoder extends EncoderBase<ILoggingEvent> {

    private String                 pattern;

    private Charset                charset = StandardCharsets.UTF_8;

    private String                 formatProperty;

    private Encoder<ILoggingEvent> delegate;

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setFormatProperty(String formatProperty) {
        this.formatProperty = formatProperty;
    }

    @Override
    public void start() {
        Assert.state(StringUtils.hasText(this.pattern), "Pattern has not been set");
        Assert.state(StringUtils.hasText(this.formatProperty), "Format property has not been set");
        this.delegate = createDelegate();
        super.start();
    }

    private Encoder<ILoggingEvent> createDelegate() {
        String format = System.getProperty(this.formatProperty);
        if (StringUtils.hasText(format)) {
            return createStructuredDelegate(format);
        }
        return createPatternDelegate();
    }

    private Encoder<ILoggingEvent> createStructuredDelegate(String format) {
        StructuredLogEncoder encoder = new StructuredLogEncoder();
        encoder.setContext(getContext());
        encoder.setCharset((this.charset != null) ? this.charset : StandardCharsets.UTF_8);
        encoder.setFormat(format);
        try {
            encoder.start();
            return encoder;
        } catch (RuntimeException exception) {
            addWarn(
                "Falling back to pattern logging because structured logging could not be initialized for "
                        + this.formatProperty, exception);
            encoder.stop();
            return createPatternDelegate();
        }
    }

    private Encoder<ILoggingEvent> createPatternDelegate() {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(getContext());
        encoder.setPattern(this.pattern);
        encoder.setCharset((this.charset != null) ? this.charset : StandardCharsets.UTF_8);
        encoder.start();
        return encoder;
    }

    @Override
    public void stop() {
        if (this.delegate != null) {
            this.delegate.stop();
        }
        super.stop();
    }

    @Override
    public byte[] headerBytes() {
        return (this.delegate != null) ? this.delegate.headerBytes() : null;
    }

    @Override
    public byte[] encode(ILoggingEvent event) {
        Assert.state(this.delegate != null, "Encoder has not been started");
        return this.delegate.encode(event);
    }

    @Override
    public byte[] footerBytes() {
        return (this.delegate != null) ? this.delegate.footerBytes() : null;
    }
}

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
package com.alipay.sofa.boot.autoconfigure.tracer;

import com.alipay.common.tracer.core.listener.SpanReportListener;
import com.alipay.common.tracer.core.listener.SpanReportListenerHolder;
import com.alipay.common.tracer.core.span.SofaTracerSpan;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaTracerAutoConfiguration}.
 *
 * @author huzijie
 * @version SofaTracerAutoConfigurationTests.java, v 0.1 2023年04月04日 10:14 AM huzijie Exp $
 */
public class SofaTracerAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(SofaTracerAutoConfiguration.class));

    @Test
    public void hasSpanReportListenerHolder() {
        this.contextRunner
                .withBean(SampleSpanReportListener.class)
                .run((context) -> assertThat(context)
                        .hasSingleBean(SpanReportListenerHolder.class));
    }

    @Test
    public void noSpanReportListenerHolderWhenSpanReportListenerHolderClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(SpanReportListenerHolder.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(SpanReportListenerHolder.class));
    }

    @Test
    public void withoutSpanReportListenerHolder() {
        this.contextRunner
                .run((context) -> assertThat(context.getBean("sofaTracerSpanReportListener").toString()).isEqualTo("null"));
    }

    static class SampleSpanReportListener implements SpanReportListener {

        @Override
        public void onSpanReport(SofaTracerSpan sofaTracerSpan) {

        }
    }
}

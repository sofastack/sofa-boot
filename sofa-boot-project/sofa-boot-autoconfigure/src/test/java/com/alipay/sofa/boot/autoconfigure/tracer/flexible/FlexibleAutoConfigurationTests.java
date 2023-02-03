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
package com.alipay.sofa.boot.autoconfigure.tracer.flexible;

import com.alipay.common.tracer.core.reporter.facade.Reporter;
import com.alipay.common.tracer.core.span.SofaTracerSpan;
import com.alipay.sofa.boot.autoconfigure.tracer.SofaTracerAutoConfiguration;
import com.alipay.sofa.boot.tracer.flexible.MethodInvocationProcessor;
import com.alipay.sofa.boot.tracer.flexible.SofaTracerAdvisingBeanPostProcessor;
import com.alipay.sofa.boot.tracer.flexible.SofaTracerIntroductionInterceptor;
import com.alipay.sofa.tracer.plugin.flexible.FlexibleTracer;
import io.opentracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FlexibleAutoConfiguration}.
 *
 * @author huzijie
 * @version FlexibleAutoConfigurationTests.java, v 0.1 2023年01月11日 10:38 AM huzijie Exp $
 */
public class FlexibleAutoConfigurationTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withConfiguration(AutoConfigurations
                                                                 .of(FlexibleAutoConfiguration.class,
                                                                     SofaTracerAutoConfiguration.class));

    @Test
    public void registerFlexibleBeans() {
        this.contextRunner
                .run((context) -> assertThat(context)
                        .hasSingleBean(MethodInvocationProcessor.class)
                        .hasSingleBean(SofaTracerIntroductionInterceptor.class)
                        .hasSingleBean(SofaTracerAdvisingBeanPostProcessor.class)
                        .hasBean("sofaTracer"));
    }

    @Test
    public void noFlexibleBeansWhenTracerClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(Tracer.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(MethodInvocationProcessor.class)
                        .doesNotHaveBean(SofaTracerIntroductionInterceptor.class)
                        .doesNotHaveBean(SofaTracerAdvisingBeanPostProcessor.class)
                        .doesNotHaveBean("sofaTracer"));
    }

    @Test
    public void noFlexibleBeansWhenSofaTracerIntroductionInterceptorClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(SofaTracerIntroductionInterceptor.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(MethodInvocationProcessor.class)
                        .doesNotHaveBean(SofaTracerIntroductionInterceptor.class)
                        .doesNotHaveBean(SofaTracerAdvisingBeanPostProcessor.class)
                        .doesNotHaveBean("sofaTracer"));
    }

    @Test
    public void noFlexibleBeansWhenTracerAnnotationClassNotExist() {
        this.contextRunner.withClassLoader(new FilteredClassLoader(com.alipay.sofa.tracer.plugin.flexible.annotations.Tracer.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(MethodInvocationProcessor.class)
                        .doesNotHaveBean(SofaTracerIntroductionInterceptor.class)
                        .doesNotHaveBean(SofaTracerAdvisingBeanPostProcessor.class)
                        .doesNotHaveBean("sofaTracer"));
    }

    @Test
    public void noFlexibleBeansWhenPropertySetFalse() {
        this.contextRunner
                .withPropertyValues("sofa.boot.tracer.flexible.enabled=false")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(MethodInvocationProcessor.class)
                        .doesNotHaveBean(SofaTracerIntroductionInterceptor.class)
                        .doesNotHaveBean(SofaTracerAdvisingBeanPostProcessor.class)
                        .doesNotHaveBean("sofaTracer"));
    }

    @Test
    public void useCustomReporter() {
        this.contextRunner
                .withPropertyValues("sofa.boot.tracer.reporterName=" + EmptyReporter.class.getName())
                .run((context) -> {
                    assertThat(context).hasBean("sofaTracer");
                    Tracer tracer = context.getBean("sofaTracer", Tracer.class);
                    assertThat(tracer).isInstanceOf(FlexibleTracer.class);
                    Field field = ReflectionUtils.findField(tracer.getClass(), "reporter");
                    ReflectionUtils.makeAccessible(field);
                    Object object = ReflectionUtils.getField(field, tracer);
                    assertThat(object).isInstanceOf(EmptyReporter.class);
                });
    }

    public static class EmptyReporter implements Reporter {

        @Override
        public String getReporterType() {
            return null;
        }

        @Override
        public void report(SofaTracerSpan span) {

        }

        @Override
        public void close() {

        }
    }

}

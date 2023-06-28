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
package com.alipay.sofa.boot.startup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StartupReporterBeanPostProcessor}.
 *
 * @author huzijie
 * @version StartupReporterBeanPostProcessorTests.java, v 0.1 2023年02月01日 3:52 PM huzijie Exp $
 */
public class StartupReporterBeanPostProcessorTests {

    @Test
    public void setStartupReporter() {
        StartupReporter startupReporter = new StartupReporter();
        StartupReporterBeanPostProcessor startupReporterBeanPostProcessor = new StartupReporterBeanPostProcessor(
            startupReporter);
        StartupReporterAwareImpl startupReporterAware = new StartupReporterAwareImpl();
        startupReporterAware = (StartupReporterAwareImpl) startupReporterBeanPostProcessor
            .postProcessBeforeInitialization(startupReporterAware, "startupReporterAwareImpl");

        assertThat(startupReporterAware).isNotNull();
        assertThat(startupReporterAware.getStartupReporter()).isEqualTo(startupReporter);
    }

    static class StartupReporterAwareImpl implements StartupReporterAware {

        private StartupReporter startupReporter;

        @Override
        public void setStartupReporter(StartupReporter startupReporter) throws BeansException {
            this.startupReporter = startupReporter;
        }

        public StartupReporter getStartupReporter() {
            return startupReporter;
        }
    }
}

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
package com.alipay.sofa.smoke.tests.boot;

import com.alipay.sofa.boot.autoconfigure.detect.LegacyAutoConfigurationDetectListener;
import com.alipay.sofa.boot.util.LogOutPutUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link LegacyAutoConfigurationDetectListener}.
 *
 * @author huzijie
 * @version LegacyAutoConfigurationDetectListenerTests.java, v 0.1 2023年04月18日 10:46 AM huzijie Exp $
 */
@SpringBootTest(classes = BootSofaBootApplication.class)
@ExtendWith(OutputCaptureExtension.class)
public class LegacyAutoConfigurationDetectListenerTests {

    static {
        LogOutPutUtils.openOutPutForLoggers(LegacyAutoConfigurationDetectListener.class);
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void checkWarnLog(CapturedOutput capturedOutput) {
        assertThat(applicationContext.containsBean("legacyBean")).isFalse();
        assertThat(applicationContext.containsBean("bothBean")).isTrue();

        assertThat(capturedOutput.getAll()).contains(
            "These configurations defined in spring.factories file will be ignored:");
        assertThat(capturedOutput.getAll())
            .contains(
                "--- com.alipay.sofa.smoke.tests.boot.LegacyAutoConfigurationDetectListenerTests$LegacyAutoConfiguration");
    }

    @AutoConfiguration
    public static class LegacyAutoConfiguration {

        @Bean
        public Object legacyBean() {
            return new Object();
        }
    }

    @AutoConfiguration
    public static class BothAutoConfiguration {

        @Bean
        public Object bothBean() {
            return new Object();
        }
    }
}

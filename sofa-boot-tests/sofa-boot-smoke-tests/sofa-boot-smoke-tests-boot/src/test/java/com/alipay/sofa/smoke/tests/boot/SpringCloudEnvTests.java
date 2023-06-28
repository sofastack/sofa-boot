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

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.listener.SpringCloudConfigListener;
import com.alipay.sofa.boot.util.SofaBootEnvUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SpringCloudConfigListener}.
 *
 * @author qilong.zql
 * @since 2.5.0
 */
@SpringBootTest(classes = BootSofaBootApplication.class)
public class SpringCloudEnvTests {

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void isSpringCloudBootstrapEnvironment() {
        Environment environment = ctx.getEnvironment();
        assertThat(SofaBootEnvUtils.isSpringCloudBootstrapEnvironment(environment)).isFalse();
        assertThat(SampleSpringContextInitializer.bootstrapContext.get()).isEqualTo(1L);
        assertThat(SampleSpringContextInitializer.applicationContext.get()).isEqualTo(1L);
        assertThat(SofaBootEnvUtils.isSpringCloudBootstrapEnvironment(null)).isFalse();
        assertThat(SofaBootEnvUtils.isSpringCloud()).isTrue();
        assertThat(SofaBootEnvUtils.isSpringCloudEnvironmentEnabled(environment)).isTrue();
        assertThat("smoke-tests-boot").isEqualTo(
            SampleSpringContextInitializer.bootstrapEnvironment
                .getProperty(SofaBootConstants.APP_NAME_KEY));
        assertThat("smoke-tests-boot").isEqualTo(
            SampleSpringContextInitializer.applicationEnvironment
                .getProperty(SofaBootConstants.APP_NAME_KEY));
        assertThat("INFO").isEqualTo(
            SampleSpringContextInitializer.bootstrapEnvironment
                .getProperty("logging.level.com.alipay.test"));
        assertThat("INFO").isEqualTo(
            SampleSpringContextInitializer.applicationEnvironment
                .getProperty("logging.level.com.alipay.test"));
        assertThat("WARN").isEqualTo(
            SampleSpringContextInitializer.bootstrapEnvironment
                .getProperty("logging.level.com.test.demo"));
        assertThat("WARN").isEqualTo(
            SampleSpringContextInitializer.applicationEnvironment
                .getProperty("logging.level.com.test.demo"));
        assertThat("./logs").isEqualTo(
            SampleSpringContextInitializer.bootstrapEnvironment.getProperty("logging.path"));
        assertThat("./logs").isEqualTo(
            SampleSpringContextInitializer.applicationEnvironment.getProperty("logging.path"));
        assertThat(SampleSpringContextInitializer.bootstrapEnvironment.getProperty("any.key"))
            .isNull();
        assertThat("any.value").isEqualTo(
            SampleSpringContextInitializer.applicationEnvironment.getProperty("any.key"));
    }
}

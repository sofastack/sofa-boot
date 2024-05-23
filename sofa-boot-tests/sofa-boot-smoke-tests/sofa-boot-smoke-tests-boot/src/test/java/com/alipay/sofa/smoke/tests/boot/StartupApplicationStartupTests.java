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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.metrics.ApplicationStartup;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author huzijie
 * @version StartupApplicationStartupTests.java, v 0.1 2024年05月23日 17:40 huzijie Exp $
 */
@SpringBootTest
public class StartupApplicationStartupTests {

    @Autowired
    private ConfigurableApplicationContext context;

    @Test
    public void checkBufferApplicationStartup() {
        ApplicationStartup applicationStartup = context.getApplicationStartup();
        assertThat(applicationStartup).isInstanceOf(BufferingApplicationStartup.class);
    }
}

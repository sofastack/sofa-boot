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
package com.alipay.sofa.smoke.tests.runtime.filter;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.filter.JvmFilter;
import com.alipay.sofa.smoke.tests.runtime.RuntimeSofaBootApplication;
import com.alipay.sofa.smoke.tests.runtime.service.SampleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link JvmFilter}.
 *
 * @author huzijie
 * @version JvmFilterTests.java, v 0.1 2023年02月02日 10:56 AM huzijie Exp $
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class)
@Import(JvmFilterTestConfiguration.class)
@TestPropertySource(properties = "sofa.boot.runtime.jvmFilterEnable=true")
public class JvmFilterTests {

    @Autowired
    private JvmFilterTestConfiguration jvmFilterConfig;

    @SofaReference
    private SampleService              demoService;

    @Test
    public void test() {
        assertThat(jvmFilterConfig.getBeforeCount()).isEqualTo(0);
        assertThat(jvmFilterConfig.getAfterCount()).isEqualTo(0);

        String result = demoService.service();

        assertThat(result).isEqualTo("egressFilter1");
        assertThat(jvmFilterConfig.getBeforeCount()).isEqualTo(3);
        assertThat(jvmFilterConfig.getAfterCount()).isEqualTo(1);
    }
}

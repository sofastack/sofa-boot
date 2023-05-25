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
package com.alipay.sofa.smoke.tests.runtime.spring;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.smoke.tests.runtime.RuntimeSofaBootApplication;
import com.alipay.sofa.smoke.tests.runtime.service.SampleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SofaServiceBeanAnnotationTests
 *
 * @author xunfang
 * @version SofaServiceBeanAnnotationTests.java, v 0.1 2023/5/23
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class)
public class SofaServiceBeanAnnotationTests {

    @SofaReference(uniqueId = "sofaServiceBeanService")
    private SampleService sofaServiceBeanService;

    @Test
    public void testSofaServiceBean() {
        assertThat(sofaServiceBeanService).isNotNull();
        assertThat("sofaServiceBeanService").isEqualTo(sofaServiceBeanService.service());
    }

}

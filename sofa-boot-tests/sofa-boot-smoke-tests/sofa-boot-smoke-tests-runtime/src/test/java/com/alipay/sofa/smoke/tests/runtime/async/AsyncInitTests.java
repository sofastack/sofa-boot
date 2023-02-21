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
package com.alipay.sofa.smoke.tests.runtime.async;

import com.alipay.sofa.runtime.api.annotation.SofaAsyncInit;
import com.alipay.sofa.runtime.async.AsyncInitMethodManager;
import com.alipay.sofa.smoke.tests.runtime.RuntimeSofaBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link AsyncInitMethodManager}.
 *
 * @author huzijie
 * @version JvmFilterTests.java, v 0.1 2023年02月02日 10:56 AM huzijie Exp $
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class)
@Import(AsyncInitTests.AsyncInitTestConfiguration.class)
@TestPropertySource(properties = { "sofa.boot.runtime.asyncInitExecutorCoreSize=20",
                                  "sofa.boot.runtime.asyncInitExecutorMaxSize=20" })
public class AsyncInitTests {

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void asyncInitBean() {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        assertThat(TimeWasteBean.getCount()).isEqualTo(15);
        for (int i = 1; i <= 12; i++) {
            TimeWasteBean bean = ctx.getBean("testBean" + i, TimeWasteBean.class);
            if (bean.getPrintTime() < min) {
                min = bean.getPrintTime();
            }
            if (bean.getPrintTime() > max) {
                max = bean.getPrintTime();
            }
            String threadName = bean.getThreadName();
            assertThat(threadName).contains("async-init-bean");
        }
        for (int i = 13; i <= 15; i++) {
            TimeWasteBean bean = ctx.getBean("testBean" + i, TimeWasteBean.class);
            String threadName = bean.getThreadName();
            assertThat(threadName).doesNotContain("async-init-bean");
        }
        assertThat(max - min < 5000).isTrue();
        TimeWasteBean.resetCount();
    }

    /**
     * @author huzijie
     * @version AsyncInitTestConfiguration.java, v 0.1 2023年02月02日 2:23 PM huzijie Exp $
     */
    @TestConfiguration
    @ImportResource("classpath:/spring/async/*.xml")
    static class AsyncInitTestConfiguration {

        @Bean(initMethod = "init")
        @SofaAsyncInit
        public TimeWasteBean testBean12() {
            return new TimeWasteBean();
        }

        @Bean(initMethod = "init")
        @SofaAsyncInit(false)
        public TimeWasteBean testBean13() {
            return new TimeWasteBean();
        }
    }
}
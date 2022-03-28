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
package com.alipay.sofa.runtime.test;

import com.alipay.sofa.runtime.api.annotation.SofaAsyncInit;
import com.alipay.sofa.runtime.test.beans.TimeWasteBean;
import com.alipay.sofa.runtime.test.configuration.SofaRuntimeTestConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author qilong.zql
 * @since 2.6.0
 */
@RunWith(SpringRunner.class)
@TestPropertySource(properties = { "async.config=false",
        "com.alipay.sofa.boot.asyncInitBeanCoreSize=20",
        "com.alipay.sofa.boot.asyncInitBeanMaxSize=20" })
public class AsyncInitTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void testAsyncInitBean() {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        Assert.assertEquals(16, TimeWasteBean.getCount());
        for (int i = 1; i <= 12; i++) {
            TimeWasteBean bean = ctx.getBean("testBean" + i, TimeWasteBean.class);
            if (bean.getPrintTime() < min) {
                min = bean.getPrintTime();
            }
            if (bean.getPrintTime() > max) {
                max = bean.getPrintTime();
            }
            String threadName = bean.getThreadName();
            Assert.assertTrue(threadName, threadName.contains("async-init-bean"));
        }
        for (int i = 13; i <= 16; i++) {
            TimeWasteBean bean = ctx.getBean("testBean" + i, TimeWasteBean.class);
            String threadName = bean.getThreadName();
            Assert.assertFalse(threadName, threadName.contains("async-init-bean"));
        }
        Assert.assertTrue("max:" + max + ", min:" + min, max - min < 5000);
        TimeWasteBean.resetCount();
    }

    @Configuration(proxyBeanMethods = false)
    @ImportResource({ "classpath*:META-INF/async/*.xml" })
    @Import(SofaRuntimeTestConfiguration.class)
    static class AsyncInitTestConfiguration {

        @Bean(initMethod = "init")
        @SofaAsyncInit
        public TimeWasteBean testBean12() {
            return new TimeWasteBean();
        }

        @Bean(initMethod = "init")
        @SofaAsyncInit("false")
        public TimeWasteBean testBean13() {
            return new TimeWasteBean();
        }

        @Bean(initMethod = "init")
        @SofaAsyncInit("${async.config}")
        public TimeWasteBean testBean14() {
            return new TimeWasteBean();
        }

    }

    @SofaAsyncInit
    static class TimeWasteBeanChild extends TimeWasteBean {

    }
}
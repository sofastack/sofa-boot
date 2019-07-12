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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.runtime.test.beans.TimeWasteBean;
import com.alipay.sofa.runtime.test.configuration.SofaRuntimeTestConfiguration;

/**
 * @author qilong.zql
 * @since 2.6.0
 */
@RunWith(SpringRunner.class)
public class AsyncInitTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void testAsyncInitBean() {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        Assert.assertEquals(12, TimeWasteBean.getCount());
        for (int i = 1; i <= 10; i++) {
            TimeWasteBean bean = ctx.getBean("testBean" + i, TimeWasteBean.class);
            if (bean.getPrintTime() < min) {
                min = bean.getPrintTime();
            }
            if (bean.getPrintTime() > max) {
                max = bean.getPrintTime();
            }
        }
        Assert.assertTrue("max:" + max + ", min:" + min, max - min < 3500);
        TimeWasteBean.resetCount();
    }

    @Configuration
    @ImportResource({ "classpath*:META-INF/async/*.xml" })
    @Import(SofaRuntimeTestConfiguration.class)
    static class AsyncInitTestConfiguration {
    }
}
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
package com.alipay.sofa.runtime.integration;

import com.alipay.sofa.runtime.beans.TimeWasteBean;
import com.alipay.sofa.runtime.integration.base.TestBase;
import com.alipay.sofa.runtime.spring.async.AsyncTaskExecutor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author qilong.zql
 * @since 2.6.0
 */
public class AsyncInitTest extends TestBase {

    @Before
    public void before() {
        try {
            Field field = AsyncTaskExecutor.class.getDeclaredField("STARTED");
            field.setAccessible(true);
            AtomicBoolean atomicBoolean = (AtomicBoolean) field.get(null);
            atomicBoolean.set(false);
        } catch (Throwable throwable) {
            // ignore;
        }
    }

    @Test
    public void testAsyncInitBean() {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        initApplicationContext(new HashMap<String, Object>(), EmptyConfiguration.class);
        Assert.assertEquals(12, TimeWasteBean.getCount());
        for (int i = 1; i <= 10; i++) {
            TimeWasteBean bean = applicationContext.getBean("testBean" + i, TimeWasteBean.class);
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

    @EnableAutoConfiguration
    @Configuration
    @ImportResource({ "classpath*:META-INF/async/*.xml" })
    static class EmptyConfiguration {
    }
}
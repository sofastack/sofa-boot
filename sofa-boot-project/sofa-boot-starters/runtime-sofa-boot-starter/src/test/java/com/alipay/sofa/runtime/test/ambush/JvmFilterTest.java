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
package com.alipay.sofa.runtime.test.ambush;

import com.alipay.sofa.runtime.ambush.Context;
import com.alipay.sofa.runtime.ambush.FilterHolder;
import com.alipay.sofa.runtime.ambush.JvmFilter;
import com.alipay.sofa.runtime.test.RuntimeTestBase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/8/18
 */
@SpringBootTest(classes = JvmFilterTestConfiguration.class, properties = "spring.application.name=filterTest")
public class JvmFilterTest extends RuntimeTestBase {
    @Autowired
    private Service myService;

    @BeforeClass
    public static void before() {
        FilterHolder.clearJvmFilters();
    }

    @Test
    public void test() {
        Assert.assertEquals("egressFilter2", myService.say());
        Assert.assertEquals(5, FilterHolder.getJvmFilters().size());
        Assert.assertEquals(3, JvmFilterTestConfiguration.beforeCount);
        Assert.assertEquals(1, JvmFilterTestConfiguration.afterCount);
    }

    @Test
    public void testResort() {
        FilterHolder.clearJvmFilters();

        FilterHolder.addJvmFilter(new JvmFilter() {
            @Override
            public boolean before(Context context) {
                return false;
            }

            @Override
            public boolean after(Context context) {
                return false;
            }

            @Override
            public int getOrder() {
                return 10;
            }
        });

        FilterHolder.addJvmFilter(new JvmFilter() {
            @Override
            public boolean before(Context context) {
                return false;
            }

            @Override
            public boolean after(Context context) {
                return false;
            }

            @Override
            public int getOrder() {
                return 9;
            }
        });

        List<JvmFilter> filters = FilterHolder.getJvmFilters();
        Assert.assertEquals(9, filters.get(0).getOrder());
        Assert.assertEquals(10, filters.get(1).getOrder());

        FilterHolder.addJvmFilter(new JvmFilter() {
            @Override
            public boolean before(Context context) {
                return false;
            }

            @Override
            public boolean after(Context context) {
                return false;
            }

            @Override
            public int getOrder() {
                return 0;
            }
        });

        filters = FilterHolder.getJvmFilters();
        Assert.assertEquals(0, filters.get(0).getOrder());
    }
}

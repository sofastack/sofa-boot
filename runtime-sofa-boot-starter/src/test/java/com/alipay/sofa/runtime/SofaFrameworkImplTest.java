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
package com.alipay.sofa.runtime;

import com.alipay.sofa.runtime.spi.SofaFramework;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author qilong.zql
 * @since 2.3.1
 */
public class SofaFrameworkImplTest {

    @Test
    public void testSofaFrameworkImpl(final @Mocked SofaRuntimeManager app1,
                                      final @Mocked SofaRuntimeManager app2,
                                      final @Mocked SofaRuntimeContext context) {
        SofaFramework sofaFramework = new SofaFrameworkImpl();

        Assert.assertTrue(sofaFramework.getSofaFrameworkAppNames().size() == 0);

        new NonStrictExpectations() {
            {
                app1.getAppName();
                result = "app1";
                app1.getSofaRuntimeContext();
                result = context;

                app2.getAppName();
                result = "app2";
                app2.getSofaRuntimeContext();
                result = context;
            }
        };

        ((SofaFrameworkInternal) sofaFramework).registerSofaRuntimeManager(app1);
        ((SofaFrameworkInternal) sofaFramework).registerSofaRuntimeManager(app2);

        Assert.assertTrue(sofaFramework.getSofaFrameworkAppNames().size() == 2);
        Assert.assertTrue(sofaFramework.getSofaFrameworkAppNames().contains("app1"));
        Assert.assertTrue(sofaFramework.getSofaFrameworkAppNames().contains("app2"));

        Assert.assertNotNull(sofaFramework.getSofaRuntimeManager("app1"));
        Assert.assertNotNull(sofaFramework.getSofaRuntimeManager("app2"));

        Assert.assertNotNull(sofaFramework.getSofaRuntimeContext("app1"));
        Assert.assertNotNull(sofaFramework.getSofaRuntimeContext("app2"));

        sofaFramework.removeSofaRuntimeManager("app1");
        Assert.assertNull(sofaFramework.getSofaRuntimeManager("app1"));
        Assert.assertNull(sofaFramework.getSofaRuntimeContext("app1"));
        Assert.assertTrue(sofaFramework.getSofaFrameworkAppNames().size() == 1);

        sofaFramework.removeSofaRuntimeManager("app2");
        Assert.assertTrue(sofaFramework.getSofaFrameworkAppNames().size() == 0);

    }

}
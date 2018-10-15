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
package com.alipay.sofa.infra.utils;

import com.alipay.sofa.infra.base.AbstractTestBase;
import org.junit.Test;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * SOFABootEnvUtils Tester.
 *
 * @author <guanchao.ygc>
 * @version 1.0
 * @since <pre>2.5.0</pre>
 */
public class SOFABootEnvUtilsTest extends AbstractTestBase
                                                          implements
                                                          ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static AtomicLong bootstrapContext    = new AtomicLong(0L);

    private static AtomicLong applicatioinContext = new AtomicLong(0L);

    /**
     * Method: isSpringCloudBootstrapEnvironment(Environment environment)
     */
    @Test
    public void testIsSpringCloudBootstrapEnvironment() throws Exception {
        Environment environment = ctx.getEnvironment();
        assertFalse(SOFABootEnvUtils.isSpringCloudBootstrapEnvironment(environment));
        assertEquals(1L, bootstrapContext.get());
        assertEquals(1L, applicatioinContext.get());
        assertFalse(SOFABootEnvUtils.isSpringCloudBootstrapEnvironment(null));
    }

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        if (SOFABootEnvUtils.isSpringCloudBootstrapEnvironment(configurableApplicationContext
            .getEnvironment())) {
            bootstrapContext.incrementAndGet();
            return;
        }
        applicatioinContext.incrementAndGet();
    }
}

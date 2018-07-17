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
package com.alipay.sofa.runtime.log;

import com.alipay.sofa.runtime.spi.log.SofaRuntimeLoggerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class SofaRuntimeLoggerFactoryTest {
    @Test
    public void testLoggerFactory() {
        Logger logger = SofaRuntimeLoggerFactory.getLogger(SofaRuntimeLoggerFactoryTest.class);
        Assert.assertNotNull(logger);

        logger = SofaRuntimeLoggerFactory.getLogger(SofaRuntimeLoggerFactoryTest.class
            .getCanonicalName());
        Assert.assertNotNull(logger);

        logger = SofaRuntimeLoggerFactory.getLogger("");
        Assert.assertNull(logger);
    }
}
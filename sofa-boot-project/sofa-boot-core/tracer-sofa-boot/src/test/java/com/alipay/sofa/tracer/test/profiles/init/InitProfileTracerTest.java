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
package com.alipay.sofa.tracer.test.profiles.init;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import com.alipay.sofa.tracer.test.base.AbstractTestBase;

/**
 * InitProfileTracerTest
 *
 * @author yangguanchao
 * @since 2018/05/08
 */
@ActiveProfiles("init")
public class InitProfileTracerTest extends AbstractTestBase {

    @Autowired
    private Environment environment;

    @Test
    public void initPropertyTest() throws Exception {
        String loggingPath = environment.getProperty("logging.path1");
        assertEquals("./logs", loggingPath);
    }
}

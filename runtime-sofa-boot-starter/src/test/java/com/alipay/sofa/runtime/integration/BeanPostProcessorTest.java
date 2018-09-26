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

import com.alipay.sofa.runtime.integration.base.AbstractTestBase;
import com.alipay.sofa.runtime.util.StateMessage;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * @author qilong.zql
 * @since 2.4.1
 */
public class BeanPostProcessorTest extends AbstractTestBase {
    @Test
    public void test() {
        Assert.isTrue("aop".equals(StateMessage.getFactoryMessage()));
        Assert.isTrue("config".equals(StateMessage.getConfigMessage()));
    }
}
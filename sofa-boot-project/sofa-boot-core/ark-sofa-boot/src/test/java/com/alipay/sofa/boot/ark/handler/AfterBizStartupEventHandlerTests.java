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
package com.alipay.sofa.boot.ark.handler;

import com.alipay.sofa.ark.spi.event.biz.AfterBizStartupEvent;
import com.alipay.sofa.boot.ark.MockBiz;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AfterBizStartupEventHandler}.
 *
 * @author huzijie
 * @version AfterBizStartupEventHandlerTests.java, v 0.1 2023年04月06日 11:08 AM huzijie Exp $
 */
public class AfterBizStartupEventHandlerTests {

    @Test
    public void handleEvent() {
        AfterBizStartupEventHandler afterBizStartupEventHandler = new AfterBizStartupEventHandler();
        MockBiz mockBiz = new MockBiz();
        afterBizStartupEventHandler.handleEvent(new AfterBizStartupEvent(mockBiz));

        assertThat(mockBiz.isCalled()).isTrue();
    }
}

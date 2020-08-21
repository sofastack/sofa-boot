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
package com.alipay.sofa.startup.test;

import com.alipay.sofa.startup.SofaStartupContext;
import com.alipay.sofa.startup.SofaStartupReporter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashMap;

import static org.mockito.Mockito.when;

/**
 * @author: Zhijie
 * @since: 2020/7/13
 */
public class SofaStartupReporterTest {
    @Mock
    private SofaStartupContext sofaStartupContext;

    @Before
    public void init() {
        sofaStartupContext = Mockito.mock(SofaStartupContext.class);
        when(sofaStartupContext.getAppStartupTime()).thenReturn(1L);
        when(sofaStartupContext.getBeanInitCost()).thenReturn(1L);
        when(sofaStartupContext.getComponentCost()).thenReturn(1L);
        when(sofaStartupContext.getIsleInstallCost()).thenReturn(1L);
        when(sofaStartupContext.getWebServerInitCost()).thenReturn(1L);
        when(sofaStartupContext.getBeanInitDetail()).thenReturn(new HashMap<>());
        when(sofaStartupContext.getComponentDetail()).thenReturn(new HashMap<>());
    }

    @Test
    public void testReport() {
        SofaStartupReporter sofaStartupReporter = new SofaStartupReporter(sofaStartupContext);
        SofaStartupReporter.SofaStartupCostModel sofaStartupCostModel = sofaStartupReporter
            .report();
        Assert.assertNotNull(sofaStartupCostModel);
        Assert.assertTrue(sofaStartupCostModel.getTotalCost() > 0);
        Assert.assertNotNull(sofaStartupCostModel.getBaseCosts());
        Assert.assertTrue(sofaStartupCostModel.getBaseCosts().size() >= 4);
        Assert.assertNotNull(sofaStartupCostModel.getDetailCosts());
        Assert.assertTrue(sofaStartupCostModel.getDetailCosts().size() >= 2);
    }
}

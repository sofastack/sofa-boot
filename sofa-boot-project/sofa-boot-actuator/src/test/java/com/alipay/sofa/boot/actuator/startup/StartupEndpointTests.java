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
package com.alipay.sofa.boot.actuator.startup;

import com.alipay.sofa.boot.startup.BaseStat;
import com.alipay.sofa.boot.startup.StartupReporter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link StartupEndpoint}.
 *
 * @author huzijie
 * @version StartupEndpointTests.java, v 0.1 2023年01月04日 11:51 AM huzijie Exp $
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StartupEndpointTests {

    private final StartupReporter startupReporter = new StartupReporter();

    private final StartupEndpoint startupEndPoint = new StartupEndpoint(startupReporter);

    @Test
    @Order(1)
    public void startupSnapshot() {
        startupReporter.setAppName("StartupEndpointTests");
        assertThat(startupEndPoint.startupSnapshot().getAppName())
            .isEqualTo("StartupEndpointTests");
    }

    @Test
    @Order(2)
    public void startup() {
        StartupReporter.StartupStaticsModel staticsModel = startupReporter.getStartupStaticsModel();
        BaseStat baseStat = new BaseStat();
        List<BaseStat> baseStatList = new ArrayList<>();
        baseStatList.add(baseStat);
        staticsModel.setStageStats(baseStatList);
        staticsModel.setAppName("StartupEndpointTests");
        StartupReporter.StartupStaticsModel model = startupEndPoint.startup();
        assertThat(model.getAppName()).isEqualTo("StartupEndpointTests");
        assertThat(model.getStageStats().size()).isEqualTo(1);

        model = startupEndPoint.startupSnapshot();
        assertThat(model.getAppName()).isEqualTo("StartupEndpointTests");
        assertThat(model.getStageStats().size()).isEqualTo(0);
    }
}

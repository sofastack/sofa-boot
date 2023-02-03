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
package com.alipay.sofa.boot.actuator.health;

import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.ArrayList;
import java.util.List;

import static com.alipay.sofa.boot.actuator.health.ComponentHealthChecker.COMPONENT_NAME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ComponentHealthChecker}.
 *
 * @author huzijie
 * @version ComponentHealthCheckerTests.java, v 0.1 2023年01月05日 6:01 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class ComponentHealthCheckerTests {

    @InjectMocks
    private ComponentHealthChecker componentHealthChecker;

    @Mock
    private SofaRuntimeContext     sofaRuntimeContext;     ;

    @Mock
    private ComponentManager       componentManager;

    @Test
    public void componentsHealthCheckSuccess() {
        List<ComponentInfo> componentInfos = new ArrayList<>();
        ComponentInfo componentInfo = Mockito.mock(ComponentInfo.class);
        HealthResult healthResult = new HealthResult("demo");
        healthResult.setHealthy(true);
        Mockito.doReturn(healthResult).when(componentInfo).isHealthy();
        componentInfos.add(componentInfo);

        Mockito.doReturn(componentManager).when(sofaRuntimeContext).getComponentManager();
        Mockito.doReturn(componentInfos).when(componentManager).getComponents();
        Health health = componentHealthChecker.isHealthy();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    public void componentsHealthCheckFail() {
        List<ComponentInfo> componentInfos = new ArrayList<>();
        ComponentInfo componentInfo = Mockito.mock(ComponentInfo.class);
        HealthResult healthResult = new HealthResult("demo");
        healthResult.setHealthy(false);
        healthResult.setHealthReport("check failed");
        Mockito.doReturn(healthResult).when(componentInfo).isHealthy();
        componentInfos.add(componentInfo);

        Mockito.doReturn(componentManager).when(sofaRuntimeContext).getComponentManager();
        Mockito.doReturn(componentInfos).when(componentManager).getComponents();
        Health health = componentHealthChecker.isHealthy();
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails().toString()).isEqualTo("{demo=check failed}");
    }

    @Test
    public void componentsHealthConfigs() {
        assertThat(componentHealthChecker.getComponentName()).isEqualTo(COMPONENT_NAME);
        assertThat(componentHealthChecker.getRetryCount()).isEqualTo(20);
        assertThat(componentHealthChecker.getRetryTimeInterval()).isEqualTo(1000);
        assertThat(componentHealthChecker.getTimeout()).isEqualTo(10000);
        assertThat(componentHealthChecker.isStrictCheck()).isTrue();
    }
}

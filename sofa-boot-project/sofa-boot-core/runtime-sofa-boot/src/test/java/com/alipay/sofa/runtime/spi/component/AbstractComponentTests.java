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
package com.alipay.sofa.runtime.spi.component;

import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AbstractComponent}.
 * 
 * @author huzijie
 * @author xuanbei
 */
public class AbstractComponentTests {

    private final AbstractComponent abstractComponent = new AbstractComponent() {
                                                          @Override
                                                          public ComponentType getType() {
                                                              return null;
                                                          }

                                                          @Override
                                                          public Map<String, Property> getProperties() {
                                                              return null;
                                                          }
                                                      };

    @Test
    public void activate() throws Exception {
        abstractComponent.register();
        assertThat(abstractComponent.getState()).isEqualTo(ComponentStatus.REGISTERED);
        abstractComponent.resolve();
        assertThat(abstractComponent.getState()).isEqualTo(ComponentStatus.RESOLVED);
        abstractComponent.activate();
        assertThat(abstractComponent.getState()).isEqualTo(ComponentStatus.ACTIVATED);
        abstractComponent.deactivate();
        assertThat(abstractComponent.getState()).isEqualTo(ComponentStatus.RESOLVED);
    }

    @Test
    public void isHealthy() throws Exception {
        Field componentNameField = AbstractComponent.class.getDeclaredField("componentName");
        componentNameField.setAccessible(true);
        ComponentName componentName = new ComponentName(new ComponentType("healthy"), "test");
        componentNameField.set(abstractComponent, componentName);

        assertThat("healthy:test").isEqualTo(abstractComponent.dump());

        abstractComponent.unregister();
        abstractComponent.register();
        abstractComponent.resolve();
        abstractComponent.activate();
        assertThat(abstractComponent.isHealthy().isHealthy()).isTrue();

        abstractComponent.exception(new RuntimeException("this is a test exception"));
        HealthResult healthResult = abstractComponent.isHealthy();
        assertThat(healthResult.isHealthy()).isFalse();
        assertThat("this is a test exception").isEqualTo(healthResult.getHealthReport());

        abstractComponent.deactivate();
        assertThat(abstractComponent.isHealthy().isHealthy()).isFalse();
        abstractComponent.unregister();
    }

}

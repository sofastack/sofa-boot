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
package com.alipay.sofa.runtime.context;

import com.alipay.sofa.boot.context.SofaGenericApplicationContext;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

/**
 * Tests for {@link ComponentContextRefreshInterceptor}.
 *
 * @author huzijie
 * @version ComponentContextRefreshInterceptorTests.java, v 0.1 2023年04月10日 11:22 AM huzijie Exp $
 */
public class ComponentContextRefreshInterceptorTests {

    @Test
    void afterRefreshWithNullThrowableShouldRegisterComponent() {
        // Arrange
        SofaRuntimeManager sofaRuntimeManager = Mockito.mock(SofaRuntimeManager.class);
        ComponentManager componentManager = Mockito.mock(ComponentManager.class);
        Mockito.when(sofaRuntimeManager.getComponentManager()).thenReturn(componentManager);
        SofaRuntimeContext sofaRuntimeContext = Mockito.mock(SofaRuntimeContext.class);
        Mockito.when(sofaRuntimeManager.getSofaRuntimeContext()).thenReturn(sofaRuntimeContext);
        SofaGenericApplicationContext context = new SofaGenericApplicationContext();
        ComponentContextRefreshInterceptor interceptor = new ComponentContextRefreshInterceptor(
            sofaRuntimeManager);

        // Act
        interceptor.afterRefresh(context, null);

        // Assert
        Mockito.verify(componentManager, times(1)).register(any());
    }

    @Test
    void afterRefreshWithThrowableShouldUnregisterComponents() {
        // Arrange
        SofaRuntimeManager sofaRuntimeManager = Mockito.mock(SofaRuntimeManager.class);
        ComponentManager componentManager = Mockito.mock(ComponentManager.class);
        Mockito.when(sofaRuntimeManager.getComponentManager()).thenReturn(componentManager);
        SofaGenericApplicationContext context = new SofaGenericApplicationContext();
        ComponentInfo componentInfo = Mockito.mock(ComponentInfo.class);
        Mockito.when(componentInfo.getName()).thenReturn(
            new ComponentName(new ComponentType("test"), "TestComponent"));
        Mockito.when(componentManager.getComponentInfosByApplicationContext(context)).thenReturn(
            Collections.singletonList(componentInfo));
        ComponentContextRefreshInterceptor interceptor = new ComponentContextRefreshInterceptor(
            sofaRuntimeManager);

        // Act
        interceptor.afterRefresh(context, new Throwable());

        // Assert
        Mockito.verify(componentManager, times(1)).unregister(any());
    }

}

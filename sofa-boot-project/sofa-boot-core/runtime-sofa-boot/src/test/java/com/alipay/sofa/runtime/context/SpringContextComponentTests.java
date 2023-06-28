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

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SpringContextComponent}.
 *
 * @author huzijie
 * @version SpringContextComponentTests.java, v 0.1 2023年04月10日 11:28 AM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class SpringContextComponentTests {

    @Mock
    private ComponentName      componentName;

    @Mock
    private Implementation     implementation;

    @Mock
    private SofaRuntimeContext sofaRuntimeContext;

    @Test
    void getTypeShouldReturnSpringComponentType() {
        // Arrange
        SpringContextComponent component = new SpringContextComponent(componentName,
            implementation, sofaRuntimeContext);

        // Act
        ComponentType result = component.getType();

        // Assert
        assertThat(result).isEqualTo(SpringContextComponent.SPRING_COMPONENT_TYPE);
    }

    @Test
    void getPropertiesShouldReturnNull() {
        // Arrange
        SpringContextComponent component = new SpringContextComponent(componentName,
            implementation, sofaRuntimeContext);

        // Act
        Map<String, Property> result = component.getProperties();

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void getApplicationContextShouldReturnTarget() {
        // Arrange
        GenericApplicationContext applicationContext = new GenericApplicationContext();
        Implementation implementation = new SpringContextImplementation(applicationContext);
        SpringContextComponent component = new SpringContextComponent(componentName,
            implementation, sofaRuntimeContext);

        // Act
        ApplicationContext result = component.getApplicationContext();

        // Assert
        assertThat(result).isEqualTo(applicationContext);
    }

    @Test
    void activateShouldSetComponentStatusToActivated() throws ServiceRuntimeException {
        // Arrange
        SpringContextComponent component = new SpringContextComponent(componentName,
            implementation, sofaRuntimeContext);

        // Act
        component.register();
        component.resolve();
        component.activate();

        // Assert
        assertThat(component.getState()).isEqualTo(ComponentStatus.ACTIVATED);
    }

    @Test
    void activateShouldNotSetComponentStatusToActivatedIfAlreadyResolved()
                                                                          throws ServiceRuntimeException {
        // Arrange
        SpringContextComponent component = new SpringContextComponent(componentName,
            implementation, sofaRuntimeContext);

        // Act
        component.activate();

        // Assert
        assertThat(component.getState()).isEqualTo(ComponentStatus.UNREGISTERED);
    }

    @Test
    void deactivateShouldCloseApplicationContextAndSetComponentStatusToDeactivated()
                                                                                    throws ServiceRuntimeException {
        // Arrange
        GenericApplicationContext applicationContext = Mockito
            .mock(GenericApplicationContext.class);
        Implementation implementation = new SpringContextImplementation(applicationContext);
        SpringContextComponent component = new SpringContextComponent(componentName,
            implementation, sofaRuntimeContext);

        // Act
        component.deactivate();

        // Assert
        Mockito.verify(applicationContext).close();
        assertThat(component.getState()).isEqualTo(ComponentStatus.UNREGISTERED);
    }

}

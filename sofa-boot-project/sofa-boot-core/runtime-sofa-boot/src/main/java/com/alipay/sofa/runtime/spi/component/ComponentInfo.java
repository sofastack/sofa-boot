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
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * Component info interface.
 *
 * @author xuanbei 18/2/28
 */
public interface ComponentInfo extends Component {

    /**
     * Get component type.
     *
     * @return component type
     */
    ComponentType getType();

    /**
     * Get component name.
     *
     * @return component name
     */
    ComponentName getName();

    /**
     * Get component create in which {@link ApplicationContext}, default for compatibility.
     *
     * @return component applicationContext
     */
    default ApplicationContext getApplicationContext() {
        return null;
    }

    /**
     * Set component create in which {@link ApplicationContext}, default for compatibility.
     *
     * @param applicationContext applicationContext
     */
    default void setApplicationContext(ApplicationContext applicationContext) {}

    /**
     * Get component implementation.
     *
     * @return component implementation
     */
    Implementation getImplementation();

    /**
     * Get sofa runtime context.
     *
     * @return {@link SofaRuntimeContext}
     */
    SofaRuntimeContext getContext();

    /**
     * Get all properties.
     *
     * @return properties
     */
    Map<String, Property> getProperties();

    /**
     * Get component status.
     *
     * @return component status
     */
    ComponentStatus getState();

    /**
     * Check component is activated or not.
     *
     * @return true or false
     */
    boolean isActivated();

    /**
     * Check component is resolved or not.
     *
     * @return true or false
     */
    boolean isResolved();

    /**
     * Get component information.
     *
     * @return dump
     */
    String dump();

    /**
     * Check component is health or not.
     *
     * @return health result
     */
    HealthResult isHealthy();

    /**
     * Whether component can be duplicated defined, default is true.
     *
     * @return can be duplicate
     */
    boolean canBeDuplicate();
}

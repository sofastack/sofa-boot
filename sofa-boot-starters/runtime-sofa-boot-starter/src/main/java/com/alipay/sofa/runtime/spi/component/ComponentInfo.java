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

import java.util.Map;

/**
 * Component info interface
 *
 * @author xuanbei 18/2/28
 */
public interface ComponentInfo extends Component {
    /**
     * get component type
     *
     * @return component type
     */
    ComponentType getType();

    /**
     * get component name
     *
     * @return component name
     */
    ComponentName getName();

    /**
     * get component implementation
     *
     * @return component implementation
     */
    Implementation getImplementation();

    /**
     * get sofa runtime context
     *
     * @return {@link SofaRuntimeContext}
     */
    SofaRuntimeContext getContext();

    /**
     * get all properties
     *
     * @return properties
     */
    Map<String, Property> getProperties();

    /**
     * get component status
     *
     * @return component status
     */
    ComponentStatus getState();

    /**
     * check component is activated or not
     *
     * @return true or false
     */
    boolean isActivated();

    /**
     * check component is resolved or not
     *
     * @return true or false
     */
    boolean isResolved();

    /**
     * get component information
     *
     * @return
     */
    String dump();

    /**
     * check component is health or not
     *
     * @return health result
     */
    HealthResult isHealthy();

    /**
     * whether component can be duplicated defined, default is true
     *
     * @return
     */
    boolean canBeDuplicate();
}

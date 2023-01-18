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
import com.alipay.sofa.runtime.spi.component.AbstractComponent;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.Map;

/**
 * Register application context as a component.
 *
 * @author khotyn Created on 2020/10/26
 */
public class SpringContextComponent extends AbstractComponent {

    public static ComponentType SPRING_COMPONENT_TYPE = new ComponentType("Spring");

    public SpringContextComponent(ComponentName componentName, Implementation implementation,
                                  SofaRuntimeContext sofaRuntimeContext) {
        this.implementation = implementation;
        this.componentName = componentName;
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    @Override
    public ComponentType getType() {
        return SPRING_COMPONENT_TYPE;
    }

    @Override
    public Map<String, Property> getProperties() {
        return null;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return (ApplicationContext) implementation.getTarget();
    }

    @Override
    public void activate() throws ServiceRuntimeException {
        if (componentStatus != ComponentStatus.RESOLVED) {
            return;
        }

        componentStatus = ComponentStatus.ACTIVATED;
    }

    @Override
    public void deactivate() throws ServiceRuntimeException {
        if (implementation instanceof SpringContextImplementation) {
            AbstractApplicationContext applicationContext = (AbstractApplicationContext) implementation
                .getTarget();
            applicationContext.close();
        }
        super.deactivate();
    }
}

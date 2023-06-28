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
package com.alipay.sofa.runtime.sample;

import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.component.AbstractComponent;
import com.alipay.sofa.runtime.spi.component.ComponentNameFactory;

import java.util.Map;

/**
 * @author huzijie
 * @version DemoComponent.java, v 0.1 2023年02月02日 3:22 PM huzijie Exp $
 */
public class DemoComponent extends AbstractComponent {

    public static ComponentType DEMO_COMPONENT_TYPE = new ComponentType("Demo");

    private boolean             canBeDuplicate      = true;

    private boolean             registerException   = false;

    private boolean             resolveException    = false;

    private boolean             activateException   = false;

    public DemoComponent() {
        this.componentName = ComponentNameFactory.createComponentName(DEMO_COMPONENT_TYPE, "demo");
    }

    public DemoComponent(String name) {
        this.componentName = ComponentNameFactory.createComponentName(DEMO_COMPONENT_TYPE, name);
    }

    @Override
    public ComponentType getType() {
        return DEMO_COMPONENT_TYPE;
    }

    @Override
    public Map<String, Property> getProperties() {
        return null;
    }

    @Override
    public void register() {
        if (registerException) {
            throw new RuntimeException("register error");
        }
        super.register();
    }

    @Override
    public boolean resolve() {
        if (resolveException) {
            throw new RuntimeException("resolve error");
        }
        return super.resolve();
    }

    @Override
    public void activate() {
        if (activateException) {
            throw new RuntimeException("activate error");
        }
        super.activate();
    }

    @Override
    public boolean canBeDuplicate() {
        return canBeDuplicate;
    }

    public void setCanBeDuplicate(boolean canBeDuplicate) {
        this.canBeDuplicate = canBeDuplicate;
    }

    public void setRegisterException(boolean registerException) {
        this.registerException = registerException;
    }

    public void setResolveException(boolean resolveException) {
        this.resolveException = resolveException;
    }

    public void setActivateException(boolean activateException) {
        this.activateException = activateException;
    }
}

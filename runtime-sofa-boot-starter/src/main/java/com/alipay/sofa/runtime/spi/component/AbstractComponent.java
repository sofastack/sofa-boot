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

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.spi.health.HealthResult;

/**
 * abstract component implementation
 *
 * @author xuanbei 18/3/1
 */
public abstract class AbstractComponent implements ComponentInfo {
    /**
     * component status
     */
    protected ComponentStatus    componentStatus = ComponentStatus.UNREGISTERED;

    protected Implementation     implementation;

    protected ComponentName      componentName;

    protected SofaRuntimeContext sofaRuntimeContext;

    protected Exception          e;

    @Override
    public SofaRuntimeContext getContext() {
        return sofaRuntimeContext;
    }

    @Override
    public ComponentStatus getState() {
        return componentStatus;
    }

    @Override
    public ComponentName getName() {
        return componentName;
    }

    @Override
    public Implementation getImplementation() {
        return implementation;
    }

    @Override
    public boolean isActivated() {
        return componentStatus == ComponentStatus.ACTIVATED;
    }

    @Override
    public boolean isResolved() {
        return componentStatus == ComponentStatus.ACTIVATED
               || componentStatus == ComponentStatus.RESOLVED;
    }

    @Override
    public void register() {
        if (componentStatus != ComponentStatus.UNREGISTERED) {
            return;
        }
        componentStatus = ComponentStatus.REGISTERED;
    }

    @Override
    public void unregister() throws ServiceRuntimeException {
        if (componentStatus == ComponentStatus.UNREGISTERED) {
            return;
        }
        if (componentStatus == ComponentStatus.ACTIVATED
            || componentStatus == ComponentStatus.RESOLVED) {
            unresolve();
        }
        componentStatus = ComponentStatus.UNREGISTERED;
    }

    @Override
    public void unresolve() throws ServiceRuntimeException {
        if (componentStatus == ComponentStatus.REGISTERED
            || componentStatus == ComponentStatus.UNREGISTERED) {
            return;
        }

        if (componentStatus == ComponentStatus.ACTIVATED) {
            deactivate();
        }
        componentStatus = ComponentStatus.REGISTERED;
    }

    @Override
    public boolean resolve() {
        if (componentStatus != ComponentStatus.REGISTERED) {
            return false;
        }

        componentStatus = ComponentStatus.RESOLVED;

        return true;
    }

    @Override
    public void activate() throws ServiceRuntimeException {
        if (componentStatus != ComponentStatus.RESOLVED) {
            return;
        }

        componentStatus = ComponentStatus.ACTIVATED;
    }

    public void exception(Exception e) throws ServiceRuntimeException {
        this.e = e;
    }

    @Override
    public void deactivate() throws ServiceRuntimeException {
        if (componentStatus != ComponentStatus.ACTIVATED) {
            return;
        }

        componentStatus = ComponentStatus.RESOLVED;
    }

    @Override
    public String dump() {
        return componentName.getRawName();
    }

    @Override
    public HealthResult isHealthy() {
        HealthResult healthResult = new HealthResult(componentName.getRawName());
        if (!isActivated()) {
            healthResult.setHealthy(false);
            healthResult.setHealthReport("Status: " + this.getState().toString());
        } else {
            healthResult.setHealthy(true);
        }

        return healthResult;
    }

    @Override
    public boolean canBeDuplicate() {
        return true;
    }
}

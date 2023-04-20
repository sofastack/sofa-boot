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
import com.alipay.sofa.runtime.api.component.ComponentLifeCycle;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract component implementation.
 *
 * @author xuanbei 18/3/1
 */
public abstract class AbstractComponent implements ComponentInfo {
    /**
     * component status
     */
    protected ComponentStatus       componentStatus = ComponentStatus.UNREGISTERED;

    protected Implementation        implementation;

    protected ComponentName         componentName;

    protected SofaRuntimeContext    sofaRuntimeContext;

    protected Exception             e;

    protected ApplicationContext    applicationContext;

    protected Map<String, Property> properties      = new ConcurrentHashMap<>();

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
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
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

        if (this.implementation != null) {
            Object target = this.implementation.getTarget();
            if (target instanceof ComponentLifeCycle) {
                ((ComponentLifeCycle) target).activate();
            }
        }

        componentStatus = ComponentStatus.ACTIVATED;
    }

    @Override
    public void exception(Exception e) throws ServiceRuntimeException {
        this.e = e;
    }

    @Override
    public void deactivate() throws ServiceRuntimeException {
        if (componentStatus != ComponentStatus.ACTIVATED) {
            return;
        }

        if (this.implementation != null) {
            Object target = this.implementation.getTarget();
            if (target instanceof ComponentLifeCycle) {
                ((ComponentLifeCycle) target).deactivate();
            }
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
        } else if (e != null) {
            healthResult.setHealthy(false);
            healthResult.setHealthReport(e.getMessage());
        } else {
            healthResult.setHealthy(true);
        }

        return healthResult;
    }

    protected String aggregateBindingHealth(Collection<Binding> bindings) {
        List<String> healthResult = new ArrayList<>();
        for (Binding binding : bindings) {
            HealthResult result = binding.healthCheck();
            String report = "["
                            + result.getHealthName()
                            + ","
                            + (result.getHealthReport() == null ? (result.isHealthy() ? "passed"
                                : "failed") : result.getHealthReport()) + "]";
            healthResult.add(report);
        }
        return String.join(" ", healthResult);
    }

    @Override
    public boolean canBeDuplicate() {
        return true;
    }

    @Override
    public Map<String, Property> getProperties() {
        return properties;
    }
}

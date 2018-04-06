/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
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
}

package com.alipay.sofa.healthcheck.test;

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.HealthResult;

import java.util.Map;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/6/3
 */
public class TestComponent implements ComponentInfo {
    private String componentName;
    private boolean health;

    public TestComponent(String componentName, boolean health) {
        this.componentName = componentName;
        this.health = health;
    }

    @Override
    public HealthResult isHealthy() {
        HealthResult rtn = new HealthResult(componentName);
        rtn.setHealthy(health);
        return rtn;
    }

    @Override
    public ComponentName getName() {
        return new ComponentName(new ComponentType("TEST"), componentName);
    }

    @Override
    public ComponentType getType() {
        return null;
    }

    @Override
    public Implementation getImplementation() {
        return null;
    }

    @Override
    public SofaRuntimeContext getContext() {
        return null;
    }

    @Override
    public Map<String, Property> getProperties() {
        return null;
    }

    @Override
    public ComponentStatus getState() {
        return null;
    }

    @Override
    public boolean isActivated() {
        return false;
    }

    @Override
    public boolean isResolved() {
        return false;
    }

    @Override
    public String dump() {
        return null;
    }

    @Override
    public boolean canBeDuplicate() {
        return false;
    }

    @Override
    public void register() {

    }

    @Override
    public void unregister() throws ServiceRuntimeException {

    }

    @Override
    public boolean resolve() {
        return false;
    }

    @Override
    public void unresolve() throws ServiceRuntimeException {

    }

    @Override
    public void activate() throws ServiceRuntimeException {

    }

    @Override
    public void deactivate() throws ServiceRuntimeException {

    }

    @Override
    public void exception(Exception e) throws ServiceRuntimeException {

    }
}

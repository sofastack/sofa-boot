package com.alipay.sofa.runtime.spi.component;

import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.SofaFramework;
import com.alipay.sofa.runtime.spi.SofaFrameworkHolder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

/**
 * @author xuanbei 18/4/3
 */
public class AbstractComponentTest {

    private AbstractComponent   abstractComponent = new AbstractComponent() {
        @Override
        public ComponentType getType() {
            return null;
        }

        @Override
        public Map<String, Property> getProperties() {
            return null;
        }
    };
    private MockSofaFramework   sofaFramework     = new MockSofaFramework();

    public static class MockSofaFramework implements SofaFramework {

        @Override
        public SofaRuntimeContext getSofaRuntimeContext(String appName) {
            return null;
        }

        @Override
        public SofaRuntimeManager getSofaRuntimeManager(String appName) {
            return null;
        }

        @Override
        public void removeSofaRuntimeManager(String appName) {

        }

        @Override
        public Set<String> getSofaFrameworkAppNames() {
            return null;
        }
    }

    @Before
    public void setUp() {
        abstractComponent.implementation = new DefaultImplementation();
        abstractComponent.implementation.setTarget(new Object());
        SofaFrameworkHolder.setSofaFramework(sofaFramework);
    }

    @Test
    public void testActivate() throws Exception {
        abstractComponent.register();
        Assert.assertEquals(abstractComponent.getState(), ComponentStatus.REGISTERED);
        abstractComponent.resolve();
        Assert.assertEquals(abstractComponent.getState(), ComponentStatus.RESOLVED);
        abstractComponent.activate();
        Assert.assertEquals(abstractComponent.getState(), ComponentStatus.ACTIVATED);
        abstractComponent.deactivate();
        Assert.assertEquals(abstractComponent.getState(), ComponentStatus.RESOLVED);
    }
}
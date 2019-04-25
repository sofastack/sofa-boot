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

import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.ComponentStatus;
import com.alipay.sofa.runtime.model.ComponentType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author xuanbei 18/5/11
 */
public class AbstractComponentTest {
    private AbstractComponent abstractComponent = new AbstractComponent() {
                                                    @Override
                                                    public ComponentType getType() {
                                                        return null;
                                                    }

                                                    @Override
                                                    public Map<String, Property> getProperties() {
                                                        return null;
                                                    }
                                                };

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

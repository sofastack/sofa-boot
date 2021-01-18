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
package com.alipay.sofa.runtime.test;

import com.alipay.sofa.runtime.api.ReferenceRegisterHook;
import com.alipay.sofa.runtime.api.ServiceRegisterHook;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import com.alipay.sofa.runtime.service.helper.ReferenceRegisterHelper;
import com.alipay.sofa.runtime.service.helper.ServiceRegisterHelper;
import com.alipay.sofa.runtime.spi.binding.AbstractBinding;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.DefaultImplementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2021/1/14
 */
@RunWith(MockitoJUnitRunner.class)
public class RegisterHelperTest {
    @Mock
    private SofaRuntimeContext sofaRuntimeContext;

    @Mock
    private ComponentManager   componentManager;

    @Mock
    private ComponentInfo      componentInfo;

    @Before
    public void before() {
        Mockito.when(sofaRuntimeContext.getComponentManager()).thenReturn(componentManager);
        Mockito.when(sofaRuntimeContext.getAppClassLoader()).thenReturn(
            this.getClass().getClassLoader());
        Mockito.when(componentInfo.getImplementation()).thenReturn(
            new DefaultImplementation(new Object()));
        Mockito.when(componentManager.registerAndGet(Mockito.any())).thenReturn(componentInfo);
    }

    @Test
    public void serviceTest() {
        Service service = new ServiceImpl("", ServiceRegisterHook.class, null);
        ServiceRegisterHelper.registerService(service, null, null, sofaRuntimeContext);
        Assert.assertEquals(1, service.getBindings().size());
        Assert.assertEquals(JvmBinding.JVM_BINDING_TYPE,
            ((Binding) service.getBindings().toArray()[0]).getBindingType());
    }

    @Test
    public void referenceTest() {
        Reference reference = new ReferenceImpl("", ReferenceRegisterHook.class,
            InterfaceMode.spring, true);
        reference.addBinding(new AbstractBinding() {
            @Override
            public String getURI() {
                return null;
            }

            @Override
            public BindingType getBindingType() {
                return new BindingType("Other");
            }

            @Override
            public Element getBindingPropertyContent() {
                return null;
            }

            @Override
            public int getBindingHashCode() {
                return 0;
            }

            @Override
            public HealthResult healthCheck() {
                return null;
            }
        });
        ReferenceRegisterHelper.registerReference(reference, null, sofaRuntimeContext);
        Assert.assertEquals(2, reference.getBindings().size());
        boolean containsJvmBinding = false;
        for (Binding binding: reference.getBindings()) {
            if (JvmBinding.JVM_BINDING_TYPE.equals(binding.getBindingType())) {
                containsJvmBinding = true;
                break;
            }
        }
        Assert.assertTrue(containsJvmBinding);
    }
}

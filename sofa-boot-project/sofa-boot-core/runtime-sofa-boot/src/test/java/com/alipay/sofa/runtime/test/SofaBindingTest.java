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

import java.util.Collection;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.test.beans.facade.SampleService;
import com.alipay.sofa.runtime.test.beans.service.DefaultSampleService;
import com.alipay.sofa.runtime.test.configuration.RuntimeConfiguration;
import com.alipay.sofa.runtime.test.util.ComponentNameUtil;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = "spring.application.name=SofaFactoryBeanTest")
public class SofaBindingTest {

    @Autowired
    private SofaRuntimeContext sofaRuntimeContext;

    @Test
    public void testServiceBinding() {
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        ServiceComponent serializeFalseViaAnnotation = (ServiceComponent) componentManager
            .getComponentInfo(ComponentNameUtil.getServiceComponentName(SampleService.class,
                "serializeFalseViaAnnotation"));
        ServiceComponent defaultSerializeTrueViaAnnotation = (ServiceComponent) componentManager
            .getComponentInfo(ComponentNameUtil.getServiceComponentName(SampleService.class,
                "defaultSerializeTrueViaAnnotation"));
        ServiceComponent defaultElement = (ServiceComponent) componentManager
            .getComponentInfo(ComponentNameUtil.getServiceComponentName(SampleService.class,
                "default-element"));
        ServiceComponent element = (ServiceComponent) componentManager
            .getComponentInfo(ComponentNameUtil.getServiceComponentName(SampleService.class,
                "element"));
        ServiceComponent noneUniqueId = (ServiceComponent) componentManager
            .getComponentInfo(ComponentNameUtil.getServiceComponentName(SampleService.class, ""));

        Assert.assertNotNull(serializeFalseViaAnnotation);
        Assert.assertNotNull(defaultSerializeTrueViaAnnotation);
        Assert.assertNotNull(defaultElement);
        Assert.assertNotNull(element);
        Assert.assertNotNull(noneUniqueId);

        JvmBinding jvmBinding;
        jvmBinding = (JvmBinding) serializeFalseViaAnnotation.getService().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        Assert.assertFalse(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) defaultSerializeTrueViaAnnotation.getService().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        Assert.assertTrue(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) defaultElement.getService().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        Assert.assertTrue(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) element.getService().getBinding(JvmBinding.JVM_BINDING_TYPE);
        Assert.assertFalse(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) noneUniqueId.getService().getBinding(JvmBinding.JVM_BINDING_TYPE);
        Assert.assertTrue(jvmBinding.getJvmBindingParam().isSerialize());
    }

    @Test
    public void testReferenceBinding() {
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        ReferenceComponent serializeTrueViaAnnotation = null;
        ReferenceComponent defaultSerializeFalseViaAnnotation = null;
        ReferenceComponent defaultElement = null;
        ReferenceComponent element = null;
        ReferenceComponent noneUniqueId = null;
        Collection<ComponentInfo> componentInfos = componentManager
            .getComponentInfosByType(ReferenceComponent.REFERENCE_COMPONENT_TYPE);
        for (ComponentInfo componentInfo : componentInfos) {
            String rawName = componentInfo.getName().getRawName();
            if (rawName.contains(ComponentNameUtil.getReferenceComponentName(SampleService.class,
                "serializeTrueViaAnnotation").getRawName())) {
                serializeTrueViaAnnotation = (ReferenceComponent) componentInfo;
            } else if (rawName.contains(ComponentNameUtil.getReferenceComponentName(
                SampleService.class, "defaultSerializeFalseViaAnnotation").getRawName())) {
                defaultSerializeFalseViaAnnotation = (ReferenceComponent) componentInfo;
            } else if (rawName.contains(ComponentNameUtil.getReferenceComponentName(
                SampleService.class, "default-element").getRawName())) {
                defaultElement = (ReferenceComponent) componentInfo;
            } else if (componentInfo
                .getName()
                .getRawName()
                .contains(
                    ComponentNameUtil.getReferenceComponentName(SampleService.class, "element")
                        .getRawName())) {
                element = (ReferenceComponent) componentInfo;
            } else if (rawName.contains(":#")
                       && rawName.contains(ComponentNameUtil.getReferenceComponentName(
                           SampleService.class, "").getRawName())) {
                noneUniqueId = (ReferenceComponent) componentInfo;
            }
        }
        Assert.assertNotNull(serializeTrueViaAnnotation);
        Assert.assertNotNull(defaultSerializeFalseViaAnnotation);
        Assert.assertNotNull(defaultElement);
        Assert.assertNotNull(element);
        Assert.assertNotNull(noneUniqueId);

        JvmBinding jvmBinding;
        jvmBinding = (JvmBinding) serializeTrueViaAnnotation.getReference().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        Assert.assertTrue(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) defaultSerializeFalseViaAnnotation.getReference().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        Assert.assertFalse(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) defaultElement.getReference().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        Assert.assertFalse(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) element.getReference().getBinding(JvmBinding.JVM_BINDING_TYPE);
        Assert.assertTrue(jvmBinding.getJvmBindingParam().isSerialize());

        jvmBinding = (JvmBinding) noneUniqueId.getReference().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        Assert.assertFalse(jvmBinding.getJvmBindingParam().isSerialize());
    }

    @Test
    public void testBindingType() {
        Assert.assertTrue(new BindingType("jvm").equals(new BindingType("jvm")));
        Assert.assertFalse(new BindingType("jvm").equals(new BindingType("bolt")));
        Assert.assertFalse(new BindingType("jvm").equals(null));
        Assert.assertFalse(new BindingType("jvm").equals("jvm"));

        HashMap<BindingType, String> map = new HashMap<>();
        map.put(new BindingType("jvm"), "jvm");
        map.put(new BindingType("bolt"), "bolt");
        map.put(new BindingType("rest"), "rest");

        Assert.assertEquals(map.get(new BindingType("jvm")), "jvm");
        Assert.assertEquals(map.get(new BindingType("bolt")), "bolt");
        Assert.assertEquals(map.get(new BindingType("rest")), "rest");
        Assert.assertEquals(map.get("jvm"), null);
        Assert.assertEquals(map.get(null), null);
    }

    @Configuration
    @Import(RuntimeConfiguration.class)
    @ImportResource("classpath*:META-INF/service/test-service.xml")
    static class SofaBindingTestConfiguration {
        @Bean
        @SofaService(uniqueId = "serializeFalseViaAnnotation", bindings = @SofaServiceBinding(serialize = false))
        public SampleService service1(@SofaReference(uniqueId = "defaultSerializeFalseViaAnnotation") SampleService service) {
            return new DefaultSampleService();
        }

        @Bean
        @SofaService(uniqueId = "defaultSerializeTrueViaAnnotation")
        public SampleService service2(@SofaReference(uniqueId = "serializeTrueViaAnnotation", binding = @SofaReferenceBinding(serialize = true)) SampleService service) {
            return new DefaultSampleService();
        }
    }
}
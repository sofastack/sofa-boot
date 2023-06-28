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
package com.alipay.sofa.smoke.tests.runtime.spi.binding;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.binding.JvmBindingInterface;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.ComponentNameFactory;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.smoke.tests.runtime.RuntimeSofaBootApplication;
import com.alipay.sofa.smoke.tests.runtime.impl.SampleServiceImpl;
import com.alipay.sofa.smoke.tests.runtime.service.SampleNoService;
import com.alipay.sofa.smoke.tests.runtime.service.SampleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import java.util.Collection;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link Binding}.
 * 
 * @author qilong.zql
 * @since 3.2.0
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class)
@Import(SofaBindingTests.SofaBindingTestConfiguration.class)
public class SofaBindingTests {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private SofaRuntimeContext sofaRuntimeContext;

    @Test
    public void jvmBindingInterface() {
        // Jdk 代理类添加接口
        assertThat(ctx.getBean("reference1") instanceof JvmBindingInterface).isTrue();

        // Cglib 代理类不受影响
        assertThat(ctx.getBean("reference4") instanceof JvmBindingInterface).isFalse();
        assertThat(ctx.getBean("reference4") instanceof SampleNoService).isTrue();
    }

    @Test
    public void serviceBinding() {
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        ServiceComponent serializeFalseViaAnnotation = (ServiceComponent) componentManager
            .getComponentInfo(getServiceComponentName(SampleService.class,
                "serializeFalseViaAnnotation"));
        ServiceComponent defaultSerializeTrueViaAnnotation = (ServiceComponent) componentManager
            .getComponentInfo(getServiceComponentName(SampleService.class,
                "defaultSerializeTrueViaAnnotation"));
        ServiceComponent defaultElement = (ServiceComponent) componentManager
            .getComponentInfo(getServiceComponentName(SampleService.class, "default-element"));
        ServiceComponent element = (ServiceComponent) componentManager
            .getComponentInfo(getServiceComponentName(SampleService.class, "element"));
        ServiceComponent noneUniqueId = (ServiceComponent) componentManager
            .getComponentInfo(getServiceComponentName(SampleService.class, ""));

        assertThat(serializeFalseViaAnnotation).isNotNull();
        assertThat(defaultSerializeTrueViaAnnotation).isNotNull();
        assertThat(defaultElement).isNotNull();
        assertThat(element).isNotNull();
        assertThat(noneUniqueId).isNotNull();

        JvmBinding jvmBinding;
        jvmBinding = (JvmBinding) serializeFalseViaAnnotation.getService().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        assertThat(jvmBinding.getJvmBindingParam().isSerialize()).isFalse();

        jvmBinding = (JvmBinding) defaultSerializeTrueViaAnnotation.getService().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        assertThat(jvmBinding.getJvmBindingParam().isSerialize()).isTrue();

        jvmBinding = (JvmBinding) defaultElement.getService().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        assertThat(jvmBinding.getJvmBindingParam().isSerialize()).isTrue();

        jvmBinding = (JvmBinding) element.getService().getBinding(JvmBinding.JVM_BINDING_TYPE);
        assertThat(jvmBinding.getJvmBindingParam().isSerialize()).isFalse();

        jvmBinding = (JvmBinding) noneUniqueId.getService().getBinding(JvmBinding.JVM_BINDING_TYPE);
        assertThat(jvmBinding.getJvmBindingParam().isSerialize()).isTrue();
    }

    @Test
    public void referenceBinding() {
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
            if (rawName.contains("serializeTrueViaAnnotation")) {
                serializeTrueViaAnnotation = (ReferenceComponent) componentInfo;
            } else if (rawName.contains("defaultSerializeFalseViaAnnotation")) {
                defaultSerializeFalseViaAnnotation = (ReferenceComponent) componentInfo;
            } else if (rawName.contains("default-element")) {
                defaultElement = (ReferenceComponent) componentInfo;
            } else if (componentInfo.getName().getRawName().contains("element")) {
                element = (ReferenceComponent) componentInfo;
            } else if (rawName.contains(":#") && rawName.contains("")) {
                noneUniqueId = (ReferenceComponent) componentInfo;
            }
        }
        assertThat(serializeTrueViaAnnotation).isNotNull();
        assertThat(defaultSerializeFalseViaAnnotation).isNotNull();
        assertThat(defaultElement).isNotNull();
        assertThat(element).isNotNull();
        assertThat(noneUniqueId).isNotNull();

        JvmBinding jvmBinding;
        jvmBinding = (JvmBinding) serializeTrueViaAnnotation.getReference().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        assertThat(jvmBinding.getJvmBindingParam().isSerialize()).isTrue();

        jvmBinding = (JvmBinding) defaultSerializeFalseViaAnnotation.getReference().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        assertThat(jvmBinding.getJvmBindingParam().isSerialize()).isFalse();

        jvmBinding = (JvmBinding) defaultElement.getReference().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        assertThat(jvmBinding.getJvmBindingParam().isSerialize()).isFalse();

        jvmBinding = (JvmBinding) element.getReference().getBinding(JvmBinding.JVM_BINDING_TYPE);
        assertThat(jvmBinding.getJvmBindingParam().isSerialize()).isTrue();

        jvmBinding = (JvmBinding) noneUniqueId.getReference().getBinding(
            JvmBinding.JVM_BINDING_TYPE);
        assertThat(jvmBinding.getJvmBindingParam().isSerialize()).isFalse();
    }

    @Test
    public void bindingType() {
        assertThat(new BindingType("jvm").equals(new BindingType("jvm"))).isTrue();
        assertThat(new BindingType("jvm").equals(new BindingType("bolt"))).isFalse();

        HashMap<BindingType, String> map = new HashMap<>();
        map.put(new BindingType("jvm"), "jvm");
        map.put(new BindingType("bolt"), "bolt");
        map.put(new BindingType("rest"), "rest");

        assertThat(map.get(new BindingType("jvm"))).isEqualTo("jvm");
        assertThat(map.get(new BindingType("bolt"))).isEqualTo("bolt");
        assertThat(map.get(new BindingType("rest"))).isEqualTo("rest");
        assertThat(map.get("jvm")).isEqualTo(null);
        assertThat(map.get(null)).isEqualTo(null);
    }

    @TestConfiguration
    @ImportResource("classpath*:spring/service/test-service.xml")
    static class SofaBindingTestConfiguration {

        @SofaReference(uniqueId = "defaultSerializeFalseViaAnnotation")
        private SampleService defaultSerializeFalseViaAnnotation;

        @SofaReference(uniqueId = "serializeTrueViaAnnotation", binding = @SofaReferenceBinding(serialize = true))
        private SampleService serializeTrueViaAnnotation;

        @Bean
        @SofaService(uniqueId = "serializeFalseViaAnnotation", bindings = @SofaServiceBinding(serialize = false))
        public SampleService service1() {
            return new SampleServiceImpl();
        }

        @Bean
        @SofaService(uniqueId = "defaultSerializeTrueViaAnnotation")
        public SampleService service2() {
            return new SampleServiceImpl();
        }
    }

    private ComponentName getServiceComponentName(Class clazz, String uniqueId) {
        return ComponentNameFactory.createComponentName(ServiceComponent.SERVICE_COMPONENT_TYPE,
            clazz, uniqueId);
    }
}
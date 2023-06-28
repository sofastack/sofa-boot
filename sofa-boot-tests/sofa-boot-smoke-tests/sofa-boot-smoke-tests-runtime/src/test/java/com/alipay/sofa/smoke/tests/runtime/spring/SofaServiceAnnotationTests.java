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
package com.alipay.sofa.smoke.tests.runtime.spring;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import com.alipay.sofa.runtime.spi.component.ComponentNameFactory;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spring.bean.SofaBeanNameGenerator;
import com.alipay.sofa.smoke.tests.runtime.RuntimeSofaBootApplication;
import com.alipay.sofa.smoke.tests.runtime.service.SampleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SofaService}.
 *
 * @author huzijie
 * @version SofaServiceAnnotationTests.java, v 0.1 2023年02月22日 11:21 AM huzijie Exp $
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class)
@Import(SofaServiceAnnotationTests.ServiceBeanAnnotationConfiguration.class)
@TestPropertySource(properties = { "methodUniqueId=a", "bindingType=jvm" })
public class SofaServiceAnnotationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SofaRuntimeManager sofaRuntimeManager;

    @Test
    public void checkFactoryBean() {
        String beanName = SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class,
            "a", "methodSampleService");
        assertThat(applicationContext.containsBean(beanName)).isTrue();
        assertThat(applicationContext.getBean(beanName)).isInstanceOf(ServiceImpl.class);

        beanName = SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class, null,
            "classSampleService");
        assertThat(applicationContext.containsBean(beanName)).isTrue();
        assertThat(applicationContext.getBean(beanName)).isInstanceOf(ServiceImpl.class);
    }

    @Test
    public void checkServiceComponent() {
        ComponentName componentName = ComponentNameFactory.createComponentName(
            ServiceComponent.SERVICE_COMPONENT_TYPE, SampleService.class, "a");
        assertThat(sofaRuntimeManager.getComponentManager().getComponentInfo(componentName))
            .isNotNull();

        componentName = ComponentNameFactory.createComponentName(
            ServiceComponent.SERVICE_COMPONENT_TYPE, SampleService.class, null);
        assertThat(sofaRuntimeManager.getComponentManager().getComponentInfo(componentName))
            .isNotNull();
    }

    @Configuration
    @Import(ClassSampleService.class)
    static class ServiceBeanAnnotationConfiguration {

        @SofaService(uniqueId = "${methodUniqueId}")
        @Bean
        public SampleService methodSampleService() {
            return () -> "methodSampleService";
        }
    }

    @SofaService(bindings = @SofaServiceBinding(bindingType = "${bindingType}"))
    @Component("classSampleService")
    static class ClassSampleService implements SampleService {

        @Override
        public String service() {
            return "classSampleService";
        }
    }
}

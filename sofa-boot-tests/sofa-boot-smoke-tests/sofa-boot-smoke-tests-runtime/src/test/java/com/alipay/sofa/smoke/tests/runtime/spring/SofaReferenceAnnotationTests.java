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

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
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
import org.springframework.test.context.TestPropertySource;

import static com.alipay.sofa.runtime.service.component.ReferenceComponent.REFERENCE_COMPONENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SofaReference}.
 *
 * @author huzijie
 * @version SofaReferenceAnnotationTests.java, v 0.1 2023年02月22日 11:39 AM huzijie Exp $
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class)
@Import(SofaReferenceAnnotationTests.ReferenceBeanAnnotationConfiguration.class)
@TestPropertySource(properties = { "uniqueIdB=b", "uniqueIdC=c", "uniqueIdD=d", "bindingType=jvm" })
public class SofaReferenceAnnotationTests {

    @Autowired
    private TestSofaReferenceBean testSofaReferenceBean;

    @Autowired
    private ApplicationContext    applicationContext;

    @Autowired
    private SofaRuntimeManager    sofaRuntimeManager;

    @Test
    public void checkFieldInject() {
        SampleService sampleServiceA = testSofaReferenceBean.getSampleServiceA();
        SampleService sampleServiceB = testSofaReferenceBean.getSampleServiceB();
        SampleService sampleServiceC = testSofaReferenceBean.getSampleServiceC();
        SampleService sampleServiceD = testSofaReferenceBean.getSampleServiceD();

        assertThat(sampleServiceA).isNotNull();
        assertThat(sampleServiceB).isNotNull();
        assertThat(sampleServiceC).isNotNull();
        assertThat(sampleServiceD).isNotNull();
    }

    @Test
    public void checkFactoryBean() {
        assertThat(
            applicationContext.containsBean(SofaBeanNameGenerator.generateSofaReferenceBeanName(
                SampleService.class, null))).isFalse();
        assertThat(
            applicationContext.containsBean(SofaBeanNameGenerator.generateSofaReferenceBeanName(
                SampleService.class, "b"))).isFalse();
        assertThat(
            applicationContext.containsBean(SofaBeanNameGenerator.generateSofaReferenceBeanName(
                SampleService.class, "d"))).isFalse();
        assertThat(
            applicationContext.containsBean(SofaBeanNameGenerator.generateSofaReferenceBeanName(
                SampleService.class, "c"))).isTrue();
        assertThat(
            applicationContext.getBean(SofaBeanNameGenerator.generateSofaReferenceBeanName(
                SampleService.class, "c"))).isInstanceOf(SampleService.class);
    }

    @Test
    public void checkReferenceComponent() {
        assertThat(
            sofaRuntimeManager.getComponentManager()
                .getComponentInfosByType(REFERENCE_COMPONENT_TYPE).size()).isEqualTo(4);
    }

    @Configuration
    static class ReferenceBeanAnnotationConfiguration {

        @Bean
        public TestSofaReferenceBean testSofaReferenceBean(@SofaReference(uniqueId = "${uniqueIdC}") SampleService sampleService) {
            return new TestSofaReferenceBean(sampleService);
        }
    }

    public static class TestSofaReferenceBean {

        @SofaReference(binding = @SofaReferenceBinding(bindingType = "${bindingType}"))
        private SampleService       sampleServiceA;

        @SofaReference(uniqueId = "${uniqueIdB}")
        private SampleService       sampleServiceB;

        private final SampleService sampleServiceC;

        private SampleService       sampleServiceD;

        public TestSofaReferenceBean(SampleService sampleService) {
            sampleServiceC = sampleService;
        }

        @SofaReference(uniqueId = "${uniqueIdD}")
        public void setSampleServiceD(SampleService sampleService) {
            this.sampleServiceD = sampleService;
        }

        public SampleService getSampleServiceA() {
            return sampleServiceA;
        }

        public SampleService getSampleServiceB() {
            return sampleServiceB;
        }

        public SampleService getSampleServiceC() {
            return sampleServiceC;
        }

        public SampleService getSampleServiceD() {
            return sampleServiceD;
        }
    }

}

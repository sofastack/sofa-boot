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
package com.alipay.sofa.test.mock.injector.definition;

import com.alipay.sofa.test.mock.injector.annotation.MockBeanInjector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

/**
 * Tests for {@link QualifierDefinition}.
 *
 * @author huzijie
 * @version QualifierDefinitionTests.java, v 0.1 2023年08月21日 3:30 PM huzijie Exp $
 */
@RunWith(MockitoJUnitRunner.class)
public class QualifierDefinitionTests {

    @Mock
    private ConfigurableListableBeanFactory beanFactory;

    @Test
    public void forElementFieldIsNullShouldReturnNull() {
        assertThat(QualifierDefinition.forElement((Field) null)).isNull();
    }

    @Test
    public void forElementWhenElementIsNotFieldShouldReturnNull() {
        assertThat(QualifierDefinition.forElement(getClass())).isNull();
    }

    @Test
    public void forElementWhenElementIsFieldWithNoQualifiersShouldReturnNull() {
        QualifierDefinition definition = QualifierDefinition.forElement(ReflectionUtils.findField(
            ConfigA.class, "noQualifier"));
        assertThat(definition).isNull();
    }

    @Test
    public void forElementWhenElementIsFieldWithQualifierShouldReturnDefinition() {
        QualifierDefinition definition = QualifierDefinition.forElement(ReflectionUtils.findField(
            ConfigA.class, "directQualifier"));
        assertThat(definition).isNotNull();
    }

    @Test
    public void matchesShouldCallBeanFactory() {
		Field field = ReflectionUtils.findField(ConfigA.class, "directQualifier");
		QualifierDefinition qualifierDefinition = QualifierDefinition.forElement(field);
		qualifierDefinition.matches(this.beanFactory, "bean");
		then(this.beanFactory).should()
			.isAutowireCandidate(eq("bean"), argThat(
					(dependencyDescriptor) -> {
						assertThat(dependencyDescriptor.getAnnotatedElement()).isEqualTo(field);
						return true;
					}));
	}

    @Test
    public void applyToShouldSetQualifierElement() {
        Field field = ReflectionUtils.findField(ConfigA.class, "directQualifier");
        QualifierDefinition qualifierDefinition = QualifierDefinition.forElement(field);
        RootBeanDefinition definition = new RootBeanDefinition();
        qualifierDefinition.applyTo(definition);
        assertThat(definition.getQualifiedElement()).isEqualTo(field);
    }

    @Test
    public void hashCodeAndEqualsShouldWorkOnDifferentClasses() {
        QualifierDefinition directQualifier1 = QualifierDefinition.forElement(ReflectionUtils
            .findField(ConfigA.class, "directQualifier"));
        QualifierDefinition directQualifier2 = QualifierDefinition.forElement(ReflectionUtils
            .findField(ConfigB.class, "directQualifier"));
        QualifierDefinition differentDirectQualifier1 = QualifierDefinition
            .forElement(ReflectionUtils.findField(ConfigA.class, "differentDirectQualifier"));
        QualifierDefinition differentDirectQualifier2 = QualifierDefinition
            .forElement(ReflectionUtils.findField(ConfigB.class, "differentDirectQualifier"));
        QualifierDefinition customQualifier1 = QualifierDefinition.forElement(ReflectionUtils
            .findField(ConfigA.class, "customQualifier"));
        QualifierDefinition customQualifier2 = QualifierDefinition.forElement(ReflectionUtils
            .findField(ConfigB.class, "customQualifier"));
        assertThat(directQualifier1).hasSameHashCodeAs(directQualifier2);
        assertThat(differentDirectQualifier1).hasSameHashCodeAs(differentDirectQualifier2);
        assertThat(customQualifier1).hasSameHashCodeAs(customQualifier2);
        assertThat(differentDirectQualifier1).isEqualTo(differentDirectQualifier1)
            .isEqualTo(differentDirectQualifier2).isNotEqualTo(directQualifier2);
        assertThat(directQualifier1).isEqualTo(directQualifier1).isEqualTo(directQualifier2)
            .isNotEqualTo(differentDirectQualifier1);
        assertThat(customQualifier1).isEqualTo(customQualifier1).isEqualTo(customQualifier2)
            .isNotEqualTo(differentDirectQualifier1);
    }

    @Configuration(proxyBeanMethods = false)
    static class ConfigA {

        @MockBeanInjector(field = "Field")
        private Object noQualifier;

        @MockBeanInjector(field = "Field")
        @Qualifier("test")
        private Object directQualifier;

        @MockBeanInjector(field = "Field")
        @Qualifier("different")
        private Object differentDirectQualifier;

        @MockBeanInjector(field = "Field")
        @CustomQualifier
        private Object customQualifier;

    }

    static class ConfigB {

        @MockBeanInjector(field = "Field")
        @Qualifier("test")
        private Object directQualifier;

        @MockBeanInjector(field = "Field")
        @Qualifier("different")
        private Object differentDirectQualifier;

        @MockBeanInjector(field = "Field")
        @CustomQualifier
        private Object customQualifier;

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface CustomQualifier {

    }

}

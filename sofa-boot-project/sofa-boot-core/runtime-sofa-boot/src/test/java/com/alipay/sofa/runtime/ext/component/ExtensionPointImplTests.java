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
package com.alipay.sofa.runtime.ext.component;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.FilteredClassLoader;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ExtensionPointImpl}.
 *
 * @author huzijie
 * @version ExtensionPointImplTests.java, v 0.1 2023年04月10日 2:07 PM huzijie Exp $
 */
public class ExtensionPointImplTests {

    private final ClassLoader mockClassLoader = new FilteredClassLoader("ext");

    @Test
    void constructor_shouldInitializeFields() {
        // given
        String name = "testExtensionPoint";
        Class<?> contributionClass = String.class;

        // when
        ExtensionPointImpl extensionPoint = new ExtensionPointImpl(name, contributionClass);

        // then
        assertThat(extensionPoint.name).isEqualTo(name);
        assertThat(extensionPoint.contributions).containsExactly(contributionClass);
    }

    @Test
    void setBeanClassLoaderShouldSetClassLoader() {
        // given
        ExtensionPointImpl extensionPoint = new ExtensionPointImpl("testExtensionPoint", null);

        // when
        extensionPoint.setBeanClassLoader(mockClassLoader);

        // then
        assertThat(extensionPoint.beanClassLoader).isEqualTo(mockClassLoader);
    }

    @Test
    void getContributionsShouldReturnContributions() {
        // given
        ExtensionPointImpl extensionPoint = new ExtensionPointImpl("testExtensionPoint", null);
        List<Class<?>> contributions = List.of(String.class, Integer.class);
        extensionPoint.contributions = contributions;

        // when
        List<Class<?>> result = extensionPoint.getContributions();

        // then
        assertThat(result).isEqualTo(contributions);
    }

    @Test
    void hasContributionShouldReturnTrueIfContributionsExist() {
        // given
        ExtensionPointImpl extensionPoint = new ExtensionPointImpl("testExtensionPoint", null);
        extensionPoint.contributions = Collections.singletonList(String.class);

        // when
        boolean result = extensionPoint.hasContribution();

        // then
        assertThat(result).isTrue();
    }

    @Test
    void hasContributionShouldReturnFalseIfNoContributionsExist() {
        // given
        ExtensionPointImpl extensionPoint = new ExtensionPointImpl("testExtensionPoint", null);

        // when
        boolean result = extensionPoint.hasContribution();

        // then
        assertThat(result).isFalse();
    }

    @Test
    void getNameShouldReturnName() {
        // given
        String name = "testExtensionPoint";
        ExtensionPointImpl extensionPoint = new ExtensionPointImpl(name, null);

        // when
        String result = extensionPoint.getName();

        // then
        assertThat(result).isEqualTo(name);
    }

    @Test
    void getDocumentationShouldReturnDocumentation() {
        // given
        String documentation = "testDocumentation";
        ExtensionPointImpl extensionPoint = new ExtensionPointImpl("testExtensionPoint", null);
        extensionPoint.documentation = documentation;

        // when
        String result = extensionPoint.getDocumentation();

        // then
        assertThat(result).isEqualTo(documentation);
    }

    @Test
    void addContributionShouldAddContribution() {
        // given
        ExtensionPointImpl extensionPoint = new ExtensionPointImpl("testExtensionPoint", null);
        Class<?> contributionClass1 = String.class;
        Class<?> contributionClass2 = Integer.class;

        // when
        extensionPoint.addContribution(contributionClass1);
        extensionPoint.addContribution(contributionClass2);

        // then
        assertThat(extensionPoint.contributions).containsExactly(contributionClass1,
            contributionClass2);
    }

    @Test
    void addContributionShouldResolveClassNameAndAddContribution() throws ClassNotFoundException {
        // given
        ExtensionPointImpl extensionPoint = new ExtensionPointImpl("testExtensionPoint", null);
        String className = "java.lang.String";

        // when
        extensionPoint.addContribution(className);

        // then
        assertThat(extensionPoint.contributions).containsExactly(String.class);
    }
}

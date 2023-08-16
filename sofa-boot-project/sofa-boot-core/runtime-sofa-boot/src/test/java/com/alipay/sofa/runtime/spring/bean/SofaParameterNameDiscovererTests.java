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
package com.alipay.sofa.runtime.spring.bean;

import com.alipay.sofa.boot.annotation.AnnotationWrapper;
import com.alipay.sofa.boot.annotation.DefaultPlaceHolderBinder;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaParameterNameDiscoverer}.
 *
 * @author huzijie
 * @version SofaParameterNameDiscovererTests.java, v 0.1 2023年04月10日 5:00 PM huzijie Exp $
 */
public class SofaParameterNameDiscovererTests {

    private SofaParameterNameDiscoverer      parameterNameDiscoverer;

    private AnnotationWrapper<SofaReference> annotationWrapper;

    private MockEnvironment                  mockEnvironment;

    @BeforeEach
    public void setUp() {
        mockEnvironment = new MockEnvironment();
        annotationWrapper = AnnotationWrapper.create(SofaReference.class)
            .withEnvironment(mockEnvironment).withBinder(DefaultPlaceHolderBinder.INSTANCE);
        parameterNameDiscoverer = new SofaParameterNameDiscoverer(
            new DefaultParameterNameDiscoverer(), annotationWrapper);
    }

    @Test
    public void getParameterNamesOnMethods() {
        Method method = ReflectionUtils.findMethod(SofaReferenceOnMethod.class, "hello",
            String.class);
        String[] parameters = parameterNameDiscoverer.getParameterNames(method);
        assertThat(parameters).hasSize(1);
        assertThat(parameters[0]).isEqualTo("ReferenceFactoryBean#java.lang.String:a");
    }

    @Test
    public void getParameterNamesOnConstructs() throws NoSuchMethodException {
        Constructor<SofaReferenceOnConstructs> constructor = ReflectionUtils.accessibleConstructor(
            SofaReferenceOnConstructs.class, String.class);
        String[] parameters = parameterNameDiscoverer.getParameterNames(constructor);
        assertThat(parameters).hasSize(1);
        assertThat(parameters[0]).isEqualTo("ReferenceFactoryBean#java.lang.String:a");
    }

    @Test
    public void getParameterNamesOnConstruct() {

    }

    static class SofaReferenceOnMethod {

        public void hello(@SofaReference(uniqueId = "a") String b) {

        }
    }

    static class SofaReferenceOnConstructs {

        public SofaReferenceOnConstructs(@SofaReference(uniqueId = "a") String b) {

        }
    }
}

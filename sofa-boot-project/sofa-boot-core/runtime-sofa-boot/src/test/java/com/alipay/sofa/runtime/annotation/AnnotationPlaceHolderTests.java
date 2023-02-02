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
package com.alipay.sofa.runtime.annotation;

import com.alipay.sofa.boot.annotation.AnnotationWrapper;
import com.alipay.sofa.boot.annotation.DefaultPlaceHolderBinder;
import com.alipay.sofa.boot.annotation.WrapperAnnotation;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.runtime.sample.SampleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test {@link com.alipay.sofa.runtime.api.annotation.SofaServiceBinding} and
 * {@link com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding} place holder.
 *
 * @author qilong.zql
 * @author huzijie
 * @since 3.2.0
 */
@TestPropertySource({ "/config/application-annotation.properties" })
@ExtendWith(SpringExtension.class)
public class AnnotationPlaceHolderTests {

    @Autowired
    public Environment environment;

    @Test
    public void serviceAnnotationPlaceHolder() {

        SofaService sofaService = AnnotationSampleServiceImpl.class
            .getAnnotation(SofaService.class);
        SofaService delegate = AnnotationWrapper.create(SofaService.class)
            .withEnvironment(environment).withBinder(DefaultPlaceHolderBinder.INSTANCE)
            .wrap(sofaService);

        assertThat(sofaService.hashCode()).isEqualTo(delegate.hashCode());
        assertThat(sofaService.toString()).isEqualTo(delegate.toString());

        assertThat(SampleService.class).isEqualTo(sofaService.interfaceType());
        assertThat(SampleService.class).isEqualTo(delegate.interfaceType());

        assertThat("${annotation.sample.service.uniqueId}").isEqualTo(sofaService.uniqueId());
        assertThat("annotation-sample-service-uniqueId").isEqualTo(delegate.uniqueId());

        assertThat(1).isEqualTo(sofaService.bindings().length);
        assertThat(1).isEqualTo(delegate.bindings().length);

        SofaServiceBinding binding = sofaService.bindings()[0];
        String[] filters = binding.filters();
        SofaServiceBinding delegateBinding = delegate.bindings()[0];
        String[] delegateFilters = delegateBinding.filters();

        assertThat("${annotation.sample.service.bindingType}").isEqualTo(binding.bindingType());
        assertThat("bolt").isEqualTo(delegateBinding.bindingType());

        assertThat(300).isEqualTo(binding.timeout());
        assertThat(300).isEqualTo(delegateBinding.timeout());

        assertThat(2).isEqualTo(filters.length);
        assertThat(2).isEqualTo(delegateFilters.length);
        assertThat("${annotation.sample.service.filter-1}").isEqualTo(filters[0]);
        assertThat("service-filter-1").isEqualTo(delegateFilters[0]);
        assertThat("filter-2").isEqualTo(filters[1]);
        assertThat("filter-2").isEqualTo(delegateFilters[1]);

        assertThat(delegate).isInstanceOf(WrapperAnnotation.class);
        assertThat(delegateBinding).isInstanceOf(WrapperAnnotation.class);
    }

    @Test
    public void testReferenceAnnotationPlaceHolder() throws Exception {
        SofaReference sofaReference = AnnotationSampleServiceImpl.class.getField("sampleService")
            .getAnnotation(SofaReference.class);
        SofaReference delegate = AnnotationWrapper.create(SofaReference.class)
            .withEnvironment(environment).withBinder(DefaultPlaceHolderBinder.INSTANCE)
            .wrap(sofaReference);

        assertThat("${annotation.sample.ref.uniqueId}").isEqualTo(sofaReference.uniqueId());
        assertThat("sample-reference-uniqueId").isEqualTo(delegate.uniqueId());

        assertThat(sofaReference.jvmFirst()).isFalse();
        assertThat(delegate.jvmFirst()).isFalse();

        SofaReferenceBinding binding = sofaReference.binding();
        SofaReferenceBinding delegateBinding = delegate.binding();

        assertThat("${annotation.sample.ref.bindingType}").isEqualTo(binding.bindingType());
        assertThat("bolt").isEqualTo(delegateBinding.bindingType());

        assertThat("${annotation.sample.ref.direct-url}").isEqualTo(binding.directUrl());
        assertThat("127.0.0.1").isEqualTo(delegateBinding.directUrl());

        String[] filters = binding.filters();
        String[] delegateFilters = delegateBinding.filters();

        assertThat(2).isEqualTo(filters.length);
        assertThat(2).isEqualTo(delegateFilters.length);
        assertThat("${annotation.sample.ref.filter-1}").isEqualTo(filters[0]);
        assertThat("reference-filter-1").isEqualTo(delegateFilters[0]);
        assertThat("filter-2").isEqualTo(filters[1]);
        assertThat("filter-2").isEqualTo(delegateFilters[1]);

        assertThat(delegate).isInstanceOf(WrapperAnnotation.class);
        assertThat(delegateBinding).isInstanceOf(WrapperAnnotation.class);
    }
}
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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.boot.annotation.PlaceHolderAnnotationInvocationHandler;
import com.alipay.sofa.boot.annotation.PlaceHolderBinder;
import com.alipay.sofa.boot.annotation.WrapperAnnotation;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.runtime.test.beans.facade.SampleService;
import com.alipay.sofa.runtime.test.beans.service.AnnotationSampleService;

/**
 * Test {@link com.alipay.sofa.runtime.api.annotation.SofaServiceBinding} and
 * {@link com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding} place holder.
 *
 * @author qilong.zql
 * @since 3.2.0
 */
@TestPropertySource({ "/config/application-annotation.properties" })
@RunWith(SpringRunner.class)
public class AnnotationPlaceHolderTest {
    @Autowired
    public Environment environment;

    @Test
    @SuppressWarnings("unchecked")
    public void testServiceAnnotationPlaceHolder() {
        PlaceHolderBinder binder = new PlaceHolderBinder() {
            @Override
            public String bind(String origin) {
                return environment.resolvePlaceholders(origin);
            }
        };

        SofaService sofaService = AnnotationSampleService.class.getAnnotation(SofaService.class);
        PlaceHolderAnnotationInvocationHandler.AnnotationWrapperBuilder<SofaService> builder = PlaceHolderAnnotationInvocationHandler.AnnotationWrapperBuilder
            .wrap(sofaService).withBinder(binder);
        SofaService delegate = builder.build();

        Assert.assertEquals(sofaService.hashCode(), delegate.hashCode());
        Assert.assertEquals(sofaService.toString(), delegate.toString());

        Assert.assertEquals(SampleService.class, sofaService.interfaceType());
        Assert.assertEquals(SampleService.class, delegate.interfaceType());

        Assert.assertEquals("${annotation.sample.service.uniqueId}", sofaService.uniqueId());
        Assert.assertEquals("annotation-sample-service-uniqueId", delegate.uniqueId());

        Assert.assertEquals(1, sofaService.bindings().length);
        Assert.assertEquals(1, delegate.bindings().length);

        SofaServiceBinding binding = sofaService.bindings()[0];
        String[] filters = binding.filters();
        SofaServiceBinding delegateBinding = delegate.bindings()[0];
        String[] delegateFilters = delegateBinding.filters();

        Assert.assertEquals("${annotation.sample.service.bindingType}", binding.bindingType());
        Assert.assertEquals("bolt", delegateBinding.bindingType());

        Assert.assertEquals(300, binding.timeout());
        Assert.assertEquals(300, delegateBinding.timeout());

        Assert.assertEquals(2, filters.length);
        Assert.assertEquals(2, delegateFilters.length);
        Assert.assertEquals("${annotation.sample.service.filter-1}", filters[0]);
        Assert.assertEquals("service-filter-1", delegateFilters[0]);
        Assert.assertEquals("filter-2", filters[1]);
        Assert.assertEquals("filter-2", delegateFilters[1]);

        Assert.assertTrue(delegate instanceof WrapperAnnotation);
        Assert.assertTrue(delegateBinding instanceof WrapperAnnotation);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReferenceAnnotationPlaceHolder() throws Exception {
        PlaceHolderBinder binder = new PlaceHolderBinder() {
            @Override
            public String bind(String origin) {
                return environment.resolvePlaceholders(origin);
            }
        };

        SofaReference sofaReference = AnnotationSampleService.class.getField("sampleService")
            .getAnnotation(SofaReference.class);
        PlaceHolderAnnotationInvocationHandler.AnnotationWrapperBuilder<SofaReference> builder = PlaceHolderAnnotationInvocationHandler.AnnotationWrapperBuilder
            .wrap(sofaReference).withBinder(binder);
        SofaReference delegate = builder.build();

        Assert.assertEquals("${annotation.sample.ref.uniqueId}", sofaReference.uniqueId());
        Assert.assertEquals("sample-reference-uniqueId", delegate.uniqueId());

        Assert.assertFalse(sofaReference.jvmFirst());
        Assert.assertFalse(delegate.jvmFirst());

        SofaReferenceBinding binding = sofaReference.binding();
        SofaReferenceBinding delegateBinding = delegate.binding();

        Assert.assertEquals("${annotation.sample.ref.bindingType}", binding.bindingType());
        Assert.assertEquals("bolt", delegateBinding.bindingType());

        Assert.assertEquals("${annotation.sample.ref.direct-url}", binding.directUrl());
        Assert.assertEquals("127.0.0.1", delegateBinding.directUrl());

        String[] filters = binding.filters();
        String[] delegateFilters = delegateBinding.filters();

        Assert.assertEquals(2, filters.length);
        Assert.assertEquals(2, delegateFilters.length);
        Assert.assertEquals("${annotation.sample.ref.filter-1}", filters[0]);
        Assert.assertEquals("reference-filter-1", delegateFilters[0]);
        Assert.assertEquals("filter-2", filters[1]);
        Assert.assertEquals("filter-2", delegateFilters[1]);

        Assert.assertTrue(delegate instanceof WrapperAnnotation);
        Assert.assertTrue(delegateBinding instanceof WrapperAnnotation);
    }
}
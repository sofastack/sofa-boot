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
package com.alipay.sofa.runtime.test.beans.service;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.runtime.test.beans.facade.SampleService;

/**
 * Publish Service via annotation
 *
 * @author qilong.zql
 * @since 3.2.0
 */
@SofaService(interfaceType = SampleService.class, uniqueId = "${annotation.sample.service.uniqueId}", bindings = { @SofaServiceBinding(bindingType = "${annotation.sample.service.bindingType}", filters = {
                                                                                                                                                                                                            "${annotation.sample.service.filter-1}",
                                                                                                                                                                                                            "filter-2" }, timeout = 300) })
public class AnnotationSampleService implements SampleService {

    @SofaReference(uniqueId = "${annotation.sample.ref.uniqueId}", jvmFirst = false, binding = @SofaReferenceBinding(bindingType = "${annotation.sample.ref.bindingType}", filters = {
            "${annotation.sample.ref.filter-1}", "filter-2" }, directUrl = "${annotation.sample.ref.direct-url}"))
    public SampleService sampleService;

    @Override
    public String service() {
        return "AnnotationSampleService";
    }
}
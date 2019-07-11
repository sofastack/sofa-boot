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
package com.alipay.sofa.rpc.samples.annotation;

import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:leizhiyuan@gmail.com">leizhiyuan</a>
 */
@Component
public class AnnotationClientImpl {

    @SofaReference(interfaceType = AnnotationService.class, jvmFirst = false, binding = @SofaReferenceBinding(bindingType = "bolt"))
    private AnnotationService annotationService;

    public String sayClientAnnotation(String str) {

        String result = annotationService.sayAnnotation(str);

        return result;
    }
}
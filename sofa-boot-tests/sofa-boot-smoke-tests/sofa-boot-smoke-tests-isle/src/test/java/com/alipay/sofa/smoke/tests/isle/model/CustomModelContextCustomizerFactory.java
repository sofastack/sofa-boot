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
package com.alipay.sofa.smoke.tests.isle.model;

import com.alipay.sofa.smoke.tests.isle.util.AddCustomJar;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

import java.util.List;

/**
 * @author huzijie
 * @version CustomModelCustomizerFactory.java, v 0.1 2023年02月03日 10:33 AM huzijie Exp $
 */
public class CustomModelContextCustomizerFactory implements ContextCustomizerFactory {

    @Override
    public ContextCustomizer createContextCustomizer(Class<?> testClass,
                                                     List<ContextConfigurationAttributes> configAttributes) {
        MergedAnnotations annotations = MergedAnnotations.from(testClass,
            MergedAnnotations.SearchStrategy.SUPERCLASS);
        AddCustomJar addCustomJar = annotations.get(AddCustomJar.class).synthesize();
        String[] paths = addCustomJar.value();
        return new CustomModelContextCustomizer(paths);
    }
}

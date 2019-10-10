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
package com.alipay.sofa.isle.spring.share;

import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by TomorJM on 2019-10-09.
 */
public class SofaModulePostProcessorShareManager {

    private AbstractApplicationContext context;

    private List<Class>                filterClassList    = new ArrayList<>();

    private List<String>               filterBeanNameList = new ArrayList<>();

    public SofaModulePostProcessorShareManager(AbstractApplicationContext applicationContext) {
        this.context = applicationContext;
        Map<String, SofaModulePostProcessorShareFilter> map = context.getBeansOfType(SofaModulePostProcessorShareFilter.class);
        map.forEach((k, v) -> {
            this.filterClassList.addAll(v.filterBeanFactoryPostProessorClass());
            this.filterClassList.addAll(v.filterBeanPostProessorClass());
            this.filterBeanNameList.addAll(v.filterBeanName());
        });
    }

    public boolean unableToShare(Class<?> cls) {
        return this.filterClassList.contains(cls)
               || cls.isAnnotationPresent(UnshareSofaModulePostProcessor.class);
    }

    public boolean unableToShare(String beanName) {
        return this.filterBeanNameList.contains(beanName);
    }

}

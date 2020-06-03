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

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import java.util.Collections;
import java.util.List;

/**
 * Created by TomorJM on 2019-10-09.
 */
public interface SofaModulePostProcessorShareFilter {

    /**
     * filter {@link BeanPostProcessor} to avoid being added to submodules
     * @return
     */
    default List<Class<? extends BeanPostProcessor>> filterBeanPostProcessorClass() {
        return Collections.EMPTY_LIST;
    }

    /**
     * filter {@link BeanFactoryPostProcessor} to avoid being added to submodules
     * @return
     */
    default List<Class<? extends BeanFactoryPostProcessor>> filterBeanFactoryPostProcessorClass() {
        return Collections.EMPTY_LIST;
    }

    /**
     * filter beans with the name in the list to avoid being added to submodules
     * @return
     */
    default List<String> filterBeanName() {
        return Collections.EMPTY_LIST;
    }


}

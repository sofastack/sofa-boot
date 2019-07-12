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
package com.alipay.sofa.runtime.test.extension.bean;

import java.util.List;
import java.util.Map;

import com.alipay.sofa.runtime.test.extension.descriptor.ListExtensionDescriptor;
import com.alipay.sofa.runtime.test.extension.descriptor.SimpleExtensionDescriptor;

/**
 * @author khotyn
 * @author ruoshan
 * @since 2.6.0
 */
public interface IExtension {

    /**
     * Extension Client Test
     */
    String getClientValue();

    /**
     * Simple XNode Test
     */
    SimpleExtensionDescriptor getSimpleExtensionDescriptor();

    /**
     * XNodeList Test
     */
    ListExtensionDescriptor getListExtensionDescriptor();

    /**
     * XNodeMap Test
     */
    Map<String, String> getTestMap();

    /**
     * XNodeSpring Test
     */
    SimpleSpringBean getSimpleSpringBean();

    /**
     * XNodeSpringList Test
     */
    List<SimpleSpringListBean> getSimpleSpringListBeans();

    /**
     * XNodeSpringMap Test
     */
    Map<String, SimpleSpringMapBean> getSimpleSpringMapBeanMap();

    /**
     * XContext Test
     */
    String getTestContextValue();

    /**
     * XParent Test
     */
    String getTestParentValue();

    /**
     * Bad Test
     */
    SimpleExtensionDescriptor getBadDescriptor();

}
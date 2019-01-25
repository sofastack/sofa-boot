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
package com.alipay.sofa.runtime.ext.spring;

import com.alipay.sofa.runtime.ext.component.ExtensionPointInternal;
import com.alipay.sofa.service.api.component.ExtensionPoint;

/**
 * Extension point builder
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
public class ExtensionPointBuilder {

    private ExtensionPointInternal extensionPointInternal;

    private ExtensionPointBuilder() {

    }

    /**
     * Get extension point
     * @return extension point
     */
    public ExtensionPoint getExtensionPoint() {
        return this.extensionPointInternal;
    }

    /**
     * Create extension point builder
     * @param name extension point name
     * @param beanClassLoader classloader
     * @return extension point builder
     */
    public static ExtensionPointBuilder genericExtensionPoint(String name,
                                                              ClassLoader beanClassLoader) {
        return genericExtensionPoint(name, null, beanClassLoader);
    }

    /**
     * Create extension point builder
     * @param name extension point name
     * @param contributionClass contribution class
     * @param beanClassLoader classloader
     * @return extension point builder
     */
    public static ExtensionPointBuilder genericExtensionPoint(String name,
                                                              Class<?> contributionClass,
                                                              ClassLoader beanClassLoader) {
        ExtensionPointBuilder builder = new ExtensionPointBuilder();

        ExtensionPointInternal extensionPoint = new SpringExtensionPointImpl(name,
            contributionClass);
        extensionPoint.setBeanClassLoader(beanClassLoader);

        builder.extensionPointInternal = extensionPoint;
        return builder;
    }

    /**
     * add contribution to extension point
     * @param javaClass contribution class
     */
    public void addContribution(Class<?> javaClass) {
        this.extensionPointInternal.addContribution(javaClass);
    }

    /**
     * add contribution to extension point
     * @param className contribution class name
     */
    public void addContribution(String className) {
        this.extensionPointInternal.addContribution(className);
    }

}

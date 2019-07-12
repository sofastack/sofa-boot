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

import org.springframework.context.ApplicationContext;
import org.w3c.dom.Element;

import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.ext.component.ExtensionInternal;
import com.alipay.sofa.service.api.component.Extension;

/**
 * Extension Builder
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */

public class ExtensionBuilder {

    private ExtensionInternal extensionInternal;

    private ExtensionBuilder() {

    }

    /**
     * Get extension
     * @return extension
     */
    public Extension getExtension() {
        return this.extensionInternal;
    }

    /**
     * create extension builder
     *
     * @param extensionPoint     extension point name
     * @param element            element
     * @param applicationContext application context
     * @return extension builder
     */
    public static ExtensionBuilder genericExtension(String extensionPoint, Element element,
                                                    ApplicationContext applicationContext,
                                                    ClassLoader appClassLoader) {
        ExtensionBuilder builder = new ExtensionBuilder();
        builder.extensionInternal = new SpringExtensionImpl(null, extensionPoint, element,
            appClassLoader, applicationContext);
        return builder;
    }

    /**
     * Set element
     *
     * @param element element of the extension
     */
    public void setElement(Element element) {
        this.extensionInternal.setElement(element);
    }

    /**
     * Set target component name
     *
     * @param target target component name
     */
    public void setExtensionPoint(ComponentName target) {
        this.extensionInternal.setTargetComponentName(target);
    }

}

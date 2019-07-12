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
import com.alipay.sofa.runtime.ext.component.ExtensionImpl;

/**
 * Extension implement in spring env
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */

public class SpringExtensionImpl extends ExtensionImpl {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long    serialVersionUID = 1574173210970111642L;

    protected ApplicationContext applicationContext;

    public SpringExtensionImpl(ComponentName name, String extensionPoint, Element element,
                               ClassLoader appClassLoader, ApplicationContext applicationContext) {
        super(name, extensionPoint, element, appClassLoader);
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }
}

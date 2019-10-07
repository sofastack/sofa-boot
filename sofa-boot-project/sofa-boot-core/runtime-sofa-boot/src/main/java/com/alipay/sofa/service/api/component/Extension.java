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
package com.alipay.sofa.service.api.component;

import org.w3c.dom.Element;

import com.alipay.sofa.runtime.api.component.ComponentName;

/**
 * SOFA Extension Object
 *
 * @author xi.hux@alipay.com
 * @author ruoshan
 * @since 2.6.0
 */
public interface Extension {

    /**
     * Get component name for the extension
     *
     * @return Component name for the extension
     */
    ComponentName getComponentName();

    /**
     * Get Target Component Name。
     *
     * @return Target Component Name
     */
    ComponentName getTargetComponentName();

    /**
     * Get extension point Name
     *
     * @return Extension point Name
     */
    String getExtensionPoint();

    /**
     * Get extension element
     *
     * @return extension element
     */
    Element getElement();

    /**
     * Get contributions
     *
     * @return contributions
     */
    Object[] getContributions();

    /**
     * Get AppClassLoader
     * @return appClassLoader
     */
    ClassLoader getAppClassLoader();

    /**
     * Identifies the extension inside the contributing component.
     *
     * @return id
     */
    String getId();

}

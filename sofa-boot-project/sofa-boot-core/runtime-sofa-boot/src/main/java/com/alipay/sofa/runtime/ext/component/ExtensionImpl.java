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
package com.alipay.sofa.runtime.ext.component;

import java.io.Serializable;

import org.w3c.dom.Element;

import com.alipay.sofa.runtime.api.component.ComponentName;

/**
 * Extension Implement
 *
 * @author xi.hux@alipay.com
 * @author ruoshan
 * @since 2.6.0
 */

public class ExtensionImpl implements ExtensionInternal, Serializable {

    private static final long    serialVersionUID = 14648778982899384L;
    protected ComponentName      name;
    protected ComponentName      target;
    protected String             extensionPoint;
    protected String             documentation;
    protected ClassLoader        appClassLoader;
    protected transient Element  element;
    protected transient Object[] contributions;

    public ExtensionImpl(ComponentName name, String extensionPoint) {
        this(name, extensionPoint, null, null);
    }

    public ExtensionImpl(ComponentName name, String extensionPoint, Element element,
                         ClassLoader appClassLoader) {
        this.name = name;
        this.extensionPoint = extensionPoint;
        this.element = element;
        this.appClassLoader = appClassLoader;
    }

    public void dispose() {
        element = null;
        contributions = null;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getExtensionPoint() {
        return extensionPoint;
    }

    public ComponentName getComponentName() {
        return this.name;
    }

    public ComponentName getTargetComponentName() {
        return this.target;
    }

    public void setTargetComponentName(ComponentName target) {
        this.target = target;
    }

    public Object[] getContributions() {
        return contributions;
    }

    @Override
    public ClassLoader getAppClassLoader() {
        return appClassLoader;
    }

    public void setContributions(Object[] contributions) {
        this.contributions = contributions;
    }

    public String getDocumentation() {
        return documentation;
    }

    public String getId() {
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(ExtensionImpl.class.getSimpleName());
        buf.append(" {");
        buf.append("target: ");
        buf.append(name);
        buf.append(", point:");
        buf.append(extensionPoint);
        buf.append('}');
        return buf.toString();
    }
}

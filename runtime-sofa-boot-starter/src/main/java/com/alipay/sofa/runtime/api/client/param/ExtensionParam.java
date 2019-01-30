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
package com.alipay.sofa.runtime.api.client.param;

import org.w3c.dom.Element;

/**
 * Extension param
 *
 * @author khotyn
 * @author ruoshan
 * @since 2.6.0
 */
public class ExtensionParam {
    // target extension point name
    private String  targetName;
    // target bean name of extension
    private String  targetInstanceName;
    // XML Contribution element
    private Element element;

    /**
     * Get the target extension point name
     *
     * @return The target extension point name
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Set the target bean name of extension
     *
     * @param targetName The target bean name of extension
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    /**
     * Get the XML Contribution element
     *
     * @return The XML Contribution element
     */
    public Element getElement() {
        return element;
    }

    /**
     * Set the XML Contribution element
     *
     * @param element The XML Contribution element
     */
    public void setElement(Element element) {
        this.element = element;
    }

    /**
     * Get the target bean name of extension
     *
     * @return The target target bean name of extension
     */
    public String getTargetInstanceName() {
        return targetInstanceName;
    }

    /**
     * Set the target bean name of extension
     *
     * @param targetInstanceName The the target bean name of extension
     */
    public void setTargetInstanceName(String targetInstanceName) {
        this.targetInstanceName = targetInstanceName;
    }
}

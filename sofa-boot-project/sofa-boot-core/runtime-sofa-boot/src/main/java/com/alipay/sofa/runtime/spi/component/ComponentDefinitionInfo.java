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
package com.alipay.sofa.runtime.spi.component;

/**
 * Component basic source definition description.Just for showing Component info
 *
 * @author yuanxuan
 * @version : ComponentDefinitionInfo.java, v 0.1 2023年03月09日 14:38 yuanxuan Exp $
 */
public class ComponentDefinitionInfo {

    public static final String SOURCE = "source";

    /**
     * annotation or xml
     */
    private SourceType         sourceType;

    private String             beanId;

    private String             location;

    /**
     * when sourceType is annotation
     */
    private String             beanClassName;

    /**
     * Getter method for property <tt>sourceType</tt>.
     *
     * @return property value of sourceType
     */
    public SourceType getSourceType() {
        return sourceType;
    }

    /**
     * Setter method for property <tt>sourceType</tt>.
     *
     * @param sourceType value to be assigned to property sourceType
     */
    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * Getter method for property <tt>beanId</tt>.
     *
     * @return property value of beanId
     */
    public String getBeanId() {
        return beanId;
    }

    /**
     * Setter method for property <tt>beanId</tt>.
     *
     * @param beanId value to be assigned to property beanId
     */
    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    /**
     * Getter method for property <tt>fieldName</tt>.
     *
     * @return property value of fieldName
     */
    public String getLocation() {
        return location;
    }

    /**
     * Setter method for property <tt>fieldName</tt>.
     *
     * @param location value to be assigned to property fieldName
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Getter method for property <tt>beanClassName</tt>.
     *
     * @return property value of beanClassName
     */
    public String getBeanClassName() {
        return beanClassName;
    }

    /**
     * Setter method for property <tt>beanClassName</tt>.
     *
     * @param beanClassName value to be assigned to property beanClassName
     */
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public enum SourceType {
        ANNOTATION, XML;

    }
}
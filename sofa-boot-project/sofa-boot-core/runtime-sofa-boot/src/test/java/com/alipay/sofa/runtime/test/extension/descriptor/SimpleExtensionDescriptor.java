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
package com.alipay.sofa.runtime.test.extension.descriptor;

import java.io.File;
import java.net.URL;
import java.util.Date;

import com.alipay.sofa.common.xmap.Resource;
import com.alipay.sofa.common.xmap.annotation.XNode;
import com.alipay.sofa.common.xmap.annotation.XObject;

/**
 * @author khotyn
 * @author ruoshan
 * @since 2.6.0
 */
@XObject("simple")
public class SimpleExtensionDescriptor {

    @XNode("value")
    private String   stringValue;

    @XNode("path/value")
    private String   stringValueWithPath;

    @XNode("intValue")
    private Integer  intValue;

    @XNode("longValue")
    private Long     longValue;

    @XNode("floatValue")
    private Float    floatValue;

    @XNode("doubleValue")
    private Double   doubleValue;

    @XNode("booleanValue")
    private Boolean  booleanValue;

    @XNode("dateValue")
    private Date     dateValue;

    @XNode("fileValue")
    private File     fileValue;

    @XNode("urlValue")
    private URL      urlValue;

    @XNode("classValue")
    private Class    classValue;

    @XNode("resourceValue")
    private Resource resourceValue;

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValueWithPath() {
        return stringValueWithPath;
    }

    public void setStringValueWithPath(String stringValueWithPath) {
        this.stringValueWithPath = stringValueWithPath;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public File getFileValue() {
        return fileValue;
    }

    public void setFileValue(File fileValue) {
        this.fileValue = fileValue;
    }

    public URL getUrlValue() {
        return urlValue;
    }

    public void setUrlValue(URL urlValue) {
        this.urlValue = urlValue;
    }

    public Class getClassValue() {
        return classValue;
    }

    public void setClassValue(Class classValue) {
        this.classValue = classValue;
    }

    public Resource getResourceValue() {
        return resourceValue;
    }

    public void setResourceValue(Resource resourceValue) {
        this.resourceValue = resourceValue;
    }
}

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
package com.alipay.sofa.boot.startup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/11/23
 */
public class BeanStat {
    private static final String LAST_PREFIX        = "└─";
    private static final String MIDDLE_PREFIX      = "├─";
    private static final String INDENT_PREFIX      = "│   ";
    private static final String EMPTY_INDEX_PREFIX = "    ";

    private String              beanClassName;
    private long                beanRefreshStartTime;
    private long                beanRefreshEndTime;
    private long                refreshElapsedTime;
    private long                realRefreshElapsedTime;
    private long                initTime;
    private long                afterPropertiesSetTime;
    private String              interfaceType      = null;
    private String              beanType;
    private String              extensionProperty;

    private List<BeanStat>      children           = new ArrayList<BeanStat>();

    public void addChild(BeanStat beanStat) {
        children.add(beanStat);
    }

    public void startRefresh() {
        beanRefreshStartTime = System.currentTimeMillis();
    }

    public void finishRefresh() {
        beanRefreshEndTime = System.currentTimeMillis();
        refreshElapsedTime = beanRefreshEndTime - beanRefreshStartTime;

        long childRefreshTime = 0;
        for (BeanStat child : children) {
            childRefreshTime += child.getRealRefreshElapsedTime();
        }
        realRefreshElapsedTime = refreshElapsedTime - childRefreshTime;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public long getRefreshElapsedTime() {
        return refreshElapsedTime;
    }

    public void setRefreshElapsedTime(long refreshElapsedTime) {
        this.refreshElapsedTime = refreshElapsedTime;
    }

    public long getBeanRefreshStartTime() {
        return beanRefreshStartTime;
    }

    public void setBeanRefreshStartTime(long beanRefreshStartTime) {
        this.beanRefreshStartTime = beanRefreshStartTime;
    }

    public long getBeanRefreshEndTime() {
        return beanRefreshEndTime;
    }

    public void setBeanRefreshEndTime(long beanRefreshEndTime) {
        this.beanRefreshEndTime = beanRefreshEndTime;
    }

    public long getRealRefreshElapsedTime() {
        return realRefreshElapsedTime;
    }

    public void setRealRefreshElapsedTime(long realRefreshElapsedTime) {
        this.realRefreshElapsedTime = realRefreshElapsedTime;
    }

    public List<BeanStat> getChildren() {
        return children;
    }

    public void setChildren(List<BeanStat> children) {
        this.children = children;
    }

    public long getInitTime() {
        return initTime;
    }

    public void setInitTime(long initTime) {
        this.initTime = initTime;
    }

    public long getAfterPropertiesSetTime() {
        return afterPropertiesSetTime;
    }

    public void setAfterPropertiesSetTime(long afterPropertiesSetTime) {
        this.afterPropertiesSetTime = afterPropertiesSetTime;
    }

    public String getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getBeanType() {
        return beanType;
    }

    public void setBeanType(String beanType) {
        this.beanType = beanType;
    }

    public String getExtensionProperty() {
        return extensionProperty;
    }

    public void setExtensionProperty(String extensionProperty) {
        this.extensionProperty = extensionProperty;
    }

    public String toString(String indent, boolean last) {
        StringBuilder rtn = new StringBuilder();
        rtn.append(indent).append(last ? LAST_PREFIX : MIDDLE_PREFIX).append(beanClassName)
            .append("  [").append(refreshElapsedTime).append("ms]");

        int size = children.size();
        for (int i = 0; i < size; ++i) {
            rtn.append("\n").append(
                children.get(i).toString(indent + (last ? EMPTY_INDEX_PREFIX : INDENT_PREFIX),
                    i == size - 1));
        }
        return rtn.toString();
    }

    @Override
    public String toString() {
        return toString("", false);
    }
}

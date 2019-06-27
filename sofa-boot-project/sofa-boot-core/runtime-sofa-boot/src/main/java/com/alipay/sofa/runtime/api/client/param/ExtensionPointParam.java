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

/**
 * Extension-Point Param
 *
 * @author khotyn
 * @author ruoshan
 * @since 2.6.0
 */
public class ExtensionPointParam {
    // name of the extension point
    private String   name;
    // target bean name of the extension point
    private String   targetName;
    // target bean of the extension point
    private Object   target;
    // contribution description class
    private Class<?> contributionClass;

    /**
     * Get the name of the extension point
     *
     * @return The name of the extension point
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the extension point
     *
     * @param name The name of the extension point
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get target bean name of the extension point
     *
     * @return The name of the extension point
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * Set the target bean name of the extension point
     *
     * @param targetName The target bean name of the extension point
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    /**
     * Get target bean of the extension point
     *
     * @return The bean of the extension point
     */
    public Object getTarget() {
        return target;
    }

    /**
     * Set the target bean of the extension point
     *
     * @param target The target bean of the extension point
     */
    public void setTarget(Object target) {
        this.target = target;
    }

    /**
     * Get contribution description class of the extension point
     *
     * @return The contribution description class of the extension point
     */
    public Class<?> getContributionClass() {
        return contributionClass;
    }

    /**
     * Set contribution description class of the extension point
     *
     * @param contributionClass The contribution description class of the extension point
     */
    public void setContributionClass(Class<?> contributionClass) {
        this.contributionClass = contributionClass;
    }

}

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
package com.alipay.sofa.runtime.model;

/**
 * Component status.
 *
 * @author xuanbei 18/2/28
 */
public enum ComponentStatus {

    /**
     * UNREGISTERED
     */
    UNREGISTERED(0, "unregistered"),

    /**
     * REGISTERED
     */
    REGISTERED(1, "registered"),

    /**
     * RESOLVED
     */
    RESOLVED(2, "resolved"),

    /**
     * ACTIVATED
     */
    ACTIVATED(3, "activated");

    private final int    code;

    private final String desc;

    ComponentStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

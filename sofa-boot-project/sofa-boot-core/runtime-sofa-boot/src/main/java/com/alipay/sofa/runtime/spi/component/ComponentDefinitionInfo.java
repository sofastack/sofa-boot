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

import com.alipay.sofa.runtime.model.InterfaceMode;

import java.util.HashMap;
import java.util.Map;

/**
 * Component basic source definition description.Just for showing Component info
 *
 * @author yuanxuan
 * @version : ComponentDefinitionInfo.java, v 0.1 2023年03月09日 14:38 yuanxuan Exp $
 */
public class ComponentDefinitionInfo {

    public static final String        SOURCE               = "source";

    public static final String        BEAN_ID              = "beanId";

    public static final String        LOCATION             = "location";

    public static final String        BEAN_CLASS_NAME      = "beanClassName";

    public static final String        EXTENSION_POINT_NAME = "pointName";

    private final Map<String, String> info                 = new HashMap<>();

    /**
     * annotation or xml
     */
    private InterfaceMode             interfaceMode;

    public InterfaceMode getInterfaceMode() {
        return interfaceMode;
    }

    public void setInterfaceMode(InterfaceMode interfaceMode) {
        this.interfaceMode = interfaceMode;
    }

    public Map<String, String> getInfo() {
        return info;
    }

    public void putInfo(String key, String value) {
        info.put(key, value);
    }

    public String info(String key) {
        return info.getOrDefault(key, "");
    }
}

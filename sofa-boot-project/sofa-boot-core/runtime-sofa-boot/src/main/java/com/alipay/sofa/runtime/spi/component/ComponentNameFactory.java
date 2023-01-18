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

import org.springframework.util.StringUtils;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.model.ComponentType;

/**
 * ComponentName Factory.
 *
 * @author xuanbei 18/2/28
 */
public class ComponentNameFactory {
    /**
     * Create ComponentName by component type and class type.
     *
     * @param type component type
     * @param clazz clazz
     */
    public static ComponentName createComponentName(ComponentType type, Class<?> clazz) {
        return new ComponentName(type, mergeComponentName(clazz, null));
    }

    /**
     * Create ComponentName by component type and component name.
     *
     * @param type component type
     * @param name name
     * @return component name
     */
    public static ComponentName createComponentName(ComponentType type, String name) {
        return new ComponentName(type, name);
    }

    /**
     * Create ComponentName by component type,class type and unique id.
     *
     * @param type component type
     * @param clazz clazz
     * @param uniqueId unique id
     */
    public static ComponentName createComponentName(ComponentType type, Class<?> clazz,
                                                    String uniqueId) {
        return new ComponentName(type, mergeComponentName(clazz, uniqueId));
    }

    /**
     * Create ComponentName by class type and unique id.
     *
     * @param clazz clazz
     * @param uniqueId unique id
     */
    private static String mergeComponentName(Class<?> clazz, String uniqueId) {
        String ret = clazz.getName();
        if (StringUtils.hasText(uniqueId)) {
            ret += ":" + uniqueId;
        }
        return ret;
    }
}

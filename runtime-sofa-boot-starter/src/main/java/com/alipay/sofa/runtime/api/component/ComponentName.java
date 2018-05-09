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
package com.alipay.sofa.runtime.api.component;

import com.alipay.sofa.runtime.model.ComponentType;

import java.io.Serializable;

/**
 * ComponentName used to identify the component uniquely.
 *
 * @author xuanbei 18/2/28
 */
public class ComponentName implements Serializable {
    private static final long   serialVersionUID = -6856142686482394411L;

    /**
     * component type
     */
    private final ComponentType type;
    /**
     * component name
     */
    private final String        name;
    /**
     * raw name
     */
    private final String        rawName;

    /**
     * build ComponentName by component type and component name
     *
     * @param type component type
     * @param name component name
     */
    public ComponentName(ComponentType type, String name) {
        this.type = type;
        this.name = name;
        this.rawName = this.type.getName() + ":" + this.name;
    }

    public final ComponentType getType() {
        return type;
    }

    /**
     * Gets the name part of the component name.
     *
     * @return the name part
     */
    public final String getName() {
        return name;
    }

    public final String getRawName() {
        return rawName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return obj instanceof ComponentName && rawName.equals(((ComponentName) obj).rawName);
    }

    @Override
    public int hashCode() {
        return rawName.hashCode();
    }

    @Override
    public String toString() {
        return rawName;
    }

}

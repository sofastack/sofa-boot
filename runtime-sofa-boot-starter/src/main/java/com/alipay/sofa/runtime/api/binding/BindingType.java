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
package com.alipay.sofa.runtime.api.binding;

/**
 * The type of the binding, used to distinguish different types of
 * {@link com.alipay.sofa.runtime.api.client.param.BindingParam}
 *
 * @author xuanbei 18/2/28
 */
public class BindingType {

    private String type;

    /**
     * Set the binding type.
     *
     * @param type The binding type to set.
     */
    public BindingType(String type) {
        this.type = type;
    }

    /**
     * Get the binding type.
     *
     * @return The binding type.
     */
    public String getType() {
        return type;
    }

    /**
     * String representation of {@link BindingType}
     *
     * @return String representation of {@link BindingType}
     */
    @Override
    public String toString() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        BindingType that = (BindingType) o;

        return type != null ? type.equals(that.type) : that.type == null;
    }

    @Override
    public int hashCode() {
        return type != null ? type.hashCode() : 0;
    }
}

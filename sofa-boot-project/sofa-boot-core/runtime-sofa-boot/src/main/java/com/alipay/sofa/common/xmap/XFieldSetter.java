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
package com.alipay.sofa.common.xmap;

import java.lang.reflect.Field;

/**
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
public class XFieldSetter implements XSetter {

    /**
     * field
     */
    private final Field field;

    public XFieldSetter(Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    /**
     * @see XSetter#getType()
     */
    public Class<?> getType() {
        return field.getType();
    }

    /**
     * @see XSetter#setValue(Object, Object)
     */
    public void setValue(Object instance, Object value) throws IllegalAccessException {
        field.set(instance, value);
    }
}

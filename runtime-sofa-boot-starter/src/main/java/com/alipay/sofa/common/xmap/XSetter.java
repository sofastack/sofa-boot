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

/**
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
public interface XSetter {

    /**
     * Gets the type of the object to be set by this setter.
     *
     * @return the setter object type
     */
    Class getType();

    /**
     * Sets the value of the underlying member.
     *
     * @param instance the instance of the object that owns this field
     * @param value    the value to set
     * @throws Exception
     */
    void setValue(Object instance, Object value) throws Exception;

}

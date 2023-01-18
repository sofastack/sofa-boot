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

import com.alipay.sofa.runtime.api.ServiceValidationException;

/**
 * Implementation interface.
 *
 * @author xuanbei 18/2/28
 */
public interface Implementation {

    /**
     * Get name.
     *
     * @return name
     */
    String getName();

    /**
     * Get target object.
     *
     * @return target object
     */
    Object getTarget();

    /**
     * Get target object class type.
     *
     * @return target class type
     */
    Class<?> getTargetClass();

    /**
     * Is singleton or not.
     *
     * @return true or false。
     */
    boolean isSingleton();

    /**
     * Is lazy init or not.
     *
     * @return true or false。
     */
    boolean isLazyInit();

    /**
     * Set target object.
     */
    void setTarget(Object target);

    /**
     * Is Factory or not.
     *
     * @return true or false。
     */
    boolean isFactory();

    /**
     * Validate implement.
     *
     * @throws ServiceValidationException if validate error.
     */
    void validate() throws ServiceValidationException;
}

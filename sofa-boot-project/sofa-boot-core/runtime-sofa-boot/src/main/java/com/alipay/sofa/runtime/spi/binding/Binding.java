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
package com.alipay.sofa.runtime.spi.binding;

import org.w3c.dom.Element;

import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.spi.health.HealthResult;

/**
 * Interface for binding.
 *
 * @author xuanbei 18/2/28
 */
public interface Binding {
    /**
     * Get binding URI.
     *
     * @return binding URI
     */
    String getURI();

    /**
     * Get binding name.
     *
     * @return binding name
     */
    String getName();

    /**
     * Get binding type.
     *
     * @return binding type
     */
    BindingType getBindingType();

    /**
     * Get binding property dom Element.
     *
     * @return dom Element
     */
    Element getBindingPropertyContent();

    /**
     * Get binding hashcode, prevent duplicate registration.
     *
     * @return binding hashcode
     */
    int getBindingHashCode();

    /**
     * Dump binding information.
     *
     * @return binding information
     */
    String dump();

    /**
     * Check binding health.
     *
     * @return check result
     */
    HealthResult healthCheck();

    /**
     * Set binding healthy.
     *
     * @param healthy health or not
     */
    void setHealthy(boolean healthy);

    /**
     * Set binding destroyed state.
     *
     * @param destroyed destroyed or not
     */
    void setDestroyed(boolean destroyed);

    /**
     * Determine whether binding is destroyed.
     *
     * @return true or false
     */
    boolean isDestroyed();
}

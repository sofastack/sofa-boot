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

import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;

/**
 * <p>
 * Binding Adapter for SOFA service and reference
 * refer {@link BindingType} to get supported binding types.
 * </p>
 *
 * @author xuanbei 18/2/28
 */
public interface BindingAdapter<T extends Binding> {
    /**
     * pre out binding
     *
     * @param contract           binding contract
     * @param binding            binding object
     * @param target             binding target
     * @param sofaRuntimeContext sofa runtime context
     */
    void preOutBinding(Object contract, T binding, Object target,
                       SofaRuntimeContext sofaRuntimeContext);

    /**
     * out binding, out binding means provide service
     *
     * @param contract           binding contract
     * @param binding            binding object
     * @param target             binding target
     * @param sofaRuntimeContext sofa runtime context
     * @return binding result
     */
    Object outBinding(Object contract, T binding, Object target,
                      SofaRuntimeContext sofaRuntimeContext);

    /**
     * pre un-out binding
     *
     * @param contract           binding contract
     * @param binding            binding object
     * @param target             binding target
     * @param sofaRuntimeContext sofa runtime context
     */
    void preUnoutBinding(Object contract, T binding, Object target,
                         SofaRuntimeContext sofaRuntimeContext);

    /**
     * post unout binding
     *
     * @param contract           binding contract
     * @param binding            binding object
     * @param target             binding target
     * @param sofaRuntimeContext sofa runtime context
     */
    void postUnoutBinding(Object contract, T binding, Object target,
                          SofaRuntimeContext sofaRuntimeContext);

    /**
     * in binding, in binding means reference service
     *
     * @param contract           binding contract
     * @param binding            binding object
     * @param sofaRuntimeContext sofa runtime context
     * @return binding result
     */
    Object inBinding(Object contract, T binding, SofaRuntimeContext sofaRuntimeContext);

    /**
     * undo in binding
     *
     * @param contract contract
     * @param binding binding
     * @param sofaRuntimeContext sofa runtime context
     */
    void unInBinding(Object contract, T binding, SofaRuntimeContext sofaRuntimeContext);

    /**
     * get binding type
     *
     * @return binding type
     */
    BindingType getBindingType();

    /**
     * get binding class
     *
     * @return binding class
     */
    Class<T> getBindingClass();
}

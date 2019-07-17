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

import com.alipay.sofa.runtime.api.ServiceRuntimeException;

/**
 * <p>
 * SOFA Component Interface.
 * Component Lifecycle:
 * <pre>
 *                  [UNREGISTERED]
 *                      |   ▲
 *           register   │   │   unregister
 *                      |   |
 *                   [REGISTERED]
 *                      |   ▲
 *           resolve    │   │   unresolve
 *                      |   |
 *                    [RESOLVED]
 *                      |   ▲
 *                 ┌────┘   └────┐
 *                 │             │
 *        activate |             ▲ deactivate
 *                 │             │
 *                 └───┐    ┌────┘
 *                          |
 *                   [ACTIVATED]
 * </pre>
 *
 * @author xuanbei 18/2/28
 */
public interface Component {
    /**
     * register component
     */
    void register();

    /**
     * unregister component
     */
    void unregister() throws ServiceRuntimeException;

    /**
     * resolve component
     *
     * @return success or not
     */
    boolean resolve();

    /**
     * unresolve component
     */
    void unresolve() throws ServiceRuntimeException;

    /**
     * activate component
     *
     * @throws ServiceRuntimeException
     */
    void activate() throws ServiceRuntimeException;

    /**
     * deactivate component
     *
     * @throws ServiceRuntimeException
     */
    void deactivate() throws ServiceRuntimeException;

    /**
     * create an exception to describe error
     *
     * @throws ServiceRuntimeException
     */
    void exception(Exception e) throws ServiceRuntimeException;
}

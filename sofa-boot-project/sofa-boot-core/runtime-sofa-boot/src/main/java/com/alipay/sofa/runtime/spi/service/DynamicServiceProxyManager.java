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
package com.alipay.sofa.runtime.spi.service;

import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Contract;

/**
 * Interface to found service proxy or component dynamically.
 *
 * @author huzijie
 * @version ServiceProxyManager.java, v 0.1 2023年01月16日 3:37 PM huzijie Exp $
 * @since 4.0.0
 */
public interface DynamicServiceProxyManager {

    /**
     * Get a ServiceProxy instance.
     *
     * @param contract contract
     * @param classLoader classLoader
     * @return ServiceProxy instance.
     */
    ServiceProxy getDynamicServiceProxy(Contract contract, ClassLoader classLoader);

    /**
     * Get a ServiceComponent instance.
     *
     * @param contract contract
     * @param classLoader classLoader
     * @return ServiceComponent instance.
     */
    ServiceComponent getDynamicServiceComponent(Contract contract, ClassLoader classLoader);
}

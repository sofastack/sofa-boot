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
 * Default implementation of {@link DynamicServiceProxyManager}, return null immediately.
 *
 * @author huzijie
 * @version DefaultDynamicServiceProxyManager.java, v 0.1 2023年01月17日 3:25 PM huzijie Exp $
 */
public class DefaultDynamicServiceProxyManager implements DynamicServiceProxyManager {

    @Override
    public ServiceProxy getDynamicServiceProxy(Contract contract, ClassLoader classLoader) {
        return null;
    }

    @Override
    public ServiceComponent getDynamicServiceComponent(Contract contract, ClassLoader classLoader) {
        return null;
    }
}

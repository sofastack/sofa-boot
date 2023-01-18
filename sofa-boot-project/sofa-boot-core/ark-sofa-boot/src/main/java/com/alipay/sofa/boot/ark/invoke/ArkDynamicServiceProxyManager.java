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
package com.alipay.sofa.boot.ark.invoke;

import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.service.DynamicServiceProxyManager;
import com.alipay.sofa.runtime.spi.service.ServiceProxy;

/**
 * Implementation of {@link DynamicServiceProxyManager} to create dynamic service from Ark biz.
 *
 * @author huzijie
 * @version ArkDynamicServiceProxyManager.java, v 0.1 2023年01月16日 3:55 PM huzijie Exp $
 */
public class ArkDynamicServiceProxyManager implements DynamicServiceProxyManager {

    @Override
    public ServiceProxy getDynamicServiceProxy(Contract contract, ClassLoader classLoader) {
        return DynamicJvmServiceProxyFinder.getInstance().findServiceProxy(classLoader, contract);
    }

    @Override
    public ServiceComponent getDynamicServiceComponent(Contract contract, ClassLoader classLoader) {
        return DynamicJvmServiceProxyFinder.getInstance().findServiceComponent(classLoader,
            contract);
    }
}

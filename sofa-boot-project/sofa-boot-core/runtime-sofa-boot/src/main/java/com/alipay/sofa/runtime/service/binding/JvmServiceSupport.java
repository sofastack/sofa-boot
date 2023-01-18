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
package com.alipay.sofa.runtime.service.binding;

import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Contract;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.ComponentNameFactory;

/**
 * Support to find jvm service.
 *
 * @author huzijie
 * @version JvmServiceSupport.java, v 0.1 2023年01月16日 4:02 PM huzijie Exp $
 */
public class JvmServiceSupport {

    public static ServiceComponent foundServiceComponent(ComponentManager componentManager,
                                                         Contract contract) {
        ComponentName componentName = ComponentNameFactory.createComponentName(
            ServiceComponent.SERVICE_COMPONENT_TYPE, contract.getInterfaceType(),
            contract.getUniqueId());
        return (ServiceComponent) componentManager.getComponentInfo(componentName);
    }
}

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
package com.alipay.sofa.runtime.test.util;

import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.service.component.ReferenceComponent;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
public class ComponentNameUtil {
    public static ComponentName getServiceComponentName(Class clazz, String uniqueId) {
        return ComponentNameFactory.createComponentName(ServiceComponent.SERVICE_COMPONENT_TYPE,
            clazz, uniqueId);
    }

    public static ComponentName getReferenceComponentName(Class clazz, String uniqueId) {
        return ComponentNameFactory.createComponentName(
            ReferenceComponent.REFERENCE_COMPONENT_TYPE, clazz, uniqueId);
    }
}
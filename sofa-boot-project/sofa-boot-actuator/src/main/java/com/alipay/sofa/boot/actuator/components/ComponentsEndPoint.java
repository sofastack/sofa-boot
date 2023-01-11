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
package com.alipay.sofa.boot.actuator.components;

import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link Endpoint @Endpoint} to expose details of components registered in {@link SofaRuntimeContext}.
 *
 * @author huzijie
 * @version SofaBootComponentsEndPoint.java, v 0.1 2022年03月17日 3:55 PM huzijie Exp $
 */
@Endpoint(id = "components")
public class ComponentsEndPoint {

    private final SofaRuntimeContext sofaRuntimeContext;

    /**
     * Creates a new {@code SofaBootComponentsEndPoint} that will describe the components in the {@link SofaRuntimeContext}
     * @param sofaRuntimeContext the sofa runtime context
     */
    public ComponentsEndPoint(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    @ReadOperation
    public ApplicationComponents components() {
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        Map<String, Collection<ComponentDisplayInfo>> componentsInfoMap = new HashMap<>();
        Collection<ComponentType> componentTypes = componentManager.getComponentTypes();
        componentTypes.forEach(componentType -> {
            Collection<ComponentInfo> componentInfos = componentManager.getComponentInfosByType(componentType);
            Collection<ComponentDisplayInfo> componentDisplayInfos = componentInfos.stream()
                    .map(componentInfo -> new ComponentDisplayInfo(componentInfo.getName().getName(),
                            componentInfo.getApplicationContext().getId()))
                    .collect(Collectors.toList());
            componentsInfoMap.put(componentType.getName(), componentDisplayInfos);
        });
        return new ApplicationComponents(componentsInfoMap);
    }

    public static final class ApplicationComponents {

        private final Map<String, Collection<ComponentDisplayInfo>> componentsInfoMap;

        private ApplicationComponents(Map<String, Collection<ComponentDisplayInfo>> componentsInfoMap) {
            this.componentsInfoMap = componentsInfoMap;
        }

        public Map<String, Collection<ComponentDisplayInfo>> getComponentsInfoMap() {
            return this.componentsInfoMap;
        }
    }

    public static final class ComponentDisplayInfo {

        private final String name;

        private final String applicationId;

        private ComponentDisplayInfo(String name, String applicationId) {
            this.name = name;
            this.applicationId = applicationId;
        }

        public String getName() {
            return name;
        }

        public String getApplicationId() {
            return applicationId;
        }
    }

}

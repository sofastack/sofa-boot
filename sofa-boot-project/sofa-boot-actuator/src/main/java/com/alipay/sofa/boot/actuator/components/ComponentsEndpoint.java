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

import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.boot.actuate.endpoint.OperationResponseBody;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@link Endpoint @Endpoint} to expose details of components registered in {@link SofaRuntimeContext}.
 *
 * @author huzijie
 * @version ComponentsEndpoint.java, v 0.1 2022年03月17日 3:55 PM huzijie Exp $
 */
@Endpoint(id = "components")
public class ComponentsEndpoint {

    private final SofaRuntimeContext sofaRuntimeContext;

    /**
     * Creates a new {@code SofaBootComponentsEndPoint} that will describe the components in the {@link SofaRuntimeContext}
     *
     * @param sofaRuntimeContext the sofa runtime context
     */
    public ComponentsEndpoint(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    @ReadOperation
    public ComponentsDescriptor components() {
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        Map<String, Collection<ComponentDisplayInfo>> componentsInfoMap = new HashMap<>();
        Collection<ComponentType> componentTypes = componentManager.getComponentTypes();
        componentTypes.forEach(componentType -> {
            Collection<ComponentInfo> componentInfos = componentManager.getComponentInfosByType(componentType);
            Collection<ComponentDisplayInfo> componentDisplayInfos = componentInfos.stream()
                    .map(componentInfo -> {
                        String applicationId = componentInfo.getApplicationContext() == null ? "null"
                                : componentInfo.getApplicationContext().getId();
                        Map<String, Property> propertyMap = componentInfo.getProperties();
                        return new ComponentDisplayInfo(componentInfo.getName().getName(), applicationId,
                                propertyMap != null ? propertyMap.values().stream().map(PropertyInfo::new).collect(Collectors.toList())
                                        : null);
                    })
                    .collect(Collectors.toList());
            componentsInfoMap.put(componentType.getName(), componentDisplayInfos);
        });
        return new ComponentsDescriptor(componentsInfoMap);
    }

    public static final class ComponentsDescriptor implements OperationResponseBody {

        private final Map<String, Collection<ComponentDisplayInfo>> componentsInfoMap;

        private ComponentsDescriptor(Map<String, Collection<ComponentDisplayInfo>> componentsInfoMap) {
            this.componentsInfoMap = componentsInfoMap;
        }

        public Map<String, Collection<ComponentDisplayInfo>> getComponentsInfoMap() {
            return this.componentsInfoMap;
        }
    }

    public static final class ComponentDisplayInfo {

        private final String       name;

        private final String       applicationId;

        private List<PropertyInfo> properties;

        private ComponentDisplayInfo(String name, String applicationId,
                                     List<PropertyInfo> properties) {
            this.name = name;
            this.applicationId = applicationId;
            this.properties = properties;
        }

        public String getName() {
            return name;
        }

        public String getApplicationId() {
            return applicationId;
        }

        public List<PropertyInfo> getProperties() {
            return properties;
        }
    }

    public static final class PropertyInfo {
        private String name;

        private Object value;

        public PropertyInfo(Property property) {
            this.name = property.getName();
            this.value = property.getValue();
        }

        public PropertyInfo(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }
    }

}

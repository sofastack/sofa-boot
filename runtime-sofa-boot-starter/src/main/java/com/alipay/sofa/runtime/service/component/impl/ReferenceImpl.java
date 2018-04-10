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
package com.alipay.sofa.runtime.service.component.impl;

import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.component.AbstractContract;
import com.alipay.sofa.runtime.service.component.Reference;

import java.util.Map;

/**
 * Reference Implementation
 *
 * @author xuanbei 18/3/1
 */
public class ReferenceImpl extends AbstractContract implements Reference {
    public ReferenceImpl(String uniqueId, Class<?> interfaceType, InterfaceMode interfaceMode) {
        super(uniqueId, interfaceType, interfaceMode);
    }

    public ReferenceImpl(String uniqueId, Class<?> interfaceType, InterfaceMode interfaceMode,
                         Map<String, String> properties) {
        super(uniqueId, interfaceType, interfaceMode, properties);
    }

    @Override
    public String toString() {
        return this.getInterfaceType().getName();
    }
}

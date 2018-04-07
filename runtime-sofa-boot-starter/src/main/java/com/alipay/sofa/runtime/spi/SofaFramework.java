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
package com.alipay.sofa.runtime.spi;

import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;

import java.util.Set;

/**
 * @author xuanbei 18/3/1
 */
public interface SofaFramework {
    /**
     * get sofa runtime context by app name
     *
     * @param appName app name
     * @return sofa runtime context
     */
    SofaRuntimeContext getSofaRuntimeContext(String appName);

    /**
     * get sofa runtime manager by app name
     *
     * @param appName app name
     * @return sofa runtime manager
     */
    SofaRuntimeManager getSofaRuntimeManager(String appName);

    void removeSofaRuntimeManager(String appName);

    /**
     * get all app names in framework
     *
     * @return all app names
     */
    Set<String> getSofaFrameworkAppNames();
}

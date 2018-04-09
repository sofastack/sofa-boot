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
package com.alipay.sofa.runtime.api.component;

/**
 * The SOFA configurations of an application.
 *
 * @author xuanbei 18/2/28
 */
public interface AppConfiguration {
    /**
     * Get a specific configuration value from SOFA configuration.
     *
     * @param key The key of the configuration.
     * @return The value of the configuration.
     */
    String getPropertyValue(String key);

    /**
     * Get a specific configuration value from SOFA configuration. If the value is null, return the specified default
     * value.
     *
     * @param key The key of the configuration.
     * @param defaultValue The default value to return when the value from SOFA configuration is null.
     * @return The value of the configuration if it is not null. If it is null, return the specified default value.
     */
    String getPropertyValue(String key, String defaultValue);
}

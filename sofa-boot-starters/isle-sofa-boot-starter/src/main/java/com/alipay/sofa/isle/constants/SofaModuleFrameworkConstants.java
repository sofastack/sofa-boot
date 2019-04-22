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
package com.alipay.sofa.isle.constants;

/**
 * Framework Constants
 *
 * @author khotyn 7/25/14 8:21 PM
 */
public interface SofaModuleFrameworkConstants {
    /** framework constants **/
    String APPLICATION                            = "SOFABOOT-APPLICATION";
    String PROCESSORS_OF_ROOT_APPLICATION_CONTEXT = "PROCESSORS_OF_ROOT_APPLICATION_CONTEXT";
    String SOFA_MODULE_FILE                       = "sofa-module.properties";
    String SPRING_CONTEXT_PATH                    = "META-INF/spring";
    String PROFILE_SEPARATOR                      = ",";
    String DEFAULT_PROFILE_VALUE                  = "default";
    String SOFA_MODULE_PROPERTIES_BEAN_ID         = "sofaModuleProperties";

    /** sofa-module.properties keywords **/
    String SPRING_PARENT                          = "Spring-Parent";
    String MODULE_NAME                            = "Module-Name";
    String REQUIRE_MODULE                         = "Require-Module";
    String MODULE_PROFILE                         = "Module-Profile";
}
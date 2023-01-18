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
package com.alipay.sofa.boot.isle.profile;

import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;

/**
 * Interface used to check sofa module according to profiles.
 *
 * @author qilong.zql
 * @author yangguanchao
 * @since 3.2.0
 */
public interface SofaModuleProfileChecker {

    /***
     * determine whether the module should be activated.
     *
     * @param sofaModuleProfiles sofa module profiles
     * @return true to accept, otherwise false
     */
    boolean acceptProfiles(String[] sofaModuleProfiles);

    /**
     * determine whether the module should be activated.
     *
     * @param deploymentDescriptor deploymentDescriptor
     * @return true to accept, otherwise false
     */
    boolean acceptModule(DeploymentDescriptor deploymentDescriptor);
}

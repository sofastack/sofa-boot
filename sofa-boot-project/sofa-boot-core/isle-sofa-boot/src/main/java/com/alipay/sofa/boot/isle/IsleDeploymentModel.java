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
package com.alipay.sofa.boot.isle;

import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * An interface to obtain isle deployments
 * <p> Note it can only be aware when Isle deployments have start installing.
 * <pre>{@code
 *     IsleDeploymentModel model = applicationContext.getBean(SofaBootConstants.APPLICATION, IsleDeploymentModel.class);
 * }</pre>
 *
 * @author huzijie
 * @version IsleDeploymentModel.java, v 0.1 2022年07月11日 11:28 AM huzijie Exp $
 */
public interface IsleDeploymentModel {

    /**
     * Get All module application context map, the key is module name, the value is applicationContext.
     * <p> Note the result will change when isle deployments is installing
     * @return the application context map, key is module name, value is context instances.
     */
    @NonNull
    Map<String, ApplicationContext> getModuleApplicationContextMap();
}

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
package com.alipay.sofa.isle.utils;

import com.alipay.sofa.isle.constants.SofaIsleFrameworkConstants;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.profile.DefaultSofaIsleProfileEnvironment;
import com.alipay.sofa.isle.profile.SofaIsleProfileEnvironment;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xuanbei 18/5/3
 */
public class SofaIsleProfileUtil {
    private static final ConcurrentMap<ApplicationContext, SofaIsleProfileEnvironment> map = new ConcurrentHashMap<>();

    public static boolean acceptProfile(ApplicationContext applicationContext,
                                        DeploymentDescriptor dd) {
        SofaIsleProfileEnvironment sofaProfileEnvironment = createSofaProfileEnvironment(applicationContext);
        return sofaProfileEnvironment.acceptsProfiles(getActiveModuleProfiles(dd));
    }

    private static SofaIsleProfileEnvironment createSofaProfileEnvironment(ApplicationContext applicationContext) {
        SofaIsleProfileEnvironment environment = map.get(applicationContext);
        if (environment == null) {
            DefaultSofaIsleProfileEnvironment sofaProfileEnvironment = new DefaultSofaIsleProfileEnvironment();
            sofaProfileEnvironment.initEnvironment(applicationContext);
            map.putIfAbsent(applicationContext, sofaProfileEnvironment);
            environment = map.get(applicationContext);
        }
        return environment;
    }

    private static String[] getActiveModuleProfiles(DeploymentDescriptor deploymentDescriptor) {
        String[] activeModuleProfiles = new String[] { SofaIsleFrameworkConstants.DEFAULT_PROFILE_VALUE };
        String profiles = deploymentDescriptor
            .getProperty(SofaIsleFrameworkConstants.MODULE_PROFILE);
        if (profiles == null || profiles.length() == 0) {
            return activeModuleProfiles;
        }
        activeModuleProfiles = profiles.split(SofaIsleFrameworkConstants.PROFILE_SPLITTER);
        return activeModuleProfiles;
    }
}

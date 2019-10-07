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
package com.alipay.sofa.isle.profile;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;

/**
 * @author yangguanchao
 * @author qilong.zql
 * @since 3.2.0
 */
public class DefaultSofaModuleProfileChecker implements SofaModuleProfileChecker, InitializingBean {

    @Autowired
    private SofaModuleProperties sofaModuleProperties;
    private Set<String>          activeProfiles = new HashSet<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        this.activeProfiles.add(SofaBootConstants.DEFAULT_PROFILE_VALUE);
        String configProfiles = sofaModuleProperties.getActiveProfiles();
        if (StringUtils.hasText(configProfiles)) {
            String[] activeConfigProfileList = configProfiles
                .split(SofaBootConstants.PROFILE_SEPARATOR);
            for (String sofaProfile : activeConfigProfileList) {
                validateProfile(sofaProfile);
                this.activeProfiles.add(sofaProfile.trim());
            }
        }
    }

    @Override
    public boolean acceptProfiles(String[] sofaModuleProfiles) {
        Assert.notEmpty(sofaModuleProfiles,
            "Must specify at least one sofa module profile,at least one profile value is "
                    + SofaBootConstants.DEFAULT_PROFILE_VALUE);
        for (String sofaModuleProfile : sofaModuleProfiles) {
            if (StringUtils.hasText(sofaModuleProfile) && sofaModuleProfile.charAt(0) == '!') {
                if (!isProfileActive(sofaModuleProfile.substring(1))) {
                    return true;
                }
            } else if (isProfileActive(sofaModuleProfile)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean acceptModule(DeploymentDescriptor deploymentDescriptor) {
        return acceptProfiles(getModuleProfiles(deploymentDescriptor));
    }

    private boolean isProfileActive(String moduleProfile) {
        validateProfile(moduleProfile);
        return this.activeProfiles.contains(moduleProfile.trim());
    }

    private void validateProfile(String profile) {
        if (!StringUtils.hasText(profile)) {
            throw new IllegalArgumentException("Invalid profile [" + profile
                                               + "]: must contain text and at least a value "
                                               + SofaBootConstants.DEFAULT_PROFILE_VALUE);
        }

        if (profile.charAt(0) == '!') {
            throw new IllegalArgumentException(
                "Invalid sofa.profiles.active value in sofa-config.config [" + profile
                        + "]: must not begin with ! operator");
        }
    }

    private String[] getModuleProfiles(DeploymentDescriptor deploymentDescriptor) {
        String[] activeModuleProfiles = new String[] { SofaBootConstants.DEFAULT_PROFILE_VALUE };
        String profiles = deploymentDescriptor.getProperty(SofaBootConstants.MODULE_PROFILE);
        if (profiles == null || profiles.length() == 0) {
            return activeModuleProfiles;
        }
        activeModuleProfiles = profiles.split(SofaBootConstants.PROFILE_SEPARATOR);
        return activeModuleProfiles;
    }
}

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
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.boot.log.ErrorCode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of {@link SofaModuleProfileChecker} to use spring custom profiles.
 *
 * @author yangguanchao
 * @author qilong.zql
 * @author huzijie
 * @since 3.2.0
 */
public class DefaultSofaModuleProfileChecker implements SofaModuleProfileChecker, InitializingBean {

    private final Set<String> activeProfiles = new HashSet<>();

    private List<String>      userCustomProfiles;

    @Override
    public void afterPropertiesSet() {
        init();
    }

    public void init() {
        activeProfiles.add(DeploymentDescriptorConfiguration.DEFAULT_PROFILE_VALUE);
        if (userCustomProfiles != null) {
            activeProfiles.addAll(userCustomProfiles.stream().map(String::trim).collect(Collectors.toSet()));
        }
        for (String sofaProfile : activeProfiles) {
            validateProfile(sofaProfile);
        }
    }

    @Override
    public boolean acceptProfiles(String[] sofaModuleProfiles) {
        Assert.notEmpty(sofaModuleProfiles,
            ErrorCode.convert("01-13000", DeploymentDescriptorConfiguration.DEFAULT_PROFILE_VALUE));
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
            throw new IllegalArgumentException(ErrorCode.convert("01-13001", profile,
                DeploymentDescriptorConfiguration.DEFAULT_PROFILE_VALUE));
        }

        if (profile.charAt(0) == '!') {
            throw new IllegalArgumentException(ErrorCode.convert("01-13002", profile));
        }
    }

    private String[] getModuleProfiles(DeploymentDescriptor deploymentDescriptor) {
        String profiles = deploymentDescriptor
            .getProperty(DeploymentDescriptorConfiguration.MODULE_PROFILE);
        if (StringUtils.hasText(profiles)) {
            return StringUtils.commaDelimitedListToStringArray(profiles);
        } else {
            return new String[] { DeploymentDescriptorConfiguration.DEFAULT_PROFILE_VALUE };
        }

    }

    public List<String> getUserCustomProfiles() {
        return userCustomProfiles;
    }

    public void setUserCustomProfiles(List<String> userCustomProfiles) {
        this.userCustomProfiles = userCustomProfiles;
    }
}

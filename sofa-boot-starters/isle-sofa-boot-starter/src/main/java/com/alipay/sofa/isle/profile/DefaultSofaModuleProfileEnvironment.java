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

import com.alipay.sofa.isle.constants.SofaModuleFrameworkConstants;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * Created by yangguanchao on 16/4/1.
 */
public class DefaultSofaModuleProfileEnvironment implements SofaModuleProfileEnvironment {
    /** active sofa profiles */
    private final Set<String> activeProfiles = new HashSet<>();

    @Override
    public void initEnvironment(ApplicationContext applicationContext) {
        this.activeProfiles.clear();
        this.activeProfiles.add(SofaModuleFrameworkConstants.DEFAULT_PROFILE_VALUE);
        if (applicationContext == null || applicationContext.getEnvironment() == null) {
            return;
        }

        String activeProfiles = applicationContext
            .getBean(SofaModuleFrameworkConstants.SOFA_MODULE_PROPERTIES_BEAN_ID,
                SofaModuleProperties.class).getActiveProfiles();
        if (StringUtils.hasText(activeProfiles)) {
            String[] activeConfigProfileList = activeProfiles
                .split(SofaModuleFrameworkConstants.PROFILE_SEPARATOR);
            initActiveProfiles(activeConfigProfileList);
        }
    }

    @Override
    public boolean acceptsProfiles(String[] sofaModuleProfiles) {
        Assert.notEmpty(sofaModuleProfiles,
            "Must specify at least one sofa module profile,at least one profile value is "
                    + SofaModuleFrameworkConstants.DEFAULT_PROFILE_VALUE);
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

    private void initActiveProfiles(String[] ActiveProfiles) {
        if (ActiveProfiles == null || ActiveProfiles.length == 0) {
            return;
        }
        for (String sofaProfile : ActiveProfiles) {
            validateProfile(sofaProfile);
            this.activeProfiles.add(sofaProfile.trim());
        }
    }

    private boolean isProfileActive(String moduleProfile) {
        validateProfile(moduleProfile);
        return this.activeProfiles.contains(moduleProfile.trim());
    }

    private void validateProfile(String profile) {
        if (!StringUtils.hasText(profile)) {
            throw new IllegalArgumentException("Invalid profile [" + profile
                                               + "]: must contain text and at least a value "
                                               + SofaModuleFrameworkConstants.DEFAULT_PROFILE_VALUE);
        }

        if (profile.charAt(0) == '!') {
            throw new IllegalArgumentException(
                "Invalid sofa.profiles.active value in sofa-config.config [" + profile
                        + "]: must not begin with ! operator");
        }
    }
}

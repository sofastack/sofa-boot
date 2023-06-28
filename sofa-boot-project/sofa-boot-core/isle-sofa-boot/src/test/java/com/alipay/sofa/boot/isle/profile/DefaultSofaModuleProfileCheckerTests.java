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

import com.alipay.sofa.boot.isle.SampleDeploymentDescriptor;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptorConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link DefaultSofaModuleProfileChecker}.
 *
 * @author xuanbei 18/5/8
 * @author qilong.zql
 * @author huzijie
 */
public class DefaultSofaModuleProfileCheckerTests {

    private final DefaultSofaModuleProfileChecker sofaModuleProfileChecker;

    public DefaultSofaModuleProfileCheckerTests() {
        this.sofaModuleProfileChecker = new DefaultSofaModuleProfileChecker();
        List<String> profiles = new ArrayList<>(StringUtils.commaDelimitedListToSet("dev,product"));
        this.sofaModuleProfileChecker.setUserCustomProfiles(profiles);
        this.sofaModuleProfileChecker.init();
    }

    @Test
    public void devProfile() {
        // test dev profile
        Properties props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.dal");
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_PROFILE, "dev");
        assertThat(sofaModuleProfileChecker.acceptModule(SampleDeploymentDescriptor.create(props)))
            .isTrue();
    }

    @Test
    public void productProfile() {
        // test product profile
        Properties props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.dal");
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_PROFILE, "product");
        assertThat(sofaModuleProfileChecker.acceptModule(SampleDeploymentDescriptor.create(props)))
            .isTrue();
    }

    @Test
    public void negateDevProfile() {
        // test !dev profile
        Properties props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.dal");
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_PROFILE, "!dev");
        assertThat(sofaModuleProfileChecker.acceptModule(SampleDeploymentDescriptor.create(props)))
            .isFalse();

        props.setProperty(DeploymentDescriptorConfiguration.MODULE_PROFILE, "!test");
        assertThat(sofaModuleProfileChecker.acceptModule(SampleDeploymentDescriptor.create(props)))
            .isTrue();
    }

    @Test
    public void devAndGreyProfile() {
        // test test,grey profile
        Properties props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.dal");
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_PROFILE, "dev,grey");
        assertThat(sofaModuleProfileChecker.acceptModule(SampleDeploymentDescriptor.create(props)))
            .isTrue();
    }

    @Test
    public void testAndGreyProfile() {
        // test test,grey profile
        Properties props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.dal");
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_PROFILE, "test,grey");
        assertThat(sofaModuleProfileChecker.acceptModule(SampleDeploymentDescriptor.create(props)))
            .isFalse();
    }

    @Test
    public void noProfile() {
        // test no profile, default pass
        Properties props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.dal");
        assertThat(sofaModuleProfileChecker.acceptModule(SampleDeploymentDescriptor.create(props)))
            .isTrue();
    }

    @Test
    public void illegalProfile() {
        // test illegal profile, throw exception
        Properties props = new Properties();
        props.setProperty(DeploymentDescriptorConfiguration.MODULE_NAME, "com.alipay.dal");

        props.setProperty(DeploymentDescriptorConfiguration.MODULE_PROFILE, "!");
        assertThatThrownBy(() -> sofaModuleProfileChecker.acceptModule(SampleDeploymentDescriptor.create(props)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("01-13001");

        props.setProperty(DeploymentDescriptorConfiguration.MODULE_PROFILE, "!!");
        assertThatThrownBy(() -> sofaModuleProfileChecker.acceptModule(SampleDeploymentDescriptor.create(props)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("01-13002");
    }
}

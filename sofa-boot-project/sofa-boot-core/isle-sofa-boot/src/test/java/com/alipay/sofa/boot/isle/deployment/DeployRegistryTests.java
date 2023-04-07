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
package com.alipay.sofa.boot.isle.deployment;

import com.alipay.sofa.boot.isle.MockDeploymentDescriptor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DeployRegistry}.
 *
 * @author huzijie
 * @version DeployRegistryTests.java, v 0.1 2023年04月07日 10:13 AM huzijie Exp $
 */
public class DeployRegistryTests {

    @Test
    void addAndRemove() {
        DeployRegistry registry = new DeployRegistry();
        DeploymentDescriptor descriptor = new MockDeploymentDescriptor("module");
        registry.add(descriptor);
        assertThat(registry.get("module")).isEqualTo(descriptor);
        registry.remove("module");
        assertThat(registry.get("module")).isNotNull();
    }

    @Test
    void getResolvedEntries() {
        DeployRegistry registry = new DeployRegistry();
        DeploymentDescriptor descriptor1 = new MockDeploymentDescriptor("module1");
        DeploymentDescriptor descriptor2 = new MockDeploymentDescriptor("module2");
        registry.add(descriptor1);
        registry.add(descriptor2);
        assertThat(registry.getResolvedEntries()).containsExactly(entry("module1", descriptor1),
            entry("module2", descriptor2));
    }

    @Test
    void getMissingRequirements() {
        DeployRegistry registry = new DeployRegistry();
        MockDeploymentDescriptor descriptor1 = new MockDeploymentDescriptor("module1");
        MockDeploymentDescriptor descriptor2 = new MockDeploymentDescriptor("module2");
        descriptor2.addRequiredModule("module1");
        registry.add(descriptor2);
        assertThat(registry.getMissingRequirements())
            .containsExactly(entry("module1", descriptor1));
    }

    @Test
    void getEntries() {
        DeployRegistry registry = new DeployRegistry();
        DeploymentDescriptor descriptor1 = new MockDeploymentDescriptor("module1");
        DeploymentDescriptor descriptor2 = new MockDeploymentDescriptor("module2");
        registry.add(descriptor1);
        registry.add(descriptor2);
        assertThat(registry.getEntries()).containsExactly(entry("module1", descriptor1),
            entry("module2", descriptor2));
    }

    @Test
    void getResolvedObjects() {
        DeployRegistry registry = new DeployRegistry();
        DeploymentDescriptor descriptor1 = new MockDeploymentDescriptor("module1");
        DeploymentDescriptor descriptor2 = new MockDeploymentDescriptor("module2");
        registry.add(descriptor1);
        registry.add(descriptor2);
        assertThat(registry.getResolvedObjects()).containsExactly(descriptor1, descriptor2);
    }

    @Test
    void getPendingObjects() {
        DeployRegistry registry = new DeployRegistry();
        MockDeploymentDescriptor descriptor2 = new MockDeploymentDescriptor("module2");
        descriptor2.addRequiredModule("module1");
        registry.add(descriptor2);
        assertThat(registry.getPendingObjects()).contains(descriptor2);
    }

    @Test
    void getEntry() {
        DeployRegistry registry = new DeployRegistry();
        MockDeploymentDescriptor descriptor1 = new MockDeploymentDescriptor("module1");
        MockDeploymentDescriptor descriptor2 = new MockDeploymentDescriptor("module2");
        registry.add(descriptor1);
        registry.add(descriptor2);
        assertThat(registry.getEntry("module1")).isEqualTo(entry("module1", descriptor1));
        assertThat(registry.getEntry("module2")).isEqualTo(entry("module2", descriptor2));
    }

    @Test
    void getPendingEntries() {
        DeployRegistry registry = new DeployRegistry();
        MockDeploymentDescriptor descriptor2 = new MockDeploymentDescriptor("module2");
        descriptor2.addRequiredModule("module1");
        registry.add(descriptor2);
        assertThat(registry.getPendingEntries()).contains(entry("module2", descriptor2));
    }

    private static DependencyTree.Entry<String, DeploymentDescriptor> entry(String key,
                                                                            DeploymentDescriptor value) {
        return new DependencyTree.Entry<>(key, value);
    }

}

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
package com.alipay.sofa.boot.gradle.plugin;

import org.gradle.testkit.runner.TaskOutcome;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.gradle.junit.GradleCompatibilitySuite;
import org.springframework.boot.gradle.testkit.GradleBuild;

/**
 * Integration tests for the configuration applied by DependencyManagementPlugin
 *
 * @author Andy Wilkinson
 * @author khotyn
 */
@RunWith(GradleCompatibilitySuite.class)
public class DependencyManagementPluginActionIntegrationTests {

    @Rule
    public GradleBuild gradleBuild;

    @Test
    public void bomIsImportedWhenDependencyManagementPluginIsApplied() {
        Assert.assertTrue(this.gradleBuild
            .build("hasDependencyManagement", "-PapplyDependencyManagementPlugin")
            .task(":hasDependencyManagement").getOutcome().equals(TaskOutcome.SUCCESS));
    }
}

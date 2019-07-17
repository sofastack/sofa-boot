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
package org.springframework.boot.gradle.plugin;

import java.util.Objects;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin;
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;

/**
 * {@link Action} that is performed in response to the {@link SofaDependencyManagementPluginAction}
 * being applied.
 * <p>
 * Origin from {@link DependencyManagementPlugin}
 *
 * @author Andy Wilkinson
 * @author khotyn
 */
public class SofaDependencyManagementPluginAction implements PluginApplicationAction {
    @Override
    public Class<? extends Plugin<? extends Project>> getPluginClass() {
        return DependencyManagementPlugin.class;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void execute(Project project) {
        Objects.requireNonNull(project.getExtensions().findByType(DependencyManagementExtension.class))
                .imports((importsHandler) -> importsHandler
                        .mavenBom(SofaBootPlugin.BOM_COORDINATES));
    }
}

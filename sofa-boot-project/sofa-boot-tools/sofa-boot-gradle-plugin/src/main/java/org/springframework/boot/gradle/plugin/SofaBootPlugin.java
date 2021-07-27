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

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolvableDependencies;
import org.gradle.util.GradleVersion;
import org.springframework.boot.gradle.dsl.SpringBootExtension;

import com.alipay.sofa.boot.gradle.plugin.Marker;

/**
 * Gradle plugin for SOFA Boot. (Origin from {@link SpringBootPlugin})
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Andy Wilkinson
 * @author Danny Hyun
 * @author khotyn
 */
public class SofaBootPlugin implements Plugin<Project> {

    private static final String SOFA_BOOT_VERSION                = determineSofaBootVersion();

    /**
     * The name of the {@link Configuration} that contains Spring Boot archives.
     *
     * @since 2.0.0
     */
    private static final String BOOT_ARCHIVES_CONFIGURATION_NAME = "bootArchives";

    /**
     * The coordinates {@code (group:name:version)} of the
     * {@code sofaboot-dependencies} bom.
     */
    static final String         BOM_COORDINATES                  = "com.alipay.sofa:sofaboot-dependencies:"
                                                                   + SOFA_BOOT_VERSION;

    @SuppressWarnings("NullableProblems")
    @Override
    public void apply(Project project) {
        verifyGradleVersion();
        createExtension(project);
        Configuration bootArchives = createBootArchivesConfiguration(project);
        registerPluginActions(project, bootArchives);
        unregisterUnresolvedDependenciesAnalyzer(project);
    }

    private void verifyGradleVersion() {
        GradleVersion currentVersion = GradleVersion.current();
        if (currentVersion.compareTo(GradleVersion.version("5.6")) < 0
            || (currentVersion.getBaseVersion().compareTo(GradleVersion.version("6.0")) >= 0 && currentVersion
                .compareTo(GradleVersion.version("6.3")) < 0)) {
            throw new GradleException(
                "SOFABoot plugin requires Gradle 5 (5.6.x only) or Gradle 6 (6.3 or later). "
                        + "The current version is " + currentVersion);
        }
    }

    private void createExtension(Project project) {
        project.getExtensions().create("springBoot", SpringBootExtension.class, project);
    }

    private Configuration createBootArchivesConfiguration(Project project) {
        Configuration bootArchives = project.getConfigurations().create(
            BOOT_ARCHIVES_CONFIGURATION_NAME);
        bootArchives.setDescription("Configuration for Spring Boot archive artifacts.");
        return bootArchives;
    }

    private void registerPluginActions(Project project, Configuration bootArchives) {
        SinglePublishedArtifact singlePublishedArtifact = new SinglePublishedArtifact(
                bootArchives.getArtifacts());
        List<PluginApplicationAction> actions = Arrays.asList(
                new JavaPluginAction(singlePublishedArtifact),
                new WarPluginAction(singlePublishedArtifact),
                new MavenPluginAction(bootArchives.getUploadTaskName()),
                new SofaDependencyManagementPluginAction(), new ApplicationPluginAction(),
                new KotlinPluginAction());
        for (PluginApplicationAction action : actions) {
            Class<? extends Plugin<? extends Project>> pluginClass = action
                    .getPluginClass();
            if (pluginClass != null) {
                project.getPlugins().withType(pluginClass,
                        (plugin) -> action.execute(project));
            }
        }
    }

    private void unregisterUnresolvedDependenciesAnalyzer(Project project) {
        UnresolvedDependenciesAnalyzer unresolvedDependenciesAnalyzer = new UnresolvedDependenciesAnalyzer();
        project.getConfigurations().all((configuration) -> {
            ResolvableDependencies incoming = configuration.getIncoming();
            incoming.afterResolve((resolvableDependencies) -> {
                if (incoming.equals(resolvableDependencies)) {
                    unresolvedDependenciesAnalyzer.analyze(configuration
                            .getResolvedConfiguration().getLenientConfiguration()
                            .getUnresolvedModuleDependencies());
                }
            });
        });
        project.getGradle().buildFinished(
                (buildResult) -> unresolvedDependenciesAnalyzer.buildFinished(project));
    }

    // This method always returns null when executing gradle test
    // The version of the sofaboot-dependencies is determined by dependency graph resolving
    // As is {@link org.springframework.boot.gradle.plugin.SpringBootPlugin#determineSpringBootVersion}
    private static String determineSofaBootVersion() {
        URL codeSourceLocation = Marker.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            URLConnection connection = codeSourceLocation.openConnection();
            if (connection instanceof JarURLConnection) {
                return getImplementationVersion(((JarURLConnection) connection).getJarFile());
            }
            try (JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()))) {
                return getImplementationVersion(jarFile);
            }
        } catch (Exception ex) {
            return null;
        }
    }

    private static String getImplementationVersion(JarFile jarFile) throws IOException {
        return jarFile.getManifest().getMainAttributes()
            .getValue(Attributes.Name.IMPLEMENTATION_VERSION);
    }
}

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
package com.alipay.sofa.boot;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AutoConfigurationMigrationTests {

    private static final String ENABLE_AUTO_CONFIGURATION_KEY = "org.springframework.boot.autoconfigure.EnableAutoConfiguration";

    private static final String SPRING_FACTORIES_LOCATION     = "src/main/resources/META-INF/spring.factories";

    private static final String IMPORTS_LOCATION              = "src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports";

    private static final Path   PROJECT_ROOT                  = Path.of("..");

    @Test
    void modulesWithAutoConfigurationsShouldProvideImportsFile() throws IOException {
        Set<Path> autoConfigurationModules = findMainSourceDirectories(PROJECT_ROOT).stream()
            .map(this::toModuleDirectory)
            .filter(this::containsAutoConfigurationSource)
            .collect(Collectors.toCollection(TreeSet::new));
        Set<Path> importsModules = findFiles(PROJECT_ROOT, IMPORTS_LOCATION).stream()
            .map(this::findModuleDirectory)
            .collect(Collectors.toCollection(TreeSet::new));

        assertThat(importsModules).isEqualTo(autoConfigurationModules);
    }

    @Test
    void autoConfigurationImportsShouldMatchAutoConfigurationClasses() throws IOException {
        for (Path importsFile : findFiles(PROJECT_ROOT, IMPORTS_LOCATION)) {
            Path moduleDirectory = findModuleDirectory(importsFile);
            Set<String> imports = readImports(importsFile);
            Set<String> autoConfigurationClasses = findAutoConfigurationClasses(moduleDirectory
                .resolve("src/main/java"));

            assertThat(imports).as(importsFile.toString()).isEqualTo(autoConfigurationClasses);
        }
    }

    @Test
    void mainSpringFactoriesShouldNotRegisterEnableAutoConfiguration() throws IOException {
        List<Path> springFactoriesFiles = findFiles(PROJECT_ROOT, SPRING_FACTORIES_LOCATION);

        assertThat(springFactoriesFiles).isNotEmpty();
        for (Path springFactoriesFile : springFactoriesFiles) {
            assertThat(readProperties(springFactoriesFile)).as(springFactoriesFile.toString())
                .doesNotContainKey(ENABLE_AUTO_CONFIGURATION_KEY);
        }
    }

    private boolean containsAutoConfigurationSource(Path sourceRoot) {
        try (Stream<Path> files = Files.walk(sourceRoot)) {
            return files.filter(Files::isRegularFile)
                .anyMatch(this::isAutoConfigurationSource);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to inspect " + sourceRoot, ex);
        }
    }

    private Set<String> findAutoConfigurationClasses(Path sourceRoot) throws IOException {
        try (Stream<Path> files = Files.walk(sourceRoot)) {
            return files.filter(Files::isRegularFile)
                .filter(this::isAutoConfigurationSource)
                .map(file -> toClassName(sourceRoot, file))
                .collect(Collectors.toCollection(TreeSet::new));
        }
    }

    private boolean isAutoConfigurationSource(Path file) {
        return file.getFileName().toString().endsWith("AutoConfiguration.java");
    }

    private String toClassName(Path sourceRoot, Path file) {
        String relativePath = sourceRoot.relativize(file).toString();
        return relativePath.substring(0, relativePath.length() - ".java".length())
            .replace('/', '.').replace('\\', '.');
    }

    private Set<String> readImports(Path importsFile) throws IOException {
        return Files.readAllLines(importsFile, StandardCharsets.UTF_8).stream()
            .map(String::trim)
            .filter(line -> !line.isEmpty())
            .filter(line -> !line.startsWith("#"))
            .collect(Collectors.toCollection(TreeSet::new));
    }

    private Properties readProperties(Path propertiesFile) throws IOException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(propertiesFile, StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    private Path findModuleDirectory(Path file) {
        Path current = file.toAbsolutePath().normalize();
        while (current != null) {
            if (Files.isDirectory(current.resolve("src/main/java"))) {
                return current;
            }
            current = current.getParent();
        }
        throw new IllegalStateException("Cannot resolve module directory for " + file);
    }

    private Path toModuleDirectory(Path sourceRoot) {
        return sourceRoot.getParent().getParent().getParent();
    }

    private List<Path> findFiles(Path root, String suffix) throws IOException {
        try (Stream<Path> files = Files.walk(root)) {
            return files.filter(Files::isRegularFile)
                .map(Path::toAbsolutePath)
                .map(Path::normalize)
                .filter(path -> path.toString().endsWith(suffix))
                .sorted()
                .collect(Collectors.toList());
        }
    }

    private List<Path> findMainSourceDirectories(Path root) throws IOException {
        try (Stream<Path> files = Files.walk(root)) {
            return files.filter(Files::isDirectory)
                .map(Path::toAbsolutePath)
                .map(Path::normalize)
                .filter(path -> path.toString().endsWith("src/main/java"))
                .sorted()
                .collect(Collectors.toList());
        }
    }
}

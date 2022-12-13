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
package org.springframework.boot.gradle.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gradle.api.JavaVersion;
import org.gradle.api.Rule;
import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.springframework.boot.gradle.testkit.GradleBuild;

/**
 * Custom {@link Suite} that runs tests against multiple versions of Gradle. Test classes
 * using the suite must have a public {@link GradleBuild} field named {@code gradleBuild}
 * and annotated with {@link Rule}.
 *
 * @author Andy Wilkinson
 */
public final class GradleCompatibilitySuite extends Suite {
    public static void main(String[] args) {
        System.out.println(System.getProperty("java.version"));
    }

    private static List<String> GRADLE_VERSIONS = Arrays.asList("7.5.1");

    public GradleCompatibilitySuite(Class<?> clazz) throws InitializationError {
        super(clazz, createRunners(clazz));
    }

    private static List<Runner> createRunners(Class<?> clazz) throws InitializationError {
        List<Runner> runners = new ArrayList<>();
        for (String version : GRADLE_VERSIONS) {
            runners.add(new GradleCompatibilityClassRunner(clazz, version));
        }
        return runners;
    }

    private static final class GradleCompatibilityClassRunner extends BlockJUnit4ClassRunner {

        private final String gradleVersion;

        private GradleCompatibilityClassRunner(Class<?> klass, String gradleVersion)
                                                                                    throws InitializationError {
            super(klass);
            this.gradleVersion = gradleVersion;
        }

        @Override
        protected Object createTest() throws Exception {
            Object test = super.createTest();
            configureTest(test);
            return test;
        }

        private void configureTest(Object test) throws Exception {
            GradleBuild gradleBuild = new GradleBuild();
            if (!"default".equals(this.gradleVersion)) {
                gradleBuild = gradleBuild.gradleVersion(this.gradleVersion);
            }
            test.getClass().getField("gradleBuild").set(test, gradleBuild);
        }

        @Override
        protected String getName() {
            return "Gradle " + this.gradleVersion;
        }

        @Override
        protected String testName(FrameworkMethod method) {
            return method.getName() + " [" + getName() + "]";
        }

    }

}

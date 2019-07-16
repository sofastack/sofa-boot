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
package com.alipay.sofa.isle.test.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author xuanbei
 * @author ruoshan
 * @since 2.6.0
 */
public class SeparateClassLoaderTestRunner extends SpringJUnit4ClassRunner {

    private final SeparateClassLoader separateClassloader = new SeparateClassLoader();

    private Method                    runMethod;
    private Object                    runnerObject;

    public SeparateClassLoaderTestRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        try {
            AddCustomJar customJar = getAddCustomJarAnnotationRecursively(clazz);
            if (customJar != null) {
                for (String jar : customJar.value()) {
                    separateClassloader.addJar(jar);
                }
            }
            Class springJUnit4ClassRunnerClass = separateClassloader
                .loadClass(SpringJUnit4ClassRunner.class.getName());
            Constructor constructor = springJUnit4ClassRunnerClass.getConstructor(Class.class);
            runnerObject = constructor.newInstance(separateClassloader.loadClass(clazz.getName()));
            runMethod = springJUnit4ClassRunnerClass.getMethod("run", RunNotifier.class);

        } catch (Throwable e) {
            throw new InitializationError(e);
        }
    }

    @Override
    public void run(RunNotifier notifier) {
        ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(separateClassloader);
            runMethod.invoke(runnerObject, notifier);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldContextClassLoader);
        }
    }

    public static AddCustomJar getAddCustomJarAnnotationRecursively(Class<?> klass) {
        AddCustomJar addCustomJar = klass.getAnnotation(AddCustomJar.class);

        if (addCustomJar != null || klass == Object.class) {
            return addCustomJar;
        }

        return getAddCustomJarAnnotationRecursively(klass.getSuperclass());
    }

    public static class SeparateClassLoader extends URLClassLoader {
        public SeparateClassLoader() {
            super(((URLClassLoader) getSystemClassLoader()).getURLs(), null);
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.startsWith("org.junit") || name.startsWith("java")
                || name.startsWith("org.apache.logging")) {
                return getSystemClassLoader().loadClass(name);
            }

            return super.loadClass(name);
        }

        void addJar(String jar) {
            super.addURL(SeparateClassLoaderTestRunner.class.getClassLoader().getResource(jar));
        }
    }
}
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
package com.alipay.sofa.test.runner;

import com.alipay.sofa.test.annotation.DelegateToRunner;
import com.alipay.sofa.test.utils.TestModeUtil;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.*;
import org.junit.runner.notification.RunNotifier;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ClassUtils;

/**
 * @author qilong.zql
 * @since 2.3.0
 */
public class SofaJUnit4Runner extends Runner implements Filterable, Sortable {

    private static final String DEFAULT_JUNIT4_RUNNER  = "org.junit.runners.JUnit4";

    private static final String SOFA_ARK_JUNIT4_RUNNER = "com.alipay.sofa.ark.support.runner.ArkJUnit4Runner";

    protected Runner            runner;

    @SuppressWarnings("unchecked")
    public SofaJUnit4Runner(Class<?> klazz) {

        String testRunner = null;
        Class runnerClass = null;

        DelegateToRunner annotation = AnnotationUtils.getAnnotation(klazz, DelegateToRunner.class);

        if (annotation != null) {
            runnerClass = annotation.value();
        } else if (TestModeUtil.isArkMode()) {
            testRunner = getArkModeRunner();
        } else {
            testRunner = getDefaultRunner();
        }

        try {
            if (runnerClass == null) {
                runnerClass = ClassUtils.getDefaultClassLoader().loadClass(testRunner);
            }

            if (TestModeUtil.isArkMode()
                && SpringJUnit4ClassRunner.class.isAssignableFrom(runnerClass)) {
                throw new RuntimeException(
                    String
                        .format(
                            "As TestRunner is %s, dependency of sofa-ark-springboot-starter should be removed from classpath!",
                            runnerClass.getCanonicalName()));
            }

            runner = (Runner) runnerClass.getConstructor(Class.class).newInstance(klazz);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Description getDescription() {
        return runner.getDescription();
    }

    @Override
    public void run(RunNotifier notifier) {
        runner.run(notifier);
    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        ((Filterable) runner).filter(filter);
    }

    @Override
    public void sort(Sorter sorter) {
        ((Sortable) runner).sort(sorter);
    }

    public String getArkModeRunner() {
        return SOFA_ARK_JUNIT4_RUNNER;
    }

    public String getDefaultRunner() {
        return DEFAULT_JUNIT4_RUNNER;
    }
}
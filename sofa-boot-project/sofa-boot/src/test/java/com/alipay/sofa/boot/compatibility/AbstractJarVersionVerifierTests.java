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
package com.alipay.sofa.boot.compatibility;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author huzijie
 * @version AbstractJarVersionVerifierTests.java, v 0.1 2023年08月07日 12:07 PM huzijie Exp $
 */
public class AbstractJarVersionVerifierTests {

    private final MockEnvironment mockEnvironment = new MockEnvironment();

    @Test
    public void testJar() {
        TestJarVersionVerifier verifier = new TestJarVersionVerifier(mockEnvironment);
        assertThat(verifier.verify().isNotCompatible()).isTrue();
    }

    public static class TestJarVersionVerifier extends AbstractJarVersionVerifier {

        public TestJarVersionVerifier(Environment environment) {
            super(environment);
        }

        @Override
        public Collection<CompatibilityPredicate> getJarCompatibilityPredicates() {
            List<CompatibilityPredicate> list = new ArrayList<>();
            list.add(() -> false);
            list.add(() -> true);
            return list;
        }

        @Override
        public String name() {
            return "test jar";
        }

        @Override
        public String doc() {
            return "test doc";
        }

        @Override
        public String enableKey() {
            return "test";
        }
    }
}

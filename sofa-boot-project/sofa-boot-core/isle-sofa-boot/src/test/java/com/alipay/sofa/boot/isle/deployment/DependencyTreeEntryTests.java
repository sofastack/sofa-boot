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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link DependencyTree.Entry}.
 *
 * @author huzijie
 * @version DependencyTreeEntryTests.java, v 0.1 2023年04月06日 7:51 PM huzijie Exp $
 */
public class DependencyTreeEntryTests {

    private DependencyTree.Entry<Integer, String> entry;

    @BeforeEach
    void setUp() {
        entry = new DependencyTree.Entry<>(1, "test");
    }

    @Test
    void isRegistered() {
        assertThat(entry.isRegistered()).isTrue();
        entry = new DependencyTree.Entry<>(2, null);
        assertThat(entry.isRegistered()).isFalse();
    }

    @Test
    void isResolved() {
        assertThat(entry.isResolved()).isTrue();
        entry.addWaitingFor(new DependencyTree.Entry<>(2, "test"));
        assertThat(entry.isResolved()).isFalse();
    }

    @Test
    void addWaitingFor() {
        DependencyTree.Entry<Integer, String> otherEntry = new DependencyTree.Entry<>(2, "test");
        entry.addWaitingFor(otherEntry);
        Set<DependencyTree.Entry<Integer, String>> expected = new HashSet<>();
        expected.add(otherEntry);
        assertThat(entry.getWaitsFor()).isEqualTo(expected);
    }

    @Test
    void removeWaitingFor() {
        DependencyTree.Entry<Integer, String> otherEntry = new DependencyTree.Entry<>(2, "test");
        entry.addWaitingFor(otherEntry);
        entry.removeWaitingFor(otherEntry);
        assertThat(entry.getWaitsFor()).isNull();
    }

    @Test
    void addDependsOnMe() {
        DependencyTree.Entry<Integer, String> otherEntry = new DependencyTree.Entry<>(2, "test");
        entry.addDependsOnMe(otherEntry);
        Set<DependencyTree.Entry<Integer, String>> expected = new HashSet<>();
        expected.add(otherEntry);
        assertThat(entry.getDependsOnMe()).isEqualTo(expected);
    }

    @Test
    void addDependsOnMeWithSelf() {
        assertThrows(IllegalArgumentException.class, () -> entry.addDependsOnMe(entry));
    }

    @Test
    void addDependency() {
        DependencyTree.Entry<Integer, String> otherEntry = new DependencyTree.Entry<>(2, "test");
        entry.addDependency(otherEntry);
        Set<DependencyTree.Entry<Integer, String>> expected = new CopyOnWriteArraySet<>();
        expected.add(otherEntry);
        assertThat(entry.getDependencies()).isEqualTo(expected);
    }

    @Test
    void addDependencyWithSelf() {
        assertThrows(IllegalArgumentException.class, () -> entry.addDependency(entry));
    }

    @Test
    void get() {
        assertThat(entry.get()).isEqualTo("test");
    }

    @Test
    void getKey() {
        assertThat(entry.getKey()).isEqualTo(1);
    }

    @Test
    void equals() {
        assertThat(entry.equals(new DependencyTree.Entry<>(1, "other"))).isTrue();
        assertThat(entry.equals(new DependencyTree.Entry<>(2, "test"))).isFalse();
    }

    @Test
    void hashCodeTest() {
        assertThat(entry.hashCode()).isEqualTo(1);
    }
}

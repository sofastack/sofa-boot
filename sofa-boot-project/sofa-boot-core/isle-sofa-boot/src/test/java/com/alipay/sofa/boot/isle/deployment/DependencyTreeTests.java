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

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link DependencyTree}.
 *
 * @author huzijie
 * @version DependencyTreeTest.java, v 0.1 2022年06月22日 11:45 AM huzijie Exp $
 */
public class DependencyTreeTests {

    private DependencyTree<Integer, String> tree;

    @BeforeEach
    void setUp() {
        tree = new DependencyTree<>();
    }

    @Test
    void add() {
        tree.add(1, "test");
        assertThat(tree.get(1)).isEqualTo("test");
        tree.add(2, "test", Collections.singletonList(1));
        assertThat(tree.get(2)).isEqualTo("test");
        tree.add(3, "test", 1, 2);
        assertThat(tree.get(3)).isEqualTo("test");
        tree.add(4, null);
        assertThat(tree.get(4)).isEqualTo(null);
        tree.add(4, "test");
        assertThat(tree.get(4)).isEqualTo("test");
        tree.add(4, "other");
        assertThat(tree.get(4)).isEqualTo("test");
        assertThat(tree.getEntry(1).getDependencies()).isNull();
        assertThat(tree.getEntry(2).getDependencies()).containsExactly(tree.getEntry(1));
        assertThat(tree.getEntry(3).getDependencies()).containsExactlyInAnyOrder(tree.getEntry(1),
            tree.getEntry(2));
    }

    @Test
    void addWithoutDependencies() {
        tree.add(1, "test");
        assertThat(tree.get(1)).isEqualTo("test");
        tree.add(2, "test");
        assertThat(tree.get(2)).isEqualTo("test");
    }

    @Test
    void remove() {
        tree.add(1, "test");
        tree.add(2, "test", Collections.singletonList(1));
        tree.add(3, "test", 1, 2);
        assertThat(tree.getEntries()).hasSize(3);
        assertThat(tree.getResolvedEntries()).hasSize(3);
        tree.remove(2);
        assertThat(tree.getEntries()).hasSize(2);
        assertThat(tree.getResolvedEntries()).hasSize(1);
        assertThat(tree.getEntry(1).getDependencies()).isNull();
        assertThat(tree.getEntry(3).getDependencies()).contains(tree.getEntry(1));
    }

    @Test
    void unregister() {
        tree.add(1, "test");
        tree.add(2, "test", Collections.singletonList(1));
        assertThat(tree.getEntries()).hasSize(2);
        assertThat(tree.getResolvedEntries()).hasSize(2);
        tree.unregister(tree.getEntry(1));
        assertThat(tree.getEntries()).hasSize(2);
        assertThat(tree.getResolvedEntries()).hasSize(0);
        assertThat(tree.getPendingEntries()).hasSize(1);
    }

    @Test
    void resolve() {
        tree.add(1, "test");
        assertThat(tree.getPendingEntries()).isEmpty();
        assertThat(tree.getEntries()).containsExactly(tree.getEntry(1));
        tree.add(2, "test", Collections.singletonList(3));
        assertThat(tree.getPendingEntries()).hasSize(2);
        tree.add(3, "test");
        assertThat(tree.getPendingEntries()).isEmpty();
    }

    @Test
    void unresolve() {
        tree.add(1, "test");
        tree.add(2, "test", Collections.singletonList(1));
        tree.add(3, "test", 1, 2);
        assertThat(tree.getPendingEntries()).isEmpty();
        tree.unresolve(tree.getEntry(2));
        assertThat(tree.getPendingEntries()).hasSize(1);
        assertThat(tree.getEntries()).containsExactly(tree.getEntry(1), tree.getEntry(2),
            tree.getEntry(3));
    }

    @Test
    void getPendingEntries() {
        tree.add(1, "test");
        tree.add(2, "test", Collections.singletonList(3));
        assertThat(tree.getPendingEntries()).hasSize(2);
        tree.add(3, "test");
        assertThat(tree.getPendingEntries()).isEmpty();
    }

    @Test
    void getPendingObjects() {
        tree.add(1, "test");
        tree.add(2, "test", Collections.singletonList(3));
        assertThat(tree.getPendingObjects()).containsExactly("test", null);
        tree.add(3, "test");
        assertThat(tree.getPendingObjects()).isEmpty();
    }

    @Test
    void getMissingRequirements() {
        tree.add(1, "test", 2);
        tree.add(2, "test", 3);
        assertThat(tree.getMissingRequirements()).hasSize(1);
        tree.add(3, "test");
        assertThat(tree.getMissingRequirements()).isEmpty();
    }

    @Test
    void getResolvedEntries() {
        tree.add(1, "test", 2);
        tree.add(2, "test", 3);
        tree.add(3, "test", 4);
        assertThat(tree.getResolvedEntries()).isEmpty();
        tree.add(4, "test");
        assertThat(tree.getResolvedEntries()).containsExactly(tree.getEntry(4), tree.getEntry(3),
            tree.getEntry(2), tree.getEntry(1));
    }

    @Test
    void getResolvedObjects() {
        tree.add(1, "test", 2);
        tree.add(2, "test", 3);
        tree.add(3, "test", 4);
        assertThat(tree.getResolvedObjects()).isEmpty();
        tree.add(4, "test");
        assertThat(tree.getResolvedObjects()).containsExactly("test", "test", "test", "test");
    }

    @Test
    void clear() {
        tree.add(1, "test", 2);
        tree.add(2, "test", 3);
        tree.add(3, "test");
        assertThat(tree.getEntries()).hasSize(3);
        assertThat(tree.getResolvedEntries()).hasSize(3);
        tree.clear();
        assertThat(tree.getEntries()).isEmpty();
        assertThat(tree.getResolvedEntries()).isEmpty();
    }

    @Test
    void updateDependencies() {
        DependencyTree.Entry<Integer, String> entry1 = new DependencyTree.Entry<>(1, "test");
        DependencyTree.Entry<Integer, String> entry2 = new DependencyTree.Entry<>(2, "test");
        DependencyTree.Entry<Integer, String> entry3 = new DependencyTree.Entry<>(3, "test");

        tree.updateDependencies(entry1, Collections.singletonList(2));
        assertThat(entry1.getDependsOnMe()).isNull();
        assertThat(entry1.getDependencies()).containsExactly(entry2);
        assertThat(entry1.getWaitsFor()).containsExactly(entry2);

        tree.updateDependencies(entry2, Collections.singletonList(3));
        assertThat(entry2.getDependsOnMe()).isNull();
        assertThat(entry2.getDependencies()).containsExactly(entry3);
        assertThat(entry2.getWaitsFor()).containsExactly(entry3);

        tree.updateDependencies(entry3, Collections.singletonList(4));
        assertThat(entry3.getDependsOnMe()).isNull();
        assertThat(tree.getEntry(4)).isNotNull();
        assertThat(tree.get(4)).isNull();
    }

    @Test
    void selfDependencyCheck() {
        assertThatThrownBy(() -> tree.add(1, "Test", 1)).hasMessageContaining("01-12001");
    }
}

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
package com.alipay.sofa.runtime.ext.component;

import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.model.ComponentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.w3c.dom.Element;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ExtensionImpl}.
 *
 * @author huzijie
 * @version ExtensionImplTests.java, v 0.1 2023年04月10日 12:12 PM huzijie Exp $
 */
public class ExtensionImplTests {

    private final ComponentName componentName   = new ComponentName(new ComponentType("extension"),
                                                    "name");

    private final ComponentName componentTarget = new ComponentName(new ComponentType("extension"),
                                                    "target");

    private ExtensionImpl       extension;

    @BeforeEach
    void setUp() {
        extension = new ExtensionImpl(componentName, "point");
    }

    @Test
    void testDispose() {
        extension.dispose();
        assertThat(extension.getElement()).isNull();
        assertThat(extension.getContributions()).isNull();
    }

    @Test
    void testGettersAndSetters() {
        extension.setElement(null);
        assertThat(extension.getElement()).isNull();

        extension.setElement(Mockito.mock(Element.class));
        assertThat(extension.getElement()).isNotNull();

        assertThat(extension.getExtensionPoint()).isEqualTo("point");

        assertThat(extension.getComponentName()).isEqualTo(componentName);

        assertThat(extension.getTargetComponentName()).isNull();

        extension.setTargetComponentName(componentTarget);
        assertThat(extension.getTargetComponentName()).isEqualTo(componentTarget);

        assertThat(extension.getContributions()).isNull();

        Object[] contributions = new Object[] { "contrib1", "contrib2" };
        extension.setContributions(contributions);
        assertThat(extension.getContributions()).isEqualTo(contributions);

        assertThat(extension.getAppClassLoader()).isNull();
    }

    @Test
    void testToString() {
        assertThat(extension.toString()).isEqualTo(
            "ExtensionImpl {target: extension:name, point:point}");
    }
}

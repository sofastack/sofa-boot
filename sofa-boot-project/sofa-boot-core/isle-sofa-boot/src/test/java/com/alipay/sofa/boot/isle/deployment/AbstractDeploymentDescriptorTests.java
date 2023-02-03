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

import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Enumeration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AbstractDeploymentDescriptor}.
 *
 * @author huzijie
 * @version AbstractDeploymentDescriptorTests.java, v 0.1 2023年02月02日 8:02 PM huzijie Exp $
 */
public class AbstractDeploymentDescriptorTests {

    @Test
    public void whiteSpacePath() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        Enumeration<URL> urls = classLoader
            .getResources("white space/" + DeploymentDescriptorConfiguration.SOFA_MODULE_FILE);
        while (urls != null && urls.hasMoreElements()) {
            URL url = urls.nextElement();
            DeploymentDescriptor dd = DeploymentBuilder.build(url, null, null, classLoader);
            assertThat(dd.isSpringPowered()).isTrue();
        }
    }
}

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
package com.alipay.sofa.rpc.boot.test.config;

import com.alipay.sofa.rpc.boot.config.LocalFileConfigurator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link LocalFileConfigurator}.
 *
 * @author liangen
 * @version $Id: LocalFileConfigTest.java, v 0.1 2018年04月17日 下午2:51 liangen Exp $
 */
public class LocalFileConfigTests {

    @Test
    public void checkLocalFileConfigurator() {
        LocalFileConfigurator localFileConfigurator = new LocalFileConfigurator();
        String configA = "local";

        String file = localFileConfigurator.parseConfig(configA);
        assertThat(file).isNull();

        String config = "local:///home/admin/registry";

        file = localFileConfigurator.parseConfig(config);
        assertThat("/home/admin/registry").isEqualTo(file);
    }

    @Test
    public void parseConfig() {
        LocalFileConfigurator localFileConfigurator = new LocalFileConfigurator();
        String result = localFileConfigurator.parseConfig("local://xxx");
        assertThat("xxx").isEqualTo(result);
    }
}

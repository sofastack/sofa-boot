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
package com.alipay.sofa.boot.tracer.mongodb;

import com.alipay.sofa.tracer.plugins.mongodb.SofaTracerCommandListener;
import com.mongodb.MongoClientSettings;
import com.mongodb.event.CommandListener;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaTracerCommandListenerCustomizer}.
 *
 * @author huzijie
 * @version SofaTracerCommandListenerCustomizerTests.java, v 0.1 2023年01月09日 7:46 PM huzijie Exp $
 */
public class SofaTracerCommandListenerCustomizerTests {

    @Test
    public void customize() {
        SofaTracerCommandListenerCustomizer commandListenerCustomizer = new SofaTracerCommandListenerCustomizer();
        commandListenerCustomizer.setAppName("testApp");
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        commandListenerCustomizer.customize(builder);
        MongoClientSettings mongoClientSettings = builder.build();
        List<CommandListener> listeners = mongoClientSettings.getCommandListeners();
        assertThat(listeners).anyMatch(listener -> listener instanceof SofaTracerCommandListener);
    }
}

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
package com.alipay.sofa.tracer.test.mongodb;

import com.alipay.sofa.boot.tracer.mongodb.SofaTracerMongoClientSettingsBuilderCustomizer;
import com.alipay.sofa.tracer.plugins.mongodb.SofaTracerCommandListener;
import com.mongodb.MongoClientSettings;
import com.mongodb.event.CommandListener;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author linnan
 * @since 3.9.1
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = "spring.application.name=SofaTracerMongoClientSettingsBuilderCustomizerTest")
public class SofaTracerMongoClientSettingsBuilderCustomizerTest {

    @Autowired
    private SofaTracerMongoClientSettingsBuilderCustomizer sofaTracerMongoClientSettingsBuilderCustomizer;

    @Test
    public void customize() {
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        sofaTracerMongoClientSettingsBuilderCustomizer.customize(builder);
        MongoClientSettings settings = builder.build();
        List<CommandListener> commandListeners = settings.getCommandListeners();
        Assert.assertNotNull(commandListeners);
        Assert.assertFalse(commandListeners.isEmpty());
        CommandListener commandListener = commandListeners.get(commandListeners.size() - 1);
        Assert.assertTrue(commandListener instanceof SofaTracerCommandListener);
    }

    @Configuration(proxyBeanMethods = false)
    static class SofaTracerMongoClientSettingsBuilderCustomizerTestConfiguration {
        @Bean
        public SofaTracerMongoClientSettingsBuilderCustomizer sofaTracerMongoClientSettingsBuilderCustomizer() {
            return new SofaTracerMongoClientSettingsBuilderCustomizer();
        }
    }
}

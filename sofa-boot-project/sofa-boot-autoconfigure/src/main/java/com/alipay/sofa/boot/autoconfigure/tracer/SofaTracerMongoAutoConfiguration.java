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
package com.alipay.sofa.boot.autoconfigure.tracer;

import com.alipay.sofa.tracer.boot.mongodb.SofaTracerMongoClientSettingsBuilderCustomizer;
import com.mongodb.client.MongoClient;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SofaTracerMongoAutoConfiguration
 *
 * @author linnan
 * @since 3.9.1
 **/
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore({ MongoAutoConfiguration.class })
@ConditionalOnClass({ MongoClient.class, SofaTracerMongoClientSettingsBuilderCustomizer.class })
@ConditionalOnProperty(name = "com.alipay.tracer.mongodb.enabled", havingValue = "true", matchIfMissing = true)
public class SofaTracerMongoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    SofaTracerMongoClientSettingsBuilderCustomizer sofaTracerMongoClientSettingsBuilderCustomizer() {
        return new SofaTracerMongoClientSettingsBuilderCustomizer();
    }
}

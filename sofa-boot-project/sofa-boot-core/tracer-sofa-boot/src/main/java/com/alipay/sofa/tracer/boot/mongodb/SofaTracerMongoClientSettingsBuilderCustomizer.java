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
package com.alipay.sofa.tracer.boot.mongodb;

import com.alipay.common.tracer.core.configuration.SofaTracerConfiguration;
import com.alipay.sofa.tracer.plugins.mongodb.SofaTracerCommandListener;
import com.mongodb.MongoClientSettings.Builder;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * @author linnan
 * @since 3.9.1
 */
public class SofaTracerMongoClientSettingsBuilderCustomizer implements
                                                           MongoClientSettingsBuilderCustomizer,
                                                           EnvironmentAware {
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void customize(Builder clientSettingsBuilder) {
        String appName = environment.getProperty(SofaTracerConfiguration.TRACER_APPNAME_KEY);
        SofaTracerCommandListener commandListener = new SofaTracerCommandListener(appName);
        clientSettingsBuilder.addCommandListener(commandListener);
    }
}

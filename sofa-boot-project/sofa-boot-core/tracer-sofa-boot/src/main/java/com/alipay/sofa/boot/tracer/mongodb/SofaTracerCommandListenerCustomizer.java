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
import com.mongodb.MongoClientSettings.Builder;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static com.alipay.common.tracer.core.configuration.SofaTracerConfiguration.TRACER_APPNAME_KEY;

/**
 * Implementation of {@link MongoClientSettingsBuilderCustomizer} to add {@link SofaTracerCommandListener}.
 *
 * @author linnan
 * @author huzijie
 * @since 3.9.1
 */
public class SofaTracerCommandListenerCustomizer implements MongoClientSettingsBuilderCustomizer {

    private String appName;

    @Override
    public void customize(Builder clientSettingsBuilder) {
        Assert.isTrue(StringUtils.hasText(appName), TRACER_APPNAME_KEY + " must be configured!");
        SofaTracerCommandListener commandListener = new SofaTracerCommandListener(appName);
        clientSettingsBuilder.addCommandListener(commandListener);
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}

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
package com.alipay.sofa.boot.actuator.startup;

import com.alipay.sofa.boot.startup.StartupReporter;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.startup.StartupEndpoint;

/**
 * {@link Endpoint @Endpoint} to expose details startup costs.
 *
 * @author Zhijie
 * @since 2020/7/7
 */
@Endpoint(id = "startup")
public class StartupEndPoint {

    private final StartupReporter startupReporter;

    public StartupEndPoint(StartupReporter startupReporter) {
        this.startupReporter = startupReporter;
        this.startupReporter.setStoreStatics(true);
    }

    @ReadOperation
    public StartupReporter.StartupStaticsModel startup() {
        return startupReporter.report();
    }

    @WriteOperation
    public StartupEndpoint.StartupDescriptor startupForSpringBoot() {
        throw new UnsupportedOperationException("Please use GET method instead");
    }
}

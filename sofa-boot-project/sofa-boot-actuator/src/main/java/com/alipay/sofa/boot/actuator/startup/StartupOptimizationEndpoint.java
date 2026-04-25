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

import com.alipay.sofa.boot.startup.BeanInitInfo;
import com.alipay.sofa.boot.startup.StartupOptimizer;
import com.alipay.sofa.boot.startup.StartupReport;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.List;

/**
 * {@link Endpoint @Endpoint} to expose startup optimization analysis.
 *
 * @author OpenAI
 */
@Endpoint(id = "startup-optimization")
public class StartupOptimizationEndpoint {

    private static final String      SLOW_BEANS_OPERATION = "slow-beans";

    private final StartupOptimizer   optimizer;

    private final ApplicationContext applicationContext;

    public StartupOptimizationEndpoint(StartupOptimizer optimizer,
                                       ApplicationContext applicationContext) {
        this.optimizer = optimizer;
        this.applicationContext = applicationContext;
    }

    @ReadOperation
    public StartupReport analyze() {
        return optimizer.analyzeStartupBottlenecks(applicationContext);
    }

    @ReadOperation
    public List<BeanInitInfo> slowBeans(@Selector String operation, @Nullable Integer top) {
        Assert.isTrue(SLOW_BEANS_OPERATION.equals(operation),
            "Only slow-beans operation is supported");
        return optimizer.findSlowBeans(applicationContext, top != null ? top : 10);
    }
}

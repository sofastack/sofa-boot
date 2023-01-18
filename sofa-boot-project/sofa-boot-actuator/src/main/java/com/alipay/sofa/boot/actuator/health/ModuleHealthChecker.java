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
package com.alipay.sofa.boot.actuator.health;

import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Implementation of {@link HealthChecker} used to check sofa modules health.
 *
 * @author xuanbei 18/5/16
 * @author huzijie
 */
public class ModuleHealthChecker implements ApplicationContextAware, HealthChecker {

    public static final String COMPONENT_NAME = "modules";

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Health isHealthy() {
        Health.Builder builder = new Health.Builder();
        ApplicationRuntimeModel application = applicationContext.getBean(
                ApplicationRuntimeModel.APPLICATION_RUNTIME_MODEL_NAME, ApplicationRuntimeModel.class);

        for (DeploymentDescriptor deploymentDescriptor : application.getFailed()) {
            builder.withDetail(deploymentDescriptor.getName(), "failed");
        }

        if (application.getFailed().size() == 0) {
            return builder.status(Status.UP).build();
        } else {
            return builder.status(Status.DOWN).build();
        }
    }

    @Override
    public String getComponentName() {
        return COMPONENT_NAME;
    }

    @Override
    public int getRetryCount() {
        return 0;
    }

    @Override
    public long getRetryTimeInterval() {
        return 1000;
    }

    @Override
    public boolean isStrictCheck() {
        return true;
    }

    @Override
    public int getTimeout() {
        return 10 * 1000;
    }
}

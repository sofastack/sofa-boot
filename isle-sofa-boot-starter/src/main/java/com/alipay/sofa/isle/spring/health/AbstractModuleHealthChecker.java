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
package com.alipay.sofa.isle.spring.health;

import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.constants.SofaModuleFrameworkConstants;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Abstract Module Health Checker
 *
 * @author xuanbei 18/5/16
 */
public abstract class AbstractModuleHealthChecker implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    public Health doHealthCheck() {
        Health.Builder builder = new Health.Builder();
        ApplicationRuntimeModel application = applicationContext.getBean(
            SofaModuleFrameworkConstants.APPLICATION, ApplicationRuntimeModel.class);

        for (DeploymentDescriptor deploymentDescriptor : application.getInstalled()) {
            builder.withDetail(deploymentDescriptor.getName(), "passed");
        }

        for (DeploymentDescriptor deploymentDescriptor : application.getAllInactiveDeployments()) {
            builder.withDetail(deploymentDescriptor.getName(), "inactive");
        }

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
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

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
package com.alipay.sofa.boot.actuator.autoconfigure.startup;

import com.alipay.sofa.boot.actuator.startup.StartupEndPoint;
import com.alipay.sofa.boot.actuator.startup.StartupReporter;
import com.alipay.sofa.boot.actuator.startup.isle.StartupModelCreatingStage;
import com.alipay.sofa.boot.actuator.startup.isle.StartupSpringContextInstallStage;
import com.alipay.sofa.boot.autoconfigure.isle.SofaModuleAutoConfiguration;
import com.alipay.sofa.boot.autoconfigure.isle.SofaModuleProperties;
import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.loader.SpringContextLoader;
import com.alipay.sofa.boot.isle.profile.SofaModuleProfileChecker;
import com.alipay.sofa.boot.isle.stage.ModelCreatingStage;
import com.alipay.sofa.boot.isle.stage.SpringContextInstallStage;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for startup isle components.
 *
 * @author huzijie
 * @version StartupIsleAutoConfiguration.java, v 0.1 2023年01月04日 2:40 PM huzijie Exp $
 */
@AutoConfiguration(before = SofaModuleAutoConfiguration.class)
@ConditionalOnClass({ ApplicationRuntimeModel.class, SofaRuntimeContext.class })
@ConditionalOnProperty(value = "sofa.boot.isle.enabled", matchIfMissing = true)
@ConditionalOnAvailableEndpoint(endpoint = StartupEndPoint.class)
public class StartupIsleAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(value = ModelCreatingStage.class, search = SearchStrategy.CURRENT)
    public StartupModelCreatingStage startupModelCreatingStage(SofaModuleProperties sofaModuleProperties,
                                                               SofaModuleProfileChecker sofaModuleProfileChecker,
                                                               ApplicationRuntimeModel applicationRuntimeModel,
                                                               StartupReporter startupReporter) {
        StartupModelCreatingStage startupModelCreatingStage = new StartupModelCreatingStage(startupReporter);
        startupModelCreatingStage.setApplicationRuntimeModel(applicationRuntimeModel);
        startupModelCreatingStage.setSofaModuleProfileChecker(sofaModuleProfileChecker);
        startupModelCreatingStage.setAllowModuleOverriding(sofaModuleProperties.isAllowModuleOverriding());
        return startupModelCreatingStage;
    }

    @Bean
    @ConditionalOnMissingBean(value = SpringContextInstallStage.class, search = SearchStrategy.CURRENT)
    public StartupSpringContextInstallStage springContextInstallStage(SofaModuleProperties sofaModuleProperties,
                                                                      SpringContextLoader springContextLoader,
                                                                      ApplicationRuntimeModel applicationRuntimeModel,
                                                                      StartupReporter startupReporter) {
        StartupSpringContextInstallStage springContextInstallStage = new StartupSpringContextInstallStage(startupReporter);
        springContextInstallStage.setApplicationRuntimeModel(applicationRuntimeModel);
        springContextInstallStage.setSpringContextLoader(springContextLoader);
        springContextInstallStage.setModuleStartUpParallel(sofaModuleProperties.isModuleStartUpParallel());
        springContextInstallStage.setIgnoreModuleInstallFailure(sofaModuleProperties.isIgnoreModuleInstallFailure());
        return springContextInstallStage;
    }
}

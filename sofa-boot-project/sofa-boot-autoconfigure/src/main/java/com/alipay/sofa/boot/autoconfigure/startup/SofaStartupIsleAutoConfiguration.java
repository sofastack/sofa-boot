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
package com.alipay.sofa.boot.autoconfigure.startup;

import com.alipay.sofa.boot.autoconfigure.isle.SofaModuleAutoConfiguration;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.stage.ModelCreatingStage;
import com.alipay.sofa.isle.stage.SpringContextInstallStage;
import com.alipay.sofa.startup.StartupReporter;
import com.alipay.sofa.startup.stage.isle.StartupModelCreatingStage;
import com.alipay.sofa.startup.stage.isle.StartupSpringContextInstallStage;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * @author huzijie
 * @version SofaStartupIsleAutoConfiguration.java, v 0.1 2021年01月04日 7:07 下午 huzijie Exp $
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(SofaModuleAutoConfiguration.class)
@ConditionalOnClass({ ApplicationRuntimeModel.class, StartupReporter.class })
@ConditionalOnProperty(value = "com.alipay.sofa.boot.enable-isle", matchIfMissing = true)
public class SofaStartupIsleAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = SpringContextInstallStage.class, search = SearchStrategy.CURRENT)
    public StartupSpringContextInstallStage startupSpringContextInstallStage(ApplicationContext applicationContext,
                                                                             StartupReporter startupReporter) {
        return new StartupSpringContextInstallStage(
            (AbstractApplicationContext) applicationContext, startupReporter);
    }

    @Bean
    @ConditionalOnMissingBean(value = ModelCreatingStage.class, search = SearchStrategy.CURRENT)
    public StartupModelCreatingStage startupModelCreatingStage(ApplicationContext applicationContext,
                                                               StartupReporter startupReporter) {
        return new StartupModelCreatingStage((AbstractApplicationContext) applicationContext,
            startupReporter);
    }
}

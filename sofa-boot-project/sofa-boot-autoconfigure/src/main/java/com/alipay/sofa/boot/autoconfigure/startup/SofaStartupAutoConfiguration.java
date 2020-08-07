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

import com.alipay.sofa.startup.SofaStartupContext;
import com.alipay.sofa.startup.SofaStartupProperties;
import com.alipay.sofa.startup.SofaStartupReporter;
import com.alipay.sofa.startup.spring.SpringContextAwarer;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author: Zhijie
 * @since: 2020/7/8
 */
@Configuration
@AutoConfigureBefore(ServletWebServerFactoryAutoConfiguration.class)
@ConditionalOnClass(SofaStartupContext.class)
@EnableConfigurationProperties(SofaStartupProperties.class)
@Import({ SofaStartupConfiguration.StartupTomcat.class,
         SofaStartupConfiguration.StartupJetty.class,
         SofaStartupConfiguration.StartupUndertow.class,
         SofaStartupConfiguration.SpringContextAware.class,
         SofaStartupConfiguration.IsleSpringContextAware.class,
         SofaStartupConfiguration.InstallStage.class })
public class SofaStartupAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SofaStartupContext sofaStartupContext(SpringContextAwarer springContextAwarer,
                                                 SofaStartupProperties sofaStartupProperties) {
        return new SofaStartupContext(springContextAwarer, sofaStartupProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SofaStartupReporter sofaStartupReporter(SofaStartupContext sofaStartupContext) {
        return new SofaStartupReporter(sofaStartupContext);
    }
}

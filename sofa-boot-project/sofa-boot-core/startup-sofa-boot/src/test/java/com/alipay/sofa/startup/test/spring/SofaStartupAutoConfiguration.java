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
package com.alipay.sofa.startup.test.spring;

import com.alipay.sofa.startup.SofaStartupContext;
import com.alipay.sofa.startup.SofaStartupProperties;
import com.alipay.sofa.startup.SofaStartupReporter;
import com.alipay.sofa.startup.spring.SpringContextAwarer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Zhijie
 * @since: 2020/7/8
 */
@Configuration
@EnableConfigurationProperties(SofaStartupProperties.class)
public class SofaStartupAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SpringContextAwarer springContextAwarer() {
        return new SpringContextAwarer();
    }

    @Bean
    public SofaStartupContext sofaStartupContext(SpringContextAwarer springContextAwarer,
                                                 SofaStartupProperties sofaStartupProperties) {
        return new SofaStartupContext(springContextAwarer, sofaStartupProperties);
    }

    @Bean
    public SofaStartupReporter sofaStartupReporter(SofaStartupContext sofaStartupContext) {
        return new SofaStartupReporter(sofaStartupContext);
    }
}

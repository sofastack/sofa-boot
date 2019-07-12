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
package com.alipay.sofa.boot.autoconfigure.tracer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alipay.sofa.tracer.boot.datasource.processor.DataSourceBeanFactoryPostProcessor;
import com.alipay.sofa.tracer.boot.datasource.processor.DataSourceBeanPostProcessor;
import com.alipay.sofa.tracer.boot.datasource.properties.SofaTracerDataSourceProperties;
import com.alipay.sofa.tracer.boot.properties.SofaTracerProperties;
import com.alipay.sofa.tracer.plugins.datasource.SmartDataSource;

/**
 * @author qilong.zql
 * @since 2.2.0
 */
@Configuration
@EnableConfigurationProperties(SofaTracerDataSourceProperties.class)
@ConditionalOnClass({ SofaTracerProperties.class, SmartDataSource.class })
public class SofaTracerDataSourceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "com.alipay.sofa.tracer.datasource", value = "enable", matchIfMissing = true)
    public static DataSourceBeanFactoryPostProcessor DataSourceBeanFactoryPostProcessor() {
        return new DataSourceBeanFactoryPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "com.alipay.sofa.tracer.datasource", value = "enable", matchIfMissing = true)
    public static DataSourceBeanPostProcessor dataSourceBeanPostProcessor() {
        return new DataSourceBeanPostProcessor();
    }
}
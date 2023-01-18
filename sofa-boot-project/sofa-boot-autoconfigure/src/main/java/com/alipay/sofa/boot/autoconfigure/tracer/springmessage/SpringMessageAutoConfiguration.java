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
package com.alipay.sofa.boot.autoconfigure.tracer.springmessage;

import com.alipay.common.tracer.core.configuration.SofaTracerConfiguration;
import com.alipay.sofa.boot.tracer.springmessage.SpringMessageTracerBeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.messaging.DirectWithAttributesChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for spring message.
 *
 * @author guolei.sgl (guolei.sgl@antfin.com) 2019/12/4 10:34 PM
 * @author huzijie
 * @since 3.9.1
 **/
@AutoConfiguration
@ConditionalOnClass({ AbstractMessageChannel.class, ChannelInterceptor.class,
                     DirectWithAttributesChannel.class, SpringMessageTracerBeanPostProcessor.class })
@ConditionalOnProperty(name = "sofa.boot.tracer.springmessage.enabled", havingValue = "true", matchIfMissing = true)
public class SpringMessageAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public static SpringMessageTracerBeanPostProcessor springMessageTracerBeanPostProcessor(Environment environment) {
        String appName = environment.getProperty(SofaTracerConfiguration.TRACER_APPNAME_KEY);
        SpringMessageTracerBeanPostProcessor springMessageTracerBeanPostProcessor = new SpringMessageTracerBeanPostProcessor();
        springMessageTracerBeanPostProcessor.setAppName(appName);
        return springMessageTracerBeanPostProcessor;
    }
}

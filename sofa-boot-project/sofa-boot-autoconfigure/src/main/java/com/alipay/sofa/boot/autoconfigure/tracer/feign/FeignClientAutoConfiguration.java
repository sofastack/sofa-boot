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
package com.alipay.sofa.boot.autoconfigure.tracer.feign;

import com.alipay.sofa.boot.tracer.feign.FeignContextBeanPostProcessor;
import com.alipay.sofa.tracer.plugins.springcloud.instruments.feign.SofaTracerFeignClientFactory;
import feign.Client;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for FeignClient.
 *
 * @author guolei.sgl (guolei.sgl@antfin.com) 2019/3/13 6:04 PM
 * @author huzijie
 **/
@AutoConfiguration(before = FeignAutoConfiguration.class)
@ConditionalOnClass({ Client.class, FeignContextBeanPostProcessor.class,
                     SofaTracerFeignClientFactory.class })
@ConditionalOnProperty(name = "sofa.boot.tracer.feign.enabled", havingValue = "true", matchIfMissing = true)
public class FeignClientAutoConfiguration {

    @Bean
    public static FeignContextBeanPostProcessor feignContextBeanPostProcessor() {
        return new FeignContextBeanPostProcessor();
    }
}

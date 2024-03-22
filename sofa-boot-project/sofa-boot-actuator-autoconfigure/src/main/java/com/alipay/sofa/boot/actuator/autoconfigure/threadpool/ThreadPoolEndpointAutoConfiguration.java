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
package com.alipay.sofa.boot.actuator.autoconfigure.threadpool;

import com.alipay.sofa.boot.actuator.threadpool.ThreadPoolEndpoint;
import com.alipay.sofa.common.thread.ThreadPoolGovernor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link ThreadPoolEndpoint}.
 *
 * @author huzijie
 * @version ThreadPoolEndpointAutoConfiguration.java, v 0.1 2024年03月22日 11:58 huzijie Exp $
 */
@AutoConfiguration
@ConditionalOnAvailableEndpoint(endpoint = ThreadPoolEndpoint.class)
public class ThreadPoolEndpointAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolEndpoint threadPoolEndpoint() {
        return new ThreadPoolEndpoint(ThreadPoolGovernor.getInstance());
    }
}

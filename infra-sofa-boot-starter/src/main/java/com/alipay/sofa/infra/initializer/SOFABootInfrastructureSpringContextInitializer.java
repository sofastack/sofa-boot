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
package com.alipay.sofa.infra.initializer;

import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.infra.log.InfraHealthCheckLoggerFactory;
import com.alipay.sofa.infra.log.space.SofaBootLogSpaceIsolationInit;
import com.alipay.sofa.infra.utils.SOFABootEnvUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

/**
 * SOFABoot Infrastructure 启动初始化器
 *
 * @author yangguanchao
 * @since 2.3.0
 */
public class SOFABootInfrastructureSpringContextInitializer
                                                           implements
                                                           ApplicationContextInitializer<ConfigurableApplicationContext>,
                                                           Ordered {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        //init log
        Environment environment = applicationContext.getEnvironment();
        if (SOFABootEnvUtils.isSpringCloudBootstrapEnvironment(environment)) {
            return;
        }
        String infraLogLevelKey = Constants.LOG_LEVEL_PREFIX
                                  + InfraHealthCheckLoggerFactory.INFRASTRUCTURE_LOG_SPACE;
        SofaBootLogSpaceIsolationInit.initSofaBootLogger(environment, infraLogLevelKey);

        InfraHealthCheckLoggerFactory.getLogger(
            SOFABootInfrastructureSpringContextInitializer.class).info(
            "SOFABoot Infrastructure Starting!");

    }

    @Override
    public int getOrder() {
        //设置为最高优先级
        return HIGHEST_PRECEDENCE;
    }

}

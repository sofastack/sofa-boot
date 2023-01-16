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
package com.alipay.sofa.boot.autoconfigure.ark;

import com.alipay.sofa.boot.ark.SofaFramework;
import com.alipay.sofa.boot.ark.invoke.ArkDynamicServiceProxyManager;
import com.alipay.sofa.boot.autoconfigure.runtime.SofaRuntimeAutoConfiguration;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spi.service.DynamicServiceProxyManager;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for SOFA Ark.
 *
 * @author huzijie
 * @version SofaArkAutoConfiguration.java, v 0.1 2023年01月16日 11:31 AM huzijie Exp $
 */
@AutoConfiguration(after = SofaRuntimeAutoConfiguration.class)
@ConditionalOnClass({ SofaFramework.class, SofaRuntimeContext.class })
public class SofaArkAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SofaFramework sofaFramework(SofaRuntimeManager sofaRuntimeManager) {
        return new SofaFramework(sofaRuntimeManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicServiceProxyManager arkDynamicServiceProxyManager() {
        return new ArkDynamicServiceProxyManager();
    }
}

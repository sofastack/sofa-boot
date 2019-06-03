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
package com.alipay.sofa.rpc.boot.container;

import com.alipay.sofa.rpc.boot.runtime.adapter.helper.ConsumerConfigHelper;
import com.alipay.sofa.rpc.boot.runtime.adapter.helper.ProviderConfigHelper;
import org.springframework.context.ApplicationContext;

/**
 * @author khotyn
 */
public class SpringBridge {
    protected static ApplicationContext applicationContext;

    public static ConsumerConfigHelper getConsumerConfigHelper() {
        return applicationContext.getBean("consumerConfigHelper", ConsumerConfigHelper.class);
    }

    public static ProviderConfigHelper getProviderConfigHelper() {
        return applicationContext.getBean("providerConfigHelper", ProviderConfigHelper.class);
    }

    public static ProviderConfigContainer getProviderConfigContainer() {
        return applicationContext.getBean("providerConfigContainer", ProviderConfigContainer.class);
    }

    public static RegistryConfigContainer getRegistryConfigContainer() {
        return applicationContext.getBean("registryConfigContainer", RegistryConfigContainer.class);
    }

    public static ConsumerConfigContainer getConsumerConfigContainer() {
        return applicationContext.getBean("consumerConfigContainer", ConsumerConfigContainer.class);
    }

    public static ServerConfigContainer getServerConfigContainer() {
        return applicationContext.getBean("serverConfigContainer", ServerConfigContainer.class);
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringBridge.applicationContext = applicationContext;
    }
}

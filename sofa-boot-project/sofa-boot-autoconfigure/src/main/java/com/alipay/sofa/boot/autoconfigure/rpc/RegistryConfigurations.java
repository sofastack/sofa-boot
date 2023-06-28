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
package com.alipay.sofa.boot.autoconfigure.rpc;

/**
 *
 * @author yuanxuan
 * @version : RegistryConfigurations.java, v 0.1 2023年02月01日 16:07 yuanxuan Exp $
 */
public class RegistryConfigurations {

    public static String[] registryConfigurationClass() {
        return new String[] { LocalRegistryConfiguration.class.getName(),
                ZookeeperRegistryConfiguration.class.getName(),
                NacosRegistryConfiguration.class.getName(),
                MulticastRegistryConfiguration.class.getName(),
                MeshRegistryConfiguration.class.getName(),
                ConsulRegistryConfiguration.class.getName(),
                SofaRegistryConfiguration.class.getName() };
    }
}

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
package com.alipay.sofa.rpc.boot.config;

import com.alipay.sofa.common.config.ConfigKey;

/**
 * @author zhaowang
 * @version : SofaBootRpcConfigKeys.java, v 0.1 2020年12月23日 11:21 上午 zhaowang Exp $
 */
public class SofaBootRpcConfigKeys {

    // registry
    public static ConfigKey<String>  DEFAULT_REGISTRY     = ConfigKey
                                                              .build(
                                                                  "sofa.boot.rpc.registry.defaultRegistry",
                                                                  "",
                                                                  false,
                                                                  "默认注册中心实现",
                                                                  new String[] { "default.registry" });

    public static ConfigKey<Boolean> DISABLE_REGISTER_PUB = ConfigKey
                                                              .build(
                                                                  "sofa.boot.rpc.registry.disablePub",
                                                                  false,
                                                                  false,
                                                                  "服务提供方不注册开关",
                                                                  new String[] { "disable_confreg_pub" });

}

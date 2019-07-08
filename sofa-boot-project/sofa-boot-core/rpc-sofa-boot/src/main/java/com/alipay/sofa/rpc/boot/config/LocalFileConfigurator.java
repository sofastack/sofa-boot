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

import com.alipay.sofa.rpc.common.utils.StringUtils;
import com.alipay.sofa.rpc.config.RegistryConfig;

/**
 * 本地注册中心配置
 * 配置格式：com.alipay.sofa.rpc.registry.protocol=local:/home/registry
 *
 * @author liangen
 * @version $Id: LocalFileConfigurator.java, v 0.1 2018年04月17日 下午2:44 liangen Exp $
 */
public class LocalFileConfigurator implements RegistryConfigureProcessor {

    private static String COLON = "://";

    public LocalFileConfigurator() {
    }

    /**
     * 读取配置 key ,获取其 value 进行解析。
     */
    public String parseConfig(String config) {
        String file = null;
        if (StringUtils.isNotEmpty(config) && config.startsWith(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_LOCAL) &&
            config.length() > SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_LOCAL.length()) {
            file = config.substring(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_LOCAL.length() + COLON.length());
        }

        return file;
    }

    @Override
    public RegistryConfig buildFromAddress(String address) {
        String filePath = parseConfig(address);

        if (StringUtils.isEmpty(filePath)) {
            filePath = SofaBootRpcConfigConstants.REGISTRY_FILE_PATH_DEFAULT;
        }

        return new RegistryConfig()
            .setFile(filePath)
            .setProtocol(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_LOCAL);
    }

    @Override
    public String registryType() {
        return SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_LOCAL;
    }

}
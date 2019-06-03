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

import java.util.HashMap;
import java.util.Map;

/**
 * Consul 配置
 * <p>
 * 配置格式: com.alipay.sofa.rpc.registry.address=consul://xxx:8500
 *
 * @author <a href="mailto:zhiyuan.lzy@antfin.com">zhiyuan.lzy</a>
 */
public class ConsulConfigurator implements RegistryConfigureProcessor {

    public ConsulConfigurator() {
    }

    /**
     * 解析配置 value
     *
     * @param config 配置 value
     */
    String parseAddress(String config) {
        String address = null;

        if (StringUtils.isNotEmpty(config) && config.startsWith(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_CONSUL)) {
            final String consulProtocol = SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_CONSUL + "://";
            String value = config.substring(consulProtocol.length());
            if (!value.contains("?")) {
                address = value;
            } else {
                int index = value.lastIndexOf('?');
                address = value.substring(0, index);
            }
        }

        return address;
    }

    /**
     * 传递原始 url
     *
     * @param address
     * @return
     */
    public Map<String, String> parseParam(String address) {

        String host = parseAddress(address);

        //for config ?
        String paramString = address.substring(address.indexOf(host) + host.length());

        if (StringUtils.isNotEmpty(paramString) && paramString.startsWith("?")) {
            paramString = paramString.substring(1);
        }

        Map<String, String> map = new HashMap<String, String>();
        if (paramString.contains("&")) {
            String[] paramSplit = paramString.split("&");
            for (String param : paramSplit) {
                Map<String, String> tempMap = parseKeyValue(param);
                map.putAll(tempMap);
            }
        } else {
            Map<String, String> tempMap = parseKeyValue(paramString);
            map.putAll(tempMap);
        }

        return map;
    }

    private Map<String, String> parseKeyValue(String kv) {
        Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isNotEmpty(kv)) {
            String[] kvSplit = kv.split("=");
            String key = kvSplit[0];
            String value = kvSplit[1];
            map.put(key, value);
        }
        return map;
    }

    @Override
    public RegistryConfig buildFromAddress(String address) {
        String consulAddress = parseAddress(address);
        Map<String, String> map = parseParam(address);
        return new RegistryConfig()
            .setAddress(consulAddress)
            .setProtocol(SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_CONSUL)
            .setParameters(map);

    }

    @Override
    public String registryType() {
        return SofaBootRpcConfigConstants.REGISTRY_PROTOCOL_CONSUL;
    }
}
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
package com.alipay.sofa.rpc.boot.common;

import java.util.HashMap;
import java.util.Map;

import com.alipay.sofa.rpc.common.utils.StringUtils;

/**
 *
 * @author JervyShi
 * @version $Id: RegistryParseUtil.java, v 0.1 2018-12-03 17:18 JervyShi Exp $$
 */
public class RegistryParseUtil {

    /**
     * Parse address string.
     *
     * @param config the config 
     * @param protocol the protocol 
     * @return the string
     */
    public static String parseAddress(String config, String protocol) {
        String address = null;

        if (StringUtils.isNotEmpty(config) && config.startsWith(protocol)) {
            final String nacosProtocol = protocol + "://";
            String value = config.substring(nacosProtocol.length());
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
     * Parse param map.
     *
     * @param address the address 
     * @param protocol the protocol 
     * @return the map
     */
    public static Map<String, String> parseParam(String address, String protocol) {

        String host = parseAddress(address, protocol);

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

    /**
     * Parse key value map.
     *
     * @param kv the kv 
     * @return the map
     */
    public static Map<String, String> parseKeyValue(String kv) {
        Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isNotEmpty(kv)) {
            String[] kvSplit = kv.split("=");
            String key = kvSplit[0];
            String value = kvSplit[1];
            map.put(key, value);
        }
        return map;
    }
}

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
package com.alipay.sofa.rpc.boot.log;

import com.alipay.sofa.common.log.LoggerSpaceManager;

/**
 *
 * SOFABoot RPC日志工程
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class SofaBootRpcLoggerFactory {

    /**
     * 日志命名空间。对应的日志路径配置 com/alipay/sofa/rpc/boot/log
     */
    public static final String RPC_LOG_SPACE = "com.alipay.sofa.rpc.boot";

    /**
     * 获取Logger
     * @param clazz the clazz
     * @return ths Logger
     */
    public static org.slf4j.Logger getLogger(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return getLogger(clazz.getCanonicalName());
    }

    /**
     * 获取Logger
     * @param name the name
     * @return the Logger
     */
    public static org.slf4j.Logger getLogger(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return LoggerSpaceManager.getLoggerBySpace(name, RPC_LOG_SPACE);
    }
}
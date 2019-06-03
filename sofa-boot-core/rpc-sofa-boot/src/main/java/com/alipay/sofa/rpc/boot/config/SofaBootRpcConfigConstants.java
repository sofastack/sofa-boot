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

import com.alipay.sofa.rpc.common.SystemInfo;

/**
 * SOFABoot RPC 配置相关常量
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class SofaBootRpcConfigConstants {
    /* application name */
    public static final String  APP_NAME                           = "spring.application.name";

    /* default config value start ********************************************************/
    public static final int     BOLT_PORT_DEFAULT                  = 12200;
    public static final int     H2C_PORT_DEFAULT                   = 12300;

    /* rest default configuration */
    public static final int     REST_PORT_DEFAULT                  = 8341;
    public static final int     REST_IO_THREAD_COUNT_DEFAULT       = SystemInfo.getCpuCores() * 2;
    public static final int     REST_EXECUTOR_THREAD_COUNT_DEFAULT = 200;
    public static final int     REST_MAX_REQUEST_SIZE_DEFAULT      = 1024 * 1024 * 10;
    public static final boolean REST_TELNET_DEFAULT                = true;
    public static final boolean REST_DAEMON_DEFAULT                = true;

    /* dubbo default configuration */
    public static final int     DUBBO_PORT_DEFAULT                 = 20880;

    /* registry default configuration */
    public static final String  REGISTRY_FILE_PATH_DEFAULT         = System.getProperty("user.home")
                                                                       + System.getProperty(
                                                                           "file.separator")
                                                                       + "localFileRegistry"
                                                                       + System.getProperty(
                                                                           "file.separator")
                                                                       + "localRegistry.reg";

    /* possible config value start ********************************************************/

    /* registry */
    public static final String  REGISTRY_PROTOCOL_LOCAL            = "local";
    public static final String  REGISTRY_PROTOCOL_ZOOKEEPER        = "zookeeper";
    public static final String  REGISTRY_PROTOCOL_MESH             = "mesh";

    //@since 5.5.0
    public static final String  REGISTRY_PROTOCOL_CONSUL           = "consul";

    public static final String  REGISTRY_PROTOCOL_NACOS            = "nacos";
    //@since 5.5.2
    public static final String  REGISTRY_PROTOCOL_SOFA             = "sofa";

    /* server */
    public static final String  RPC_PROTOCOL_BOLT                  = "bolt";
    public static final String  RPC_PROTOCOL_REST                  = "rest";
    public static final String  RPC_PROTOCOL_DUBBO                 = "dubbo";
    public static final String  RPC_PROTOCOL_H2C                   = "h2c";

    /** mesh **/
    public static final String  ENABLE_MESH_ALL                    = "all";

    public static final String  DEFAULT_REGISTRY                   = "default.registry";

    /* possible config value end ********************************************************/

}
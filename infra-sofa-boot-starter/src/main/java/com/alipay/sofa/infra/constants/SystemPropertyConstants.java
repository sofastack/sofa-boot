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
package com.alipay.sofa.infra.constants;

import java.util.HashSet;
import java.util.Set;

/**
 * 改类中的静态变量的值为 SOFABoot 初始化时默认会塞入系统属性中的 key 值,
 * SOFABoot 在初始化时，如发现配置源(SOFABoot 为 Environment, Embedded 为指定文件的 Properties) 中包含该类中的静态变量，
 * 将会自动将该属性 塞入到系统属性中
 * 后续如有其他功能需要扩展需要向系统属性中写入的 key-value ,直接在该类中添加即可。
 * @author luoguimu123
 * @version $Id: PropertyConstants.java, v 0.1 2017年12月12日 下午2:54 luoguimu123 Exp $
 */
public class SystemPropertyConstants {

    private static final String     MIDDLEWARE_ACCESS_KEY   = "com.antcloud.mw.access";

    private static final String     MIDDLEWARE_SECRET_KEY   = "com.antcloud.mw.secret";

    private static final String     ANTCLOUD_ENDPOINT_KEY   = "com.antcloud.antvip.endpoint";

    private static final String     ANTCLOUD_INSTANCEID_KEY = "com.alipay.instanceid";

    private static final String     ANTCLOUD_ENV_KEY        = "com.alipay.env";

    public static final Set<String> KEYS                    = new HashSet<>();

    static {
        KEYS.add(MIDDLEWARE_ACCESS_KEY);
        KEYS.add(MIDDLEWARE_SECRET_KEY);
        KEYS.add(ANTCLOUD_ENDPOINT_KEY);
        KEYS.add(ANTCLOUD_INSTANCEID_KEY);
        KEYS.add(ANTCLOUD_ENV_KEY);
    }

}
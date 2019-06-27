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
package com.alipay.sofa.test.utils;

import org.springframework.util.ClassUtils;

/**
 * @author qilong.zql
 * @since 2.3.0
 */
public class TestModeUtil {

    public static final String SOFA_ARK_BOOT_STRAPPER = "com.alipay.sofa.ark.support.startup.SofaArkBootstrap";

    public static boolean isArkMode() {
        try {
            Class clazz = ClassUtils.getDefaultClassLoader().loadClass(SOFA_ARK_BOOT_STRAPPER);
            return clazz != null;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

}
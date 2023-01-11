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
package com.alipay.sofa.boot.log;

import org.slf4j.Logger;

import com.alipay.sofa.common.log.LoggerSpaceManager;

/**
 * @author xuanbei 18/2/28
 */
public class SofaBootLoggerFactory {
    /***
     * sofa runtime log space
     */
    public static final String SOFA_BOOT_LOG_SPACE = "sofa-boot";

    /**
     * get Logger Object
     *
     * @param name name of the logger
     * @return Logger Object
     */
    public static Logger getLogger(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return LoggerSpaceManager.getLoggerBySpace(name, SOFA_BOOT_LOG_SPACE);
    }

    /**
     * get Logger Object
     * @param clazz the returned logger will be named after clazz
     * @return Logger Object
     */
    public static Logger getLogger(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return getLogger(clazz.getCanonicalName());
    }
}
